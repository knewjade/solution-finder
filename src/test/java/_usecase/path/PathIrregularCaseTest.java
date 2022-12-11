package _usecase.path;

import _usecase.ConfigFileHelper;
import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.path.files.OutputFileHelper;
import com.google.common.base.Charsets;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

class PathIrregularCaseTest extends PathUseCaseBaseTest {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void doesNotExistFieldFile() throws Exception {
        // フィールドファイルがない

        ConfigFileHelper.createPatternFile("*p2");

        String command = "path -fp input/not_exist.txt";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot open field file [FinderParseException]")
                .contains("input/not_exist.txt");
    }

    @Test
    void doesNotExistPatternFile() throws Exception {
        // パターンファイルがない

        Field field = FieldFactory.createField("" +
                "XX_____XXX" +
                "XX______XX" +
                "XX____XXXX" +
                "XX_____XXX"
        );

        int height = 4;
        ConfigFileHelper.createFieldFile(field, height);

        String command = "path -pp input/not_exist.txt";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot open patterns file [FinderParseException]")
                .contains("input/not_exist.txt");
    }

    @Test
    void doesNotExistHeightInFieldFile() throws Exception {
        // フィールドファイル内に高さがない

        Field field = FieldFactory.createField("" +
                "XX_____XXX" +
                "XX______XX" +
                "XX____XXXX" +
                "XX_____XXX"
        );

        int height = 4;
        String fieldFileText = FieldView.toString(field, height);
        ConfigFileHelper.createFieldFile(fieldFileText);
        ConfigFileHelper.createPatternFile("*p2");

        String command = "path";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot read clear-line from field file [FinderParseException]");
    }

    @Test
    void invalidFieldDefinitionInFieldFile() throws Exception {
        // フィールドファイルのフィールドの高さが低い
        //    -> 定義がない部分は空白とみなすため、ブロック数のエラーとなる

        Field field = FieldFactory.createField("" +
                "__XX______"
        );

        String fieldFileText = 4 + LINE_SEPARATOR + FieldView.toString(field, 1);
        ConfigFileHelper.createFieldFile(fieldFileText);
        ConfigFileHelper.createPatternFile("*p2");

        String command = "path";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Empty block in field should be multiples of 4: EmptyCount=38 [FinderInitializeException]");
    }

    @Test
    void noBodyInFieldFile() throws Exception {
        // フィールドファイルの中身が空

        ConfigFileHelper.createFieldFile("");
        ConfigFileHelper.createPatternFile("*p2");

        String command = "path";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Should specify clear-line & field-definition in field file [FinderParseException]");
    }

    @Test
    void noBodyInPatternFile() throws Exception {
        // パターンファイルの中身が空

        Field field = FieldFactory.createField("" +
                "XX_____XXX" +
                "XX______XX" +
                "XX____XXXX" +
                "XX_____XXX"
        );

        int height = 4;
        ConfigFileHelper.createFieldFile(field, height);
        ConfigFileHelper.createPatternFile("");

        String command = "path";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Should specify patterns, not allow empty [FinderInitializeException]");
    }

    @Test
    void overFieldHeight() throws Exception {
        // 高さの指定が大きすぎる

        String tetfu = "v115@vhAAgH";
        String command = String.format("path -c 11 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Clear-Line should be 1 <= line <= 10: line=11 [FinderInitializeException]");
    }

    @Test
    void lessPieces() throws Exception {
        // ミノの個数が少なすぎる

        String tetfu = "v115@vhAAgH";
        String command = String.format("path -c 4 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Should specify equal to or more than 10 pieces: CurrentPieces=2 [FinderInitializeException]");
    }

    @Test
    void testAppendToError() throws Exception {
        // エラーファイルが上書きされることを確認する
        //    -> テスト2つを連続で実行して確認

        overFieldHeight();
        lessPieces();
    }

    @Test
    void noTetfuOptionValue() throws Exception {
        // オプションの指定値がない: テト譜

        String command = "path -c 4 -t -p *p2";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Should specify option value: --tetfu [FinderParseException]");
    }

