package _usecase.util.fig;

import _usecase.ConfigFileHelper;
import _usecase.Log;
import _usecase.OutputFileHelper;
import _usecase.RunnerHelper;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UtilFigIrregularCaseTest extends UtilFigUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void doesNotExistFieldFile() throws Exception {
        // フィールドファイルがない

        String command = "util fig -fp input/not_exist.txt";
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
    void noBodyInFieldFile() throws Exception {
        // フィールドファイルの中身が空

        ConfigFileHelper.createFieldFile("");

        String command = "util fig";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Should specify field-definition in field file [FinderParseException]");
    }

    @Test
    void testAppendToError() throws Exception {
        // エラーファイルが上書きされることを確認する
        //    -> テスト2つを連続で実行して確認

        noBodyInFieldFile();
        noTetfuOptionValue();
    }

    @Test
    void noTetfuOptionValue() throws Exception {
        // オプションの指定値がない: テト譜

        String command = "util fig -t";
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
    void oldTetfu() throws Exception {
        // テト譜のバージョンが古い

        String tetfu = "v114@vhAAgH";
        String command = String.format("util fig -t %s", tetfu);
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
    void oldTetfuInFieldFile() throws Exception {
        // フィールドファイル内のテト譜のバージョンが古い

        ConfigFileHelper.createFieldFile("v114@vhAAgH");

        String command = "util fig";
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
        String command = String.format("util fig -t %s", tetfu);
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
    void invalidTetfuInFieldFile() throws Exception {
        // フィールドファイル内のテト譜が不正

        ConfigFileHelper.createFieldFile("v115@invalid");

        String command = "util fig";
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
    void lessTetfuStartPage() throws Exception {
        // テト譜の開始ページ指定が小さい

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("util fig -t %s -s 0", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Tetfu-start-page should be 1 <= page: StartPage=0 [FinderParseException]");
    }

    @Test
    void overTetfuStartPage() throws Exception {
        // テト譜の開始ページ指定が大きい

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("util fig -t %s -s 6", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Tetfu-start-page is over max page: StartPage=6");
    }

    @Test
    void lessTetfuEndPage() throws Exception {
        // テト譜の終了ページ指定が小さい

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("util fig -t %s -s 2 -e 1", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Tetfu-end-page should be 2 <= page: EndPage=1 [FinderParseException]");
    }

    @Test
    void overTetfuEndPage() throws Exception {
        // テト譜の終了ページ指定が大きい

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("util fig -t %s -e 6", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Tetfu-end-page is over max page: EndPage=6");
    }

    @Test
    void existsDirectoryHavingSameOutputName() throws Exception {
        // 出力ファイルと同名のディレクトリが存在している

        File FigGifDirectory = new File("output/fig.gif");

        // noinspection ResultOfMethodCallIgnored
        FigGifDirectory.mkdir();
        FigGifDirectory.deleteOnExit();

        String tetfu = "v115@vhEKJJUqB0fBetBpoB";
        String command = String.format("util fig -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot specify directory as output file path: Output=output/fig.gif [FinderInitializeException]");

        // noinspection ResultOfMethodCallIgnored
        FigGifDirectory.delete();
    }

    @Test
    void invalidFormat() throws Exception {
        // formatの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("util fig -t %s -F INVALID", tetfu);
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
    void invalidFrame() throws Exception {
        // frameの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("util fig -t %s -f INVALID", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Unsupported frame: frame=INVALID [FinderParseException]");
    }

    @Test
    void zeroLineOptionValue() throws Exception {
        // ラインの指定値が0

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("util fig -t %s -l 0", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Line should be positive or -1: line=0 [FinderParseException]");
    }

    @Test
    void noIntegerLineOptionValue() throws Exception {
        // ラインの指定値が数字でない

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("util fig -t %s -l x", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot parse line option: value=x [FinderParseException]");
    }

    @Test
    void lowDelayOptionValue() throws Exception {
        // ディレイの指定値が小さすぎる

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("util fig -t %s -d -1", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Delay should be positive: delay=-1 [FinderParseException]");
    }

    @Test
    void invalidHold() throws Exception {
        // holdの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("util fig -t %s -H INVALID", tetfu);
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
    void invalidLoop() throws Exception {
        // loopの指定値が不正

        String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";
        String command = String.format("util fig -t %s -L INVALID", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(1);

        assertThat(log.getError())
                .contains(ErrorMessages.failPreMain());

        assertThat(OutputFileHelper.existsErrorText()).isTrue();

        String errorFile = OutputFileHelper.loadErrorText();
        assertThat(errorFile)
                .contains(command)
                .contains("Cannot parse loop option: value=INVALID [FinderParseException]");
    }
}
