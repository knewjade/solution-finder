package _usecase.percent;

import _usecase.ConfigFileHelper;
import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.path.files.OutputFileHelper;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PercentIrregularCaseTest extends PercentUseCaseBaseTest {
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

        String command = "percent -fp input/not_exist.txt";
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

        String command = "percent -pp input/not_exist.txt";
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

        String command = "percent";
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

        String command = "percent";
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

        String command = "percent";
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

        String command = "percent";
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
        String command = String.format("percent -c 25 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Field height should be equal or less than 24: height=25 [IllegalArgumentException]");
    }

    @Test
    void lessPieces() throws Exception {
        // ミノの個数が少なすぎる

        String tetfu = "v115@vhAAgH";
        String command = String.format("percent -c 4 -t %s -p *p2", tetfu);
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

        String command = "percent -c 4 -t -p *p2";
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
        String command = String.format("percent -c 0 -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Clear-Line should be 1 <= line <= 24: line=0 [FinderInitializeException]");
    }

    @Test
    void oldTetfu() throws Exception {
        // テト譜のバージョンが古い

        String tetfu = "v114@vhAAgH";
        String command = String.format("percent -c 0 -t %s -p *p2", tetfu);
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
        String command = String.format("percent -t %s -p *p2", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
        System.out.println(log.getError());
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
        String command = String.format("percent -t %s -p *p8", tetfu);
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
        String command = String.format("percent -P 0 -t %s -p *p2", tetfu);
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
        String command = String.format("percent -P 6 -t %s -p *p2", tetfu);
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
    void existsDirectoryHavingLogFileName() throws Exception {
        // 出力ファイルと同名のディレクトリが存在している

        File logDirectory = new File("output/log_directory");

        // noinspection ResultOfMethodCallIgnored
        logDirectory.mkdir();
        logDirectory.deleteOnExit();

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("percent -t %s -P 5 -p *p5 -lp output/log_directory", tetfu);
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
    void invalidHold() throws Exception {
        // holdの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("percent -t %s -p *p2 -H INVALID", tetfu);
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
    void use180RotationWithDefault() throws Exception {
        String command = "percent -t v115@GhA8DeA8CeB8DeF8DeF8JeAgH -p *! --drop 180 --kicks default";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isNotEqualTo(0);
        assertThat(log.getError()).contains("kicks do not support 180");
    }
}