    @Test
    void lowClearLineOptionValue() throws Exception {
        // クリアラインの指定値が小さすぎる

        String tetfu = "v115@vhAAgH";
        String command = String.format("path -c 0 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Clear-Line should be 1 <= line <= 10: line=0 [FinderInitializeException]");
    }

    @Test
    void oldTetfu() throws Exception {
        // テト譜のバージョンが古い

        String tetfu = "v114@vhAAgH";
        String command = String.format("path -c 0 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Unsupported tetfu: data=v114@vhAAgH [FinderParseException]");
    }

    @Test
    void invalidTetfu() throws Exception {
        // テト譜のデータが不正

        String tetfu = "v115@invalid";
        String command = String.format("path -c 0 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot parse tetfu: invalid [FinderParseException]");
    }

    @Test
    void invalidPattern() throws Exception {
        // パターンの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("path -t %s -p *p8", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Pattern syntax error [FinderInitializeException]");
    }

    @Test
    void lessTetfuPage() throws Exception {
        // テト譜のページ指定が小さい

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("path -P 0 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Page of fumen should be 1 <= page: page=0 [FinderParseException]");
    }

    @Test
    void overTetfuPage() throws Exception {
        // テト譜のページ指定が大きい

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("path -P 6 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Page of fumen is over max page: page=6");
    }

