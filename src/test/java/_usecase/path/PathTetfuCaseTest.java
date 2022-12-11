package _usecase.path;

import _usecase.ConfigFileHelper;
import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.path.files.OutputFileHelper;
import _usecase.path.files.PathHTML;
import core.field.FieldFactory;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PathTetfuCaseTest extends PathUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void useTetfuOnly1() throws Exception {
        // テト譜 + パターンコメント

            /*
            comment: 4 -p *p7
            XX________
            XXX_______
            XXXXX_____
            XXXXXX____
             */

        int height = 4;
        String tetfu = "v115@9gB8HeC8GeE8EeF8NeAgWMA0no2ANI98AQPcQB";

        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(35))
                .contains(Messages.minimalCount(29))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(35, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(35)
                .contains("9gB8Bthlwwi0C8BtglxwR4E8glwwR4g0F8zhJeAgWG?AJNWWC6/AAA")
                .contains("9gB8Bti0hlwhC8BtRpwwglwhE8RpxwwhF8g0wwglwh?JeAgWGAp+KWC6/AAA")
                .contains("9gB8Bthlwwi0C8BtglzhE8glxwR4F8wwR4g0JeAgWG?AT+TFD0/AAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(35)
                .allMatch(coloredField -> isFilled(height, coloredField));

        assertThat(uniqueHTML)
                .returns(5040, PathHTML::sequence)
                .returns(35, path -> path.allFumens().size());

        assertThat(uniqueHTML.getHtml())
                .contains("I-Left T-Right S-Left L-Left Z-Spawn J-Reverse")
                .contains("28.9 %")
                .contains("[1456]")
                .contains("O-Spawn Z-Spawn I-Spawn S-Spawn T-Reverse L-Reverse")
                .contains("25.4 %")
                .contains("[1280]");

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(29, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens()).isEmpty();

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(29)
                .contains("9gB8BtzhhlC8Btg0RpwwglE8i0xwF8RpwwglJeAgWG?A0vKWCa+AAA")
                .contains("9gB8Bti0glR4C8BtzhwwE8ilxwF8g0R4wwJeAgWGAU?ejWCz/AAA")
                .contains("9gB8Bti0hlwhC8BtQ4g0wwglwhE8R4xwwhF8Q4wwgl?whJeAgWGAp+TWC6/AAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(29)
                .allMatch(coloredField -> isFilled(height, coloredField));

        assertThat(minimalHTML)
                .returns(5040, PathHTML::sequence)
                .returns(29, path -> path.allFumens().size());

        assertThat(minimalHTML.getHtml())
                .contains("I-Left T-Right S-Left L-Left Z-Spawn J-Reverse")
                .contains("28.9 %")
                .contains("[1456]")
                .contains("O-Spawn Z-Spawn I-Spawn S-Spawn T-Reverse L-Reverse")
                .contains("25.4 %")
                .contains("[1280]");
    }

    @Test
    void useTetfuOnly1_p6() throws Exception {
        // テト譜 + パターンコメント (コマンド優先)

            /*
            comment: 4 -p *p7
            XX________
            XXX_______
            XXXXX_____
            XXXXXX____
             */

        int height = 4;
        String tetfu = "v115@9gB8HeC8GeE8EeF8NeAgWMA0no2ANI98AQPcQB";

        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -t %s -p T,[^T]p5", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("T,[^T]p5")
                .contains(Messages.uniqueCount(35))
                .contains(Messages.minimalCount(20))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(35, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(35)
                .contains("9gB8Bthlwwi0C8BtglxwR4E8glwwR4g0F8zhJeAgWG?AJNWWC6/AAA")
                .contains("9gB8Bti0hlwhC8BtRpwwglwhE8RpxwwhF8g0wwglwh?JeAgWGAp+KWC6/AAA")
                .contains("9gB8Bthlwwi0C8BtglzhE8glxwR4F8wwR4g0JeAgWG?AT+TFD0/AAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(35)
                .allMatch(coloredField -> isFilled(height, coloredField));

        assertThat(uniqueHTML)
                .returns(720, PathHTML::sequence)
                .returns(35, path -> path.allFumens().size());

        assertThat(uniqueHTML.getHtml())
                .contains("T-Left S-Spawn L-Left J-Spawn Z-Spawn I-Spawn")
                .contains("15.0 %")
                .contains("[108]")
                .contains("S-Spawn T-Right L-Left J-Reverse Z-Spawn I-Spawn")
                .contains("14.4 %")
                .contains("[104]");

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(20, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens()).isEmpty();

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(20)
                .contains("9gB8ywQ4BtRpC8wwzhRpE8R4i0F8Q4Btg0JeAgWGAK?X9wCzXBAA")
                .contains("9gB8zhBthlC8i0wwBtglE8g0xwR4F8wwR4glJeAgWG?AzuzPCM+AAA")
                .contains("9gB8BthlzhC8Btgli0wwE8glRpxwF8Rpg0wwJeAgWG?AvOUPCa+AAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(20)
                .allMatch(coloredField -> isFilled(height, coloredField));

        assertThat(minimalHTML)
                .returns(720, PathHTML::sequence)
                .returns(20, path -> path.allFumens().size());

        assertThat(minimalHTML.getHtml())
                .contains("T-Left S-Spawn L-Left J-Spawn Z-Spawn I-Spawn")
                .contains("15.0 %")
                .contains("[108]")
                .contains("O-Spawn T-Left L-Left J-Reverse Z-Spawn I-Spawn")
                .contains("14.4 %")
                .contains("[104]");
    }

    @Test
    void useTetfuOnly2() throws Exception {
        // テト譜 (フィールドファイルは無視) + パターンコメント

            /*
            comment: 3 -p [OSZ]p3
            ____XXX__X
            X__XXXXXXX
            X__XXXX__X
             */

        int height = 3;
        String tetfu = "v115@LhC8BeB8BeH8BeD8BeA8JeAgWUAzno2ANI98AwXfzB?Pt7SAV28CB";

        ConfigFileHelper.createFieldFile(FieldFactory.createField(3), height);
        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("[OSZ]p3")
                .contains(Messages.uniqueCount(2))
                .contains(Messages.minimalCount(2));

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(2, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(2)
                .contains("HhBtR4C8RpB8R4H8BtD8RpA8JeAgWDATnDCA")
                .contains("HhBtR4C8RpB8BtH8R4D8RpA8JeAgWDAa3zBA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(2)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(2, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens()).isEmpty();

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(2)
                .contains("HhBtR4C8RpB8R4H8BtD8RpA8JeAgWDATnDCA")
                .contains("HhBtR4C8RpB8BtH8R4D8RpA8JeAgWDAa3zBA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(2)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void useTetfuOnly3() throws Exception {
        // テト譜 + パターンコメント (パターンファイルを無視)

            /*
            comment: 4
            XX________
            XXX_______
            XXX_____XX
            XXXX____XX
             */

        int height = 4;
        String tetfu = "v115@9gB8HeC8GeC8EeF8DeB8JeAgWBAUAAAA";

        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -t %s -p *p7", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(54))
                .contains(Messages.minimalCount(35));

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(54, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(54)
                .contains("9gB8i0hlwhRpC8Q4g0wwglwhRpC8R4xwwhF8Q4wwgl?whB8JeAgWGAJNWWCv/AAA")
                .contains("9gB8zhhlRpC8Q4ywglRpC8R4BtglF8Q4wwBtB8JeAg?WGAa9KWCU+AAA")
                .contains("9gB8ywBti0C8zhR4g0C8wwRpR4F8RpBtB8JeAgWGAP?dNPC0XBAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(54)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(35, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(35)
                .contains("9gB8hlwwR4AtRpC8glxwBtRpC8glwwi0F8R4Atg0B8?JeAgWGAKHWWCaNBAA")
                .contains("9gB8hlwwi0RpC8glxwQ4g0RpC8glBtR4F8wwBtQ4B8?JeAgWGAa9KWC0/AAA")
                .contains("9gB8BtwwR4i0C8Btzhg0C8ywRpF8R4RpB8JeAgWGAP?ezPCUNBAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(35)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void useTetfuOnly4Split() throws Exception {
        // テト譜 + パターンコメント (テト譜splitをオン)

            /*
            comment: <Empty>
            ______XXXX
            _______XXX
            _____XXXXX
            ______XXXX
             */

        int height = 4;
        String tetfu = "v115@DhD8GeC8EeE8FeD8JeAgH";

        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -c 4 -p *p7 -s yes -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(83))
                .contains(Messages.minimalCount(68))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(83, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(83)
                .contains("DhD8GeC8EeE8FeD8JeVQYZAFLDmClcJSAVDEHBEooR?BUoAVBPtPFDpAAAAvhETpB3lBmpB0rBxuB")
                .contains("DhD8GeC8EeE8FeD8Je9KYZAFLDmClcJSAVDEHBEooR?BUoAVBziHgCpAAAAvhE3kBimB2uBTrBxuB")
                .contains("DhD8GeC8EeE8FeD8JexOYZAFLDmClcJSAVDEHBEooR?BJoAVBz/dgCsAAAAvhE3kBmqBtrBzqBipB")
                .contains("DhD8GeC8EeE8FeD8Je3KYZAFLDmClcJSAVDEHBEooR?BToAVBU+TFDvAAAAvhE1mBxkBypBUrBTpB")
                .contains("DhD8GeC8EeE8FeD8Je0JYZAFLDmClcJSAVDEHBEooR?BaoAVB0PltCvAAAAvhElqBOpBRrBXlBTrB");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(83)
                .allMatch(coloredField -> isEmpty(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(68, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens()).isEmpty();

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(68)
                .contains("DhD8GeC8EeE8FeD8JeRPYZAFLDmClcJSAVDEHBEooR?BJoAVB6/dgCsAAAAvhEMpB2qBtrBzqBipB")
                .contains("DhD8GeC8EeE8FeD8Je/JYZAFLDmClcJSAVDEHBEooR?BToAVBqXegCsAAAAvhEmqBUqBtrBzlBipB")
                .contains("DhD8GeC8EeE8FeD8JeTKYZAFLDmClcJSAVDEHBEooR?BPoAVB0vTWCpAAAAvhENpBmlB3rB6pBxvB")
                .contains("DhD8GeC8EeE8FeD8JezKYZAFLDmClcJSAVDEHBEooR?BPoAVBqXWWCpAAAAvhE2uB0kBtrByvBxuB")
                .contains("DhD8GeC8EeE8FeD8JezKYZAFLDmClcJSAVDEHBEooR?BPoAVBT+VWCqAAAAvhE3pBRmBNkBaqBGrB");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(68)
                .allMatch(coloredField -> isEmpty(height, coloredField));
    }

    @Test
    void useTetfuAndPatternsCommand1() throws Exception {
        // テト譜 + パターンコマンド (パターンファイルを無視)

            /*
            comment: 4
            XX________
            XXX_______
            XXX_____XX
            XXXX____XX
             */

        int height = 4;
        String tetfu = "v115@9gB8HeC8GeC8EeF8DeB8JeAgWBAUAAAA";

        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -t %s -p *p7", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(54))
                .contains(Messages.minimalCount(35));

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(54, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(54)
                .contains("9gB8i0hlwhRpC8Q4g0wwglwhRpC8R4xwwhF8Q4wwgl?whB8JeAgWGAJNWWCv/AAA")
                .contains("9gB8zhhlRpC8Q4ywglRpC8R4BtglF8Q4wwBtB8JeAg?WGAa9KWCU+AAA")
                .contains("9gB8ywBti0C8zhR4g0C8wwRpR4F8RpBtB8JeAgWGAP?dNPC0XBAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(54)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(35, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(35)
                .contains("9gB8hlwwR4AtRpC8glxwBtRpC8glwwi0F8R4Atg0B8?JeAgWGAKHWWCaNBAA")
                .contains("9gB8hlwwi0RpC8glxwQ4g0RpC8glBtR4F8wwBtQ4B8?JeAgWGAa9KWC0/AAA")
                .contains("9gB8BtwwR4i0C8Btzhg0C8ywRpF8R4RpB8JeAgWGAP?ezPCUNBAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(35)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void useTetfuAndPatternsCommand2() throws Exception {
        // テト譜 + パターンコマンド (パターンコメントを無視)

            /*
            comment: 4 -p *p2
            XXXX______
            XXXX______
            XXXX______
            XXXX______
             */

        int height = 4;
        String tetfu = "v115@9gD8FeD8FeD8FeD8PeAgWMA0no2ANI98AQPk/A";

        String command = String.format("path -t %s -p *p7", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(245))
                .contains(Messages.minimalCount(157));

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(245, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(2)
                .contains("9gD8ili0D8glAtRpQ4g0D8BtRpR4D8AtzhQ4JeAgWG?AJ3jFDqCBAA")
                .contains("9gD8Q4zhAtD8R4RpBtD8g0Q4RpAtglD8i0ilJeAgWG?AsvaFDT+AAA");

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(243)
                .contains("9gD8h0R4RpD8g0R4ilD8g0wwzhD8ywglRpJeAgWGAU?ujPCMHBAA")
                .contains("9gD8BtQ4ywD8h0R4hlD8g0BtQ4wwglD8g0zhglJeAg?WGApintC6OBAA")
                .contains("9gD8h0ywAtD8zhBtD8g0RpwwAtglD8g0RpilJeAgWG?AMnbMCqOBAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(245)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(157, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(2)
                .contains("9gD8ili0D8glAtRpQ4g0D8BtRpR4D8AtzhQ4JeAgWG?AJ3jFDqCBAA")
                .contains("9gD8Q4zhAtD8R4RpBtD8g0Q4RpAtglD8i0ilJeAgWG?AsvaFDT+AAA");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(155)
                .contains("9gD8ili0D8RpzhD8RpR4wwg0D8glR4ywJeAgWGAU9C?MCqCBAA")
                .contains("9gD8ili0D8glywR4D8RpwwR4g0D8RpzhJeAgWGAJ3T?xCs/AAA")
                .contains("9gD8zhRpD8glQ4BtRpD8glR4i0D8hlQ4Btg0JeAgWG?AqyjFDP+AAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(157)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void useTetfuAndPatternsCommand3() throws Exception {
        // テト譜 (無関係なコメント付き) + パターンコマンド (パターンファイルを無視)

            /*
            comment: 1ページ目: 無関係なコメントです
            XX________
            XXX______X
            XXX_____XX
            XXXX_____X
             */

        int height = 4;
        String tetfu = "v115@9gB8HeC8FeD8EeF8EeA8JeAgWbBxXHDBQGDSA1d0AC?DYHDBQzuRA1Dq9BF4CwBFbcRA1zW9AxXXXB1RhRAV/d3ByX?HDBQxCSA1dUzBzXHDBwHfRA1d0ACzXHDBw0uRA1d0KB3XHD?Bwv4AA";

        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -c 4 -t %s -p *p7", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(118))
                .contains(Messages.minimalCount(96));

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(118, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(6)
                .contains("9gB8hlzhR4C8glh0AtR4D8glg0BtwwF8g0AtywA8Je?AgWGA03ntCM+AAA")
                .contains("9gB8zhQ4ywC8ilR4wwD8glRpg0Q4F8Rpi0A8JeAgWG?AK3TWCU+AAA")
                .contains("9gB8hlzhR4C8glywR4D8glg0wwBtF8i0BtA8JeAgWG?AaNWWCK+AAA");

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(112)
                .contains("9gB8hlwhh0ywC8glwhg0BtwwD8glwhg0R4F8whR4Bt?A8JeAgWGATe/VC6OBAA")
                .contains("9gB8BtR4glywC8R4zhD8Bti0F8ilg0wwA8JeAgWGAK?ujFDsOBAA")
                .contains("9gB8hlzhR4C8glh0ywD8glg0RpwwF8g0RpR4A8JeAg?WGAv/VWCT+AAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(118)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(96, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(6)
                .contains("9gB8zhQ4ywC8ilR4wwD8glRpg0Q4F8Rpi0A8JeAgWG?AK3TWCU+AAA")
                .contains("9gB8hlzhR4C8gli0R4D8glRpg0wwF8RpywA8JeAgWG?AU3jPCM+AAA")
                .contains("9gB8zhh0R4C8ilg0R4D8glRpg0wwF8RpywA8JeAgWG?AU3jPCM+AAA");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(90)
                .contains("9gB8hlzhR4C8glh0ywD8glg0RpwwF8g0RpR4A8JeAg?WGAv/VWCT+AAA")
                .contains("9gB8i0ywR4C8RpglwwR4D8RpglBtF8g0hlBtA8JeAg?WGA6yytC0/AAA")
                .contains("9gB8BtRpwhywC8Btglwhh0D8ilwhg0F8Rpwhg0wwA8?JeAgWGAK+TFDvOBAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(96)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void useTetfuAndPatternsCommand4() throws Exception {
        // テト譜 (無関係なコメント付き) + パターンコマンド (パターンファイルを無視)

            /*
            comment: 日本語開始のコメント
            XXXXXX____
            XXXXXX____
            XXXXXX_X__
            XXXXXXXXX_
             */

        int height = 4;
        String tetfu = "v115@9gF8DeF8DeF8AeA8BeI8KeAgW8AlfrHBFwDfE2Cx2B?l/PwB5HEfE5fmzBlPJVBjDEfET4p9Blvs2ACtDfETor6Alv?s2AGtDfETIPSB";

        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -c 4 -t %s -p *p4", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p4")
                .contains(Messages.uniqueCount(11))
                .contains(Messages.minimalCount(9));

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(11, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(5)
                .contains("9gF8zhF8ilwwF8glA8xwI8wwJeAgWDA0SdBA")
                .contains("9gF8h0R4F8g0R4wwF8g0A8xwI8wwJeAgWDAUtfBA")
                .contains("9gF8wwi0F8xwQ4g0F8wwA8R4I8Q4JeAgWDAzufBA");

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(6)
                .contains("9gF8ilwwF8zhF8glA8xwI8wwJeAgWDApOkBA")
                .contains("9gF8ilwhF8i0whF8glA8g0whI8whJeAgWDAp/jBA")
                .contains("9gF8h0hlF8g0BtglF8g0A8BtI8glJeAgWDA6/jBA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(11)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(9, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(4)
                .contains("9gF8h0R4F8g0R4wwF8g0A8xwI8wwJeAgWDAUtfBA")
                .contains("9gF8wwhlwhF8xwglwhF8wwA8glwhI8whJeAgWDApOk?BA")
                .contains("9gF8h0wwwhF8g0xwwhF8g0A8wwwhI8whJeAgWDApuf?BA")
                .contains("9gF8wwi0F8xwQ4g0F8wwA8R4I8Q4JeAgWDAzufBA");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(5)
                .contains("9gF8i0whF8ilwhF8glA8g0whI8whJeAgWDApifBA")
                .contains("9gF8ilwhF8i0whF8glA8g0whI8whJeAgWDAp/jBA")
                .contains("9gF8h0hlF8g0ywF8g0A8wwglI8glJeAgWDAqOkBA")
                .contains("9gF8h0hlF8g0BtglF8g0A8BtI8glJeAgWDA6/jBA")
                .contains("9gF8wwi0F8xwRpF8wwA8RpI8g0JeAgWDAvufBA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(9)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void useTetfuAndPatternsFile1() throws Exception {
        // テト譜 + パターンファイル

            /*
            comment: 4
            __________
            __XX______
            __XXXXX___
            _XXXXXXXXX
             */

        int height = 4;
        String tetfu = "v115@JhB8HeE8DeI8JeAgWBAUAAAA";

        ConfigFileHelper.createPatternFile("*p7");

        String command = String.format("path -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(10))
                .contains(Messages.minimalCount(6))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(10, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(5)
                .contains("9gilywzhglAtB8wwi0RpBtE8g0RpAtI8JeAgWGAv33?LC0CBAA")
                .contains("9gilywR4RpglAtB8wwR4g0RpBtE8i0AtI8JeAgWGAq?HztC0CBAA")
                .contains("9gwhh0ywR4hlwhg0B8wwR4Rpglwhg0E8RpglwhI8Je?AgWGAP+rtCqOBAA");

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(5)
                .contains("9gwhh0ywR4Rpwhg0B8wwR4ilwhg0E8glRpwhI8JeAg?WGApyjPCUHBAA")
                .contains("9gilBtywR4RpB8Btwwi0RpE8R4g0glI8JeAgWGAvfj?xCzCBAA")
                .contains("9gilBtzhg0RpB8Btywg0RpE8wwh0glI8JeAgWGAv/l?FDM+AAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(10)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(6, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(5)
                .contains("9gilywR4RpglAtB8wwR4g0RpBtE8i0AtI8JeAgWGAq?HztC0CBAA")
                .contains("9gwhh0ywR4hlwhg0B8wwR4Rpglwhg0E8RpglwhI8Je?AgWGAP+rtCqOBAA")
                .contains("9gwhh0Btywhlwhg0B8BtwwRpglwhg0E8RpglwhI8Je?AgWGAP+TFDqOBAA");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(1)
                .contains("9gilywR4Atg0RpB8wwR4Btg0RpE8Ath0glI8JeAgWG?AvfLuC0CBAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(6)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void useTetfuAndPatternsFile2WithoutHold() throws Exception {
        // テト譜 + パターンファイル (ホールドの設定がコメント内)

            /*
            comment: 4 -p I,O,S --hold avoid
            XXXXXX____
            XXXXXXX___
            XXXXXXXX__
            XXXXXXX___
             */

        int height = 4;
        String tetfu = "v115@9gF8DeG8CeH8BeG8MeAgWjA0no2ANI98AwN88ADX88?ADd88AwjdzDPzSTASoikEvuaCA";

        ConfigFileHelper.createPatternFile("S,Z,T");

        String command = String.format("path -t %s -p T,*p3", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("T,*p3")
                .contains(Messages.uniqueCount(9))
                .contains(Messages.minimalCount(8))
                .contains(Messages.noUseHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(9, PathHTML::pattern);

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(3)
                .contains("9gF8zhG8i0H8wwg0G8ywJeAgWDA0PdBA")
                .contains("9gF8BthlG8BtglH8wwglG8ywJeAgWDA0XkBA")
                .contains("9gF8zhG8ywH8wwglG8ilJeAgWDA0SdBA");

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(6)
                .contains("9gF8BtR4G8R4wwH8xwG8BtwwJeAgWDAUtDCA")
                .contains("9gF8zhG8RpwwH8xwG8RpwwJeAgWDAUXdBA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(9)
                .allMatch(coloredField -> isFilled(height, coloredField));

        assertThat(uniqueHTML)
                .returns(42, PathHTML::sequence)
                .returns(9, path -> path.allFumens().size());

        assertThat(uniqueHTML.getHtml())
                .contains("T-Left O-Spawn I-Spawn")
                .contains("4.8 %")
                .contains("[2]")
                .contains("T-Left Z-Spawn S-Spawn")
                .contains("2.4 %")
                .contains("[1]");

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(8, PathHTML::pattern);

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(3)
                .contains("9gF8zhG8i0H8wwg0G8ywJeAgWDA0PdBA")
                .contains("9gF8BthlG8BtglH8wwglG8ywJeAgWDA0XkBA")
                .contains("9gF8zhG8ywH8wwglG8ilJeAgWDA0SdBA");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(5)
                .contains("9gF8hlh0G8glg0wwH8xwG8glg0wwJeAgWDA0/jBA")
                .contains("9gF8BtwwwhG8xwwhH8wwwhG8BtwhJeAgWDAUeDCA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(8)
                .allMatch(coloredField -> isFilled(height, coloredField));

        assertThat(minimalHTML)
                .returns(42, PathHTML::sequence)
                .returns(8, path -> path.allFumens().size());

        assertThat(minimalHTML.getHtml())
                .contains("T-Left O-Spawn I-Spawn")
                .contains("4.8 %")
                .contains("[2]")
                .contains("T-Left Z-Spawn S-Spawn")
                .contains("2.4 %")
                .contains("[1]");
    }

    @Test
    void validSequences() throws Exception {
        // 有効な組み合わせのチェック
            /*
            comment: <Empty>
            _____XXX__
            ___X__X__X
            _XXXXXXXXX
            _XXXXXXXXX
             */

        int height = 4;
        String tetfu = "v115@ChC8EeA8BeA8BeA8AeI8AeI8JeAgH";

        String command = String.format("path -c 4 -t %s -p I,O,S,Z --hold avoid", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("I,O,S,Z")
                .contains(Messages.uniqueCount(1))
                .contains(Messages.minimalCount(1))
                .contains(Messages.noUseHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML.getHtml())
                .contains("I-Left O-Spawn S-Spawn Z-Spawn");

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(1)
                .contains("9gwhRpBtC8R4whRpA8BtA8R4A8whI8whI8JeAgWEAJ?3jFD");

        // ライン消去なし
        assertThat(uniqueHTML.deletedLineFumens()).isEmpty();

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(1)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML.getHtml())
                .contains("I-Left O-Spawn S-Spawn Z-Spawn");

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(1)
                .contains("9gwhRpBtC8R4whRpA8BtA8R4A8whI8whI8JeAgWEAJ?3jFD");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens()).isEmpty();

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(1)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void validSequences2() throws Exception {
        // 有効な組み合わせのチェック
            /*
            comment: <Empty>
            __X_______
            __X_X___X_
             */

        int height = 2;
        String tetfu = "v115@ThA8IeA8AeA8CeA8KeAgH";

        String command = String.format("path -c 2 -t %s -p T,O,L,J", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("T,O,L,J")
                .contains(Messages.uniqueCount(1))
                .contains(Messages.minimalCount(1))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML.getHtml())
                .contains("T-Spawn O-Spawn J-Reverse L-Reverse");

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(1)
                .contains("RhRpA8ilwwi0RpA8glA8ywA8g0JeAgWEAUn/VC");

        // ライン消去なし
        assertThat(uniqueHTML.deletedLineFumens()).isEmpty();

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(1)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML.getHtml())
                .contains("T-Spawn O-Spawn J-Reverse L-Reverse");

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(1)
                .contains("RhRpA8ilwwi0RpA8glA8ywA8g0JeAgWEAUn/VC");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens()).isEmpty();

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(1)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void caseMinimalSpecified() throws Exception {
        String tetfu = "v115@GhA8HeD8DeF8CeE8JeAgWhANI98AwXfzBKOEHBEoA6?AFLHSAVjjFDUHcSAVG88ARAAAA";

        String command = String.format("path -t %s -so no", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
        assertThat(log.getReturnCode()).isEqualTo(0);

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML.getHtml())
                .contains("I-Spawn J-Spawn S-Spawn O-Spawn Z-Spawn T-Reverse");

        assertThat(minimalHTML.allFumens())
                .hasSize(9);
    }

    @Test
    void lastS() throws Exception {
        // 入力パターン以上のミノ順でパフェができていないかをチェック
        // Issue #1
            /*
            comment: <Empty>
            XXX_______
            XX________
            XX_______X
            XXXXX__XXX
             */

        String tetfu = "v115@zgyhGexhHexhGeAtxhC8BeA8BtyhE8AtA8JeAgWBAV?AAAAvhAAAPBAUAAAA";

        String command = String.format("path -t %s -P 2 -p [IJLOS]p5,S --split yes", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("[IJLOS]p5,S")
                .contains(Messages.uniqueCount(10))
                .contains(Messages.minimalCount(8))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML.getHtml())
                .contains("O-Spawn S-Spawn L-Spawn J-Spawn S-Spawn I-Spawn");

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(3)
                .contains("9gC8GeB8HeB8GeF8BeC8JezLYZAFLDmClcJSAVDEHB?EooRBPoAVBzintCpAAAAvhEXtByvBWxB3qBxxB")
                .contains("9gC8GeB8HeB8GeF8BeC8JeXIYZAFLDmClcJSAVDEHB?EooRBToAVBJ3TWCqAAAAvhEZnBTlB/rBCtBGrB")
                .contains("9gC8GeB8HeB8GeF8BeC8JeeMYZAFLDmClcJSAVDEHB?EooRBKoAVBpyytCzAAAAvhExvBqsBTtB3rB3qB");

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(7)
                .contains("9gC8GeB8HeB8GeF8BeC8JeeMYZAFLDmClcJSAVDEHB?EooRBKoAVBpy7tCvAAAAvhExvBStB3mB3lBzsB")
                .contains("9gC8GeB8HeB8GeF8BeC8JeTFYZAFLDmClcJSAVDEHB?EooRBPoAVBzintCpAAAAvhE/rBasBWtBXtBRwB");

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML.getHtml())
                .contains("O-Spawn S-Spawn L-Spawn J-Spawn S-Spawn I-Spawn");

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(3)
                .contains("9gC8GeB8HeB8GeF8BeC8JezLYZAFLDmClcJSAVDEHB?EooRBPoAVBzintCpAAAAvhEXtByvBWxB3qBxxB")
                .contains("9gC8GeB8HeB8GeF8BeC8JeXIYZAFLDmClcJSAVDEHB?EooRBToAVBJ3TWCqAAAAvhEZnBTlB/rBCtBGrB")
                .contains("9gC8GeB8HeB8GeF8BeC8JeeMYZAFLDmClcJSAVDEHB?EooRBKoAVBpyytCzAAAAvhExvBqsBTtB3rB3qB");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(5)
                .contains("9gC8GeB8HeB8GeF8BeC8JeXIYZAFLDmClcJSAVDEHB?EooRBToAVBpvTWCvAAAAvhExqBmmB3lBCoBzrB")
                .contains("9gC8GeB8HeB8GeF8BeC8JezLYZAFLDmClcJSAVDEHB?EooRBPoAVBp/rtCzAAAAvhExsBmqBCnBXtB3qB");
    }

    @Test
    void splitWithFilledLine() throws Exception {
        // 初めからラインが揃っている行がある
        String tetfu = "v115@wghlwhHeglwhEeA8BtglwhA8DeB8BtwhT8JeAgH";

        String command = String.format("path -s yes -c 6 -p [^IL]!,*p2 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("[^IL]!,*p2")
                .contains(Messages.uniqueCount(215))
                .contains(Messages.minimalCount(51))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML.getHtml())
                .contains("O-Spawn S-Left T-Spawn Z-Spawn L-Left I-Spawn");

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(43)
                .contains("EhC8HeB8EeF8DeE8JeSPYZAFLDmClcJSAVDEHBEooR?BMoAVBqfLuC0AAAAvhEOrB2uBUsB3qBlpB");

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(172)
                .contains("EhC8HeB8EeF8DeE8Je0JYZAFLDmClcJSAVDEHBEooR?BaoAVBq+aFDqAAAAvhEOrBNlBTfBUiBuqB");

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML.getHtml())
                .contains("O-Spawn Z-Spawn T-Spawn S-Spawn J-Right O-Spawn");

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(15)
                .contains("EhC8HeB8EeF8DeE8JeTKYZAFLDmClcJSAVDEHBEooR?BPoAVBKtLuC0AAAAvhEOrB/pBUsB3qBlpB");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(36)
                .contains("EhC8HeB8EeF8DeE8Je/KYZAFLDmClcJSAVDEHBEooR?BToAVBvXmFDqAAAAvhETkBUiBFmB0pBmqB");
    }

    @Test
    void quizTetfu() throws Exception {
        // テト譜 + Quizパターンコマンド (フィールドファイル・パターンファイル無視)

            /*
            comment: #Q=[](I)LOSZTJ
            __________
            L__S______
            LZZSS___OO
            LLZZS___OO
             */

        String tetfu = "http://fumen.zui.jp/?v115@HhglBeQ4FeglBtR4CeRphlBtQ4CeRpJeAgWaAFLDmC?lcJSAVDEHBEooRBJoAVBM3jFD0/AAA";

        String command = String.format("path -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains("ILOSZTJ")
                .contains(Messages.uniqueCount(1))
                .contains(Messages.minimalCount(1))
                .contains(Messages.useHold());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void quizTetfuWithPatterns() throws Exception {
        // テト譜 + Quizパターンコマンド + オプション (フィールドファイル・パターンファイル無視)

            /*
            comment: #Q=[](I)LOSZTJ
            __________
            L__S______
            LZZSS___OO
            LLZZS___OO
             */

        String tetfu = "http://fumen.zui.jp/?v115@HhglBeQ4FeglBtR4CeRphlBtQ4CeRpJeAgWaAFLDmC?lcJSAVDEHBEooRBJoAVBM3jFD0/AAA";

        String command = String.format("path -t %s -p #Q=[T](*)IJOZS", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains("#Q=[T](*)IJOZS")
                .contains(Messages.uniqueCount(7))
                .contains(Messages.minimalCount(3))
                .contains(Messages.useHold());

        assertThat(log.getError()).isEmpty();
    }

    @Test
    void checkSatisfiedSequencePercentage1() throws Exception {
            /*
            comment: <Empty>
            __________
            _________X
            XX__X__XXX
            XX_XXXXXXX
             */

        String tetfu = "v115@QhC8BeA8BeE8AeG8JeAgH";

        String command = String.format("path -p *! -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.uniqueCount(5))
                .contains(Messages.minimalCount(3))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(5040, PathHTML::sequence)
                .returns(5, PathHTML::pattern)
                .returns("9gRpwhh0ywR4Rpwhg0BtwwR4C8whg0A8BtE8whG8Je?AgWTAXBQ9AgngHBg3CwBAnltCKH2BA9gRpwhwwhlh0R4Rpw?hxwglg0R4C8whwwA8glg0E8whG8JeAAPTA3/f2AgngHBg3C?wBgtjPC0yqBA9gRpBthlh0R4RpwwBtglg0R4C8xwA8glg0E?8wwG8JeAAPTAWBIOBgngHBg3CwBA+jPCMnDCA9gRpilwwh0?R4RpglAtxwg0R4C8BtA8wwg0E8AtG8JeAAPTA18nABgngHB?g3CwBAHkPCUHkBA9gg0hlwwzhR4i0xwRpR4C8glwwA8RpE8?glG8JeAAPTAS4f2AgngHBg3CwBg2TxCqSdBA", PathHTML::mergedFumen);

        assertThat(uniqueHTML.getHtml())
                .contains("Z-Spawn I-Left S-Spawn J-Right O-Spawn T-Reverse")
                .contains("76.2 %")
                .contains("[3840]")
                .contains("I-Left S-Spawn J-Right T-Right L-Left O-Spawn")
                .contains("75.0 %")
                .contains("[3780]")
                .contains("T-Right S-Spawn J-Right L-Left O-Spawn Z-Spawn")
                .contains("66.7 %")
                .contains("[3360]")
                .contains("Z-Right S-Spawn J-Right T-Left O-Spawn L-Reverse")
                .contains("53.3 %")
                .contains("[2688]")
                .contains("O-Spawn S-Spawn T-Right J-Spawn L-Left I-Spawn")
                .contains("20.0 %")
                .contains("[1008]");

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(5040, PathHTML::sequence)
                .returns(3, PathHTML::pattern)
                .returns("9gRpwhh0ywR4Rpwhg0BtwwR4C8whg0A8BtE8whG8Je?AgWTAXBQ9AgngHBg3CwBAnltCKH2BA9gRpwhwwhlh0R4Rpw?hxwglg0R4C8whwwA8glg0E8whG8JeAAPTA3/f2AgngHBg3C?wBgtjPC0yqBA9gRpilwwh0R4RpglAtxwg0R4C8BtA8wwg0E?8AtG8JeAAPTA18nABgngHBg3CwBAHkPCUHkBA", PathHTML::mergedFumen);

        assertThat(minimalHTML.getHtml())
                .contains("Z-Spawn I-Left S-Spawn J-Right O-Spawn T-Reverse")
                .contains("76.2 %")
                .contains("[3840]")
                .contains("I-Left S-Spawn J-Right T-Right L-Left O-Spawn")
                .contains("75.0 %")
                .contains("[3780]")
                .contains("Z-Right S-Spawn J-Right T-Left O-Spawn L-Reverse")
                .contains("53.3 %")
                .contains("[2688]");
    }

    @Test
    void checkSatisfiedSequencePercentage2() throws Exception {
        // 入力シーケンスが1のケース
        String command = "path -t v115@RhA8GeE8EeB8JeAgH -p LZZSITSO -d softdrop";

        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains("LZZSITSO")
                .contains(Messages.uniqueCount(1))
                .contains(Messages.minimalCount(1))
                .contains(Messages.useHold());

        assertThat(log.getError()).isEmpty();

        PathHTML minimal = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimal)
                .returns(1, PathHTML::sequence)
                .returns(1, path -> path.allFumens().size());

        assertThat(minimal.getHtml())
                .contains("Z-Spawn Z-Spawn S-Spawn I-Spawn T-Reverse L-Left O-Spawn S-Spawn")
                .contains("100.0 %")
                .contains("[1]");

        PathHTML unique = OutputFileHelper.loadPathUniqueHTML();
        assertThat(unique)
                .returns(1, PathHTML::sequence)
                .returns(1, path -> path.allFumens().size());

        assertThat(unique.getHtml())
                .contains("Z-Spawn Z-Spawn S-Spawn I-Spawn T-Reverse L-Left O-Spawn S-Spawn")
                .contains("100.0 %")
                .contains("[1]");
    }

    @Test
    void harddrop1() throws Exception {
        // Harddropのみ
        String command = "path -t v115@RhA8GeE8EeB8JeAgH -p LZZSITSO -d harddrop";

        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains("LZZSITSO")
                .contains(Messages.uniqueCount(0))
                .contains(Messages.minimalCount(0))
                .contains(Messages.useHold());

        assertThat(log.getError()).isEmpty();

        PathHTML minimal = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimal)
                .returns(1, PathHTML::sequence)
                .returns(0, path -> path.allFumens().size());

        PathHTML unique = OutputFileHelper.loadPathUniqueHTML();
        assertThat(unique)
                .returns(1, PathHTML::sequence)
                .returns(0, path -> path.allFumens().size());
    }

    @Test
    void caseTSpinDrop() throws Exception {
        // ガムシロ積み
        String fumen = "v115@zgwhGeRpwhGeRpwhh0Eehlwhg0DeR4hli0AeBtwwwh?RpJeAgH";

        {
            String command = String.format("path -t %s -p *! -c 5 -d tsm", fumen);

            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.uniqueCount(14))
                    .contains(Messages.minimalCount(13));
        }
        {
            String command = String.format("path -t %s -p *! -c 5 -d tss", fumen);

            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.uniqueCount(12))
                    .contains(Messages.minimalCount(12));
        }
        {
            String command = String.format("path -t %s -p *! -c 5 -d tsd", fumen);

            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.uniqueCount(11))
                    .contains(Messages.minimalCount(11));
        }
        {
            String command = String.format("path -t %s -p *! -c 5 -d tst", fumen);

            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.uniqueCount(6))
                    .contains(Messages.minimalCount(6));
        }
    }

    @Test
    void use180Rotation() throws Exception {
        String tetfu = "v115@HhA8BeA8FeE8CeG8CeB8JeAgH";

        {
            String command = String.format("path -t %s -p *! --kicks default", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.uniqueCount(6))
                    .contains(Messages.minimalCount(3))
                    .contains(Messages.useHold());

            assertThat(log.getError()).isEmpty();
        }
        {
            String command = String.format("path -t %s -p *! --kicks +srs", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.uniqueCount(6))
                    .contains(Messages.minimalCount(3))
                    .contains(Messages.useHold());

            assertThat(log.getError()).isEmpty();
        }
        {
            String command = String.format("path -t %s -p *! -d 180 --kicks @nullpomino180", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains(Messages.uniqueCount(7))
                    .contains(Messages.minimalCount(5))
                    .contains(Messages.useHold());

            assertThat(log.getError()).isEmpty();
        }
    }

    @Test
    void caseFilledLineInField() throws Exception {
        // 入力された地形が揃っている
        String tetfu = "v115@7gR4B8DewwR4D8BtywE8AeBtH8AeE8JeAgH";

        String command = String.format("path -c 6 --patterns [^SZ]!,*p2 --tetfu %s -s yes", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.uniqueCount(26))
                .contains(Messages.minimalCount(7))
                .contains(Messages.useHold());

        assertThat(log.getError()).isEmpty();

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(26)
                .contains("FhD8DeG8AeJ8AeE8Je2FYZAFLDmClcJSAVDEHBEooR?BKoAVBsiHgCpAAAAvhECiB6lBOrBTpBxxB");

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(7)
                .contains("FhD8DeG8AeJ8AeE8JeCGYZAFLDmClcJSAVDEHBEooR?BMoAVBv/VWCpAAAAvhEzhBmkBdrBCtBxuB");
    }

    @Test
    void test1Line() throws Exception {
        String tetfu = "v115@bhE8DeA8JeAgH";

        String command = String.format("path -c 1 --patterns I --tetfu %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.uniqueCount(1))
                .contains(Messages.minimalCount(1))
                .contains(Messages.useHold());

        assertThat(log.getError()).isEmpty();

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();

        // ライン消去なし
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(1)
                .contains("bhE8zhA8JeAgWBApAAAA");

        // ライン消去あり
        assertThat(uniqueHTML.deletedLineFumens()).isEmpty();

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();

        // ライン消去なし
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(1)
                .contains("bhE8zhA8JeAgWBApAAAA");

        // ライン消去あり
        assertThat(minimalHTML.deletedLineFumens()).isEmpty();
    }

    @Test
    void tszDrop() throws Exception {
        String fumen = "v115@+gE8CeA8AeE8EeE8EeH8KeAgH";

        {
            String command = String.format("path -t %s --hold no -p TSLI -d tsm", fumen);

            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("TSLI")
                    .contains(Messages.uniqueCount(0))
                    .contains(Messages.minimalCount(0))
                    .contains(Messages.noUseHold());

            assertThat(log.getError()).isEmpty();

            PathHTML minimal = OutputFileHelper.loadPathMinimalHTML();
            assertThat(minimal)
                    .returns(1, PathHTML::sequence)
                    .returns(0, path -> path.allFumens().size());

            PathHTML unique = OutputFileHelper.loadPathUniqueHTML();
            assertThat(unique)
                    .returns(1, PathHTML::sequence)
                    .returns(0, path -> path.allFumens().size());
        }

        {
            String command = String.format("path -t %s --hold no -p TSLI -d tsz", fumen);

            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("TSLI")
                    .contains(Messages.uniqueCount(1))
                    .contains(Messages.minimalCount(1))
                    .contains(Messages.noUseHold());

            assertThat(log.getError()).isEmpty();

            PathHTML minimal = OutputFileHelper.loadPathMinimalHTML();
            assertThat(minimal)
                    .returns(1, PathHTML::sequence)
                    .returns(1, path -> path.allFumens().size());

            PathHTML unique = OutputFileHelper.loadPathUniqueHTML();
            assertThat(unique)
                    .returns(1, PathHTML::sequence)
                    .returns(1, path -> path.allFumens().size());
        }
    }

    @Test
    void noKicks() throws Exception {
        String fumen = "v115@9gE8DeG8CeH8BeG8CeA8JeAgH";

        {
            String command = String.format("path -t %s -p *p4", fumen);

            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("*p4")
                    .contains(Messages.uniqueCount(18))
                    .contains(Messages.minimalCount(16))
                    .contains(Messages.useHold());

            assertThat(log.getError()).isEmpty();
        }
        {
            String command = String.format("path -t %s -p *p4 --kicks @nokicks", fumen);

            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("*p4")
                    .contains(Messages.uniqueCount(8))
                    .contains(Messages.minimalCount(8))
                    .contains(Messages.useHold());

            assertThat(log.getError()).isEmpty();
        }
    }
}