    @Test
    void existsDirectoryHavingSameMinimalOutputNameLink() throws Exception {
        // 出力ファイルと同名のディレクトリが存在している

        File minimalDirectory = new File("output/path_minimal.html");

        // noinspection ResultOfMethodCallIgnored
        minimalDirectory.delete();
        // noinspection ResultOfMethodCallIgnored
        minimalDirectory.mkdir();
        while (!minimalDirectory.exists())
            Thread.sleep(200L);

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("path -t %s -P 5 -p *p5 -f link", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot specify directory as output file path: Path=output/path_minimal.html [FinderInitializeException]");

        // noinspection ResultOfMethodCallIgnored
        minimalDirectory.delete();
    }

    @Test
    void existsDirectoryHavingSameMinimalOutputNameCSV() throws Exception {
        // 出力ファイルと同名のディレクトリが存在している

        File minimalDirectory = new File("output/path_minimal.csv");

        // noinspection ResultOfMethodCallIgnored
        minimalDirectory.delete();
        // noinspection ResultOfMethodCallIgnored
        minimalDirectory.mkdir();
        while (!minimalDirectory.exists())
            Thread.sleep(200L);

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("path -t %s -P 5 -p *p5 -f csv", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        System.out.println(minimalDirectory.exists());
        System.out.println(minimalDirectory.isDirectory());
        assertThat(log.getReturnCode())
                .as(log.getOutput())
                .isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot specify directory as output file path: Path=output/path_minimal.csv [FinderInitializeException]");

        // noinspection ResultOfMethodCallIgnored
        minimalDirectory.delete();
    }

    @Test
    void existsDirectoryHavingSameUniqueOutputNameLink() throws Exception {
        // 出力ファイルと同名のディレクトリが存在している

        File uniqueDirectory = new File("output/path_unique.html");

        // noinspection ResultOfMethodCallIgnored
        uniqueDirectory.delete();
        // noinspection ResultOfMethodCallIgnored
        uniqueDirectory.mkdir();
        while (!uniqueDirectory.exists())
            Thread.sleep(200L);

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("path -t %s -P 5 -p *p5 -f link", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot specify directory as output file path: Path=output/path_unique.html [FinderInitializeException]");

        // noinspection ResultOfMethodCallIgnored
        uniqueDirectory.delete();
    }

    @Test
    void existsDirectoryHavingSameUniqueOutputNameCSV() throws Exception {
        // 出力ファイルと同名のディレクトリが存在している

        File uniqueDirectory = new File("output/path_unique.csv");

        // noinspection ResultOfMethodCallIgnored
        uniqueDirectory.delete();
        // noinspection ResultOfMethodCallIgnored
        uniqueDirectory.mkdir();
        while (!uniqueDirectory.exists())
            Thread.sleep(200L);

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("path -t %s -P 5 -p *p5 -f csv", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode())
                .as(log.getOutput())
                .isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot specify directory as output file path: Path=output/path_unique.csv [FinderInitializeException]");

        // noinspection ResultOfMethodCallIgnored
        uniqueDirectory.delete();
    }

    @Test
    void existsDirectoryHavingLogFileNameLink() throws Exception {
        // 出力ファイルと同名のディレクトリが存在している

        File logDirectory = new File("output/log_directory");

        // noinspection ResultOfMethodCallIgnored
        logDirectory.delete();
        // noinspection ResultOfMethodCallIgnored
        logDirectory.mkdir();
        while (!logDirectory.exists())
            Thread.sleep(200L);

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("path -t %s -P 5 -p *p5 -f link -lp output/log_directory", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot specify directory as output file path: Path=output/log_directory [FinderInitializeException]");

        // noinspection ResultOfMethodCallIgnored
        logDirectory.delete();
    }

    @Test
    void existsDirectoryHavingLogFileNameCSV() throws Exception {
        // 出力ファイルと同名のディレクトリが存在している

        File logDirectory = new File("output/log_directory");

        // noinspection ResultOfMethodCallIgnored
        logDirectory.delete();
        // noinspection ResultOfMethodCallIgnored
        logDirectory.mkdir();
        while (!logDirectory.exists())
            Thread.sleep(200L);

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("path -t %s -P 5 -p *p5 -f csv -lp output/log_directory", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot specify directory as output file path: Path=output/log_directory [FinderInitializeException]");

        // noinspection ResultOfMethodCallIgnored
        logDirectory.delete();
    }

    @Test
    void lessCachedMinBit() throws Exception {
        // Cached-min-bitの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("path -t %s -p *p5 -cb -1", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cached-min-bit should be 0 <= bit: bit=-1 [FinderInitializeException]");
    }

    @Test
    void invalidFormat() throws Exception {
        // formatの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("path -t %s -p *p5 -f INVALID", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Unsupported format: format=INVALID [FinderParseException]");
    }

    @Test
    void invalidHold() throws Exception {
        // holdの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("path -t %s -p *p5 -H INVALID", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot parse hold option: value=INVALID [FinderParseException]");
    }

    @Test
    void unsupportedConsoleOutput() throws Exception {
        String command = "path -t v115@9gB8HeC8GeD8FeC8QeAgH -p *! -o -";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isNotEqualTo(0);
        assertThat(log.getError()).contains("Parent directory is not invalid");
    }

    @Test
    void use180RotationWithDefault() throws Exception {
        String command = "path -t v115@HhA8BeA8FeE8CeG8CeB8JeAgH -p *! -d 180 --kicks default";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isNotEqualTo(0);
        assertThat(log.getError()).contains("kicks do not support 180");
    }

    @Test
    void unsupportedFieldFileEncoding() throws Exception {
        int height = 4;
        Charset unexpectedCharset = Charsets.UTF_16;

        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.createFieldFile(FieldFactory.createField(height), height, unexpectedCharset);

        ConfigFileHelper.deletePatternFile();
        ConfigFileHelper.createPatternFile("*p4");

        String command = "path";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isNotEqualTo(0);
        assertThat(log.getError()).contains("File encoding is probably unexpected. solution-finder supports UTF-8.");
    }

    @Test
    void unsupportedPatternFileEncoding() throws Exception {
        int height = 4;
        Charset unexpectedCharset = Charsets.UTF_16;

        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.createFieldFile(FieldFactory.createField(height), height);

        ConfigFileHelper.deletePatternFile();
        ConfigFileHelper.createPatternFile("*p4", unexpectedCharset);

        String command = "path";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isNotEqualTo(0);
        assertThat(log.getError()).contains("File encoding is probably unexpected. solution-finder supports UTF-8.");
    }
}
