package _usecase;

import _experimental.cycle1.EasyPool;
import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import common.tetfu.Tetfu;
import common.tetfu.common.ColorConverter;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;
import entry.EntryPointMain;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: write irregular unittest
class PathUseCaseTest {
    private static final EasyPool easyPool = new EasyPool();
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static class Messages {
        private static String uniqueCount(int count) {
            return String.format("Found path [unique] = %d", count);
        }

        private static String minimalCount(int count) {
            return String.format("Found path [minimal] = %d", count);
        }

        private static String minimalCount() {
            return "Found path [minimal]";
        }

        private static String clearLine(int height) {
            return String.format("Max clear lines: %d", height);
        }

        private static String useHold() {
            return "Using hold: use";
        }

        private static String noUseHold() {
            return "Using hold: avoid";
        }
    }

    private static List<ColoredField> parseLastPageTetfu(List<String> fumens) {
        return fumens.stream()
                .map(fumen -> {
                    Tetfu tetfu = easyPool.getTetfu();
                    return tetfu.decode(fumen);
                })
                .map(tetfuPages -> {
                    assert 0 < tetfuPages.size();
                    return tetfuPages.get(tetfuPages.size() - 1);
                })
                .map(page -> {
                    ColoredField field = page.getField();

                    if (!page.isPutMino())
                        return field;

                    ColorConverter converter = easyPool.getColorConverter();
                    Block block = converter.parseToBlock(page.getColorType());
                    field.putMino(new Mino(block, page.getRotate()), page.getX(), page.getY());
                    return field;
                })
                .collect(Collectors.toList());
    }

    private boolean isFilled(int height, ColoredField coloredField) {
        for (int y = 0; y < height; y++)
            if (!coloredField.isFilledLine(y))
                return false;
        return true;
    }

    private boolean isEmpty(int height, ColoredField coloredField) {
        ColoredField freeze = coloredField.freeze(height);
        freeze.clearLine();
        return freeze.isPerfect();
    }

    @BeforeEach
    void setUp() throws IOException {
        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.deletePatternFile();
        OutputFileHelper.deletePathUniqueHTML();
        OutputFileHelper.deletePathMinimalHTML();
        OutputFileHelper.deletePathUniqueCSV();
        OutputFileHelper.deletePathMinimalCSV();
    }

    @Nested
    class FileCase {
        @Test
        void useFieldFileAndPatternsFile1() throws Exception {
            // フィールドファイル + パターンファイル

            Field field = FieldFactory.createField("" +
                    "XXXXX____X" +
                    "XXXXXX___X" +
                    "XXXXXXX__X" +
                    "XXXXXX___X"
            );

            int height = 4;
            ConfigFileHelper.createFieldFile(field, height);
            ConfigFileHelper.createPatternFile("*p4");

            String command = "path";
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p4")
                    .contains(Messages.uniqueCount(18))
                    .contains(Messages.minimalCount(16))
                    .contains(Messages.useHold());

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(18, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .hasSize(5)
                    .contains("9gE8BthlG8BtglH8wwglG8ywA8JeAgWDA0iDCA")
                    .contains("9gE8zhG8ywH8wwglG8ilA8JeAgWDAsedBA")
                    .contains("9gE8ywAtG8wwBtH8AtglG8ilA8JeAgWDAsX2BA");

            // ライン消去なし
            assertThat(uniqueHTML.deletedLineFumens())
                    .hasSize(13)
                    .contains("9gE8BtQ4glG8ilH8R4G8BtQ4A8JeAgWDAMtDCA")
                    .contains("9gE8BthlG8BtglH8R4G8R4glA8JeAgWDAziDCA")
                    .contains("9gE8ywglG8ilH8RpG8wwRpA8JeAgWDAvC2BA");

            // すべての譜面
            assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                    .hasSize(18)
                    .allMatch(coloredField -> isFilled(height, coloredField));

            // minimal
            PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
            assertThat(minimalHTML)
                    .returns(16, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens())
                    .hasSize(5)
                    .contains("9gE8BthlG8BtglH8wwglG8ywA8JeAgWDA0iDCA")
                    .contains("9gE8zhG8ywH8wwglG8ilA8JeAgWDAsedBA")
                    .contains("9gE8ywAtG8wwBtH8AtglG8ilA8JeAgWDAsX2BA");

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(11)
                    .contains("9gE8BtR4G8R4wwH8xwG8BtwwA8JeAgWDAUtDCA")
                    .contains("9gE8BtQ4glG8ilH8R4G8BtQ4A8JeAgWDAMtDCA")
                    .contains("9gE8ywglG8ilH8RpG8wwRpA8JeAgWDAvC2BA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(16)
                    .allMatch(coloredField -> isFilled(height, coloredField));
        }

        @Test
        void getLog() throws Exception {
            // フィールドファイル, パターンファイル, ログファイル (場所を変更する)

            Field field = FieldFactory.createField("" +
                    "______XXXX" +
                    "______XXXX" +
                    "_____XXXXX" +
                    "_______XXX"
            );

            int height = 4;

            ConfigFileHelper.createFieldFile(field, "input", "test_field", height);
            ConfigFileHelper.createPatternFile("*p7", "input", "test_patterns");

            String command = "path -fp input/test_field.txt -pp input/test_patterns.txt -o test_output/test_path.txt --log-path test_output_log/test_last_output.txt";
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            String logFile = Files.lines(Paths.get("test_output_log/test_last_output.txt")).collect(Collectors.joining(LINE_SEPARATOR)) + LINE_SEPARATOR;
            assertThat(log.getOutput())
                    .isEqualTo(logFile);

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(82))
                    .contains(Messages.minimalCount(71))
                    .contains(Messages.useHold());

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML("test_output/test_path_unique.html");
            assertThat(uniqueHTML)
                    .returns(82, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
            assertThat(uniqueHTML.deletedLineFumens())
                    .hasSize(82)
                    .contains("9gilQ4BtD8RpzhD8RpwwR4E8glywQ4BtC8JeAgWGAU?XltC6CBAA")
                    .contains("9gh0zhD8g0wwRpBtD8xwRpglE8g0wwilBtC8JeAgWG?AMHmPCpXBAA")
                    .contains("9gilwwBtD8Rpxwh0D8RpR4g0E8glR4wwg0BtC8JeAg?WGAzvKxC6CBAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                    .hasSize(82)
                    .allMatch(coloredField -> isFilled(height, coloredField));

            // minimal
            PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML("test_output/test_path_minimal.html");
            assertThat(minimalHTML)
                    .returns(71, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(71)
                    .contains("9gBthlRpD8wwBtglRpD8xwR4g0E8wwR4gli0C8JeAg?WGAKNWWCvXBAA")
                    .contains("9gwhhlg0BtD8whwwgli0D8whxwRpE8whwwglRpBtC8?JeAgWGAP+VWCqXBAA")
                    .contains("9gh0wwilD8g0xwglBtD8g0R4RpE8R4wwRpBtC8JeAg?WGAPt/wCsXBAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(71)
                    .allMatch(coloredField -> isFilled(height, coloredField));
        }

        @Test
        void useFieldFileAndPatternsCommand1() throws Exception {
            // フィールドファイル + パターンコマンド　(パターンファイルを無視)

            Field field = FieldFactory.createField("" +
                    "__________" +
                    "X_______X_" +
                    "X___XXXXX_" +
                    "XX_XXXXXX_"
            );

            int height = 4;
            ConfigFileHelper.createFieldFile(field, height);
            ConfigFileHelper.createPatternFile("*p7");

            String command = "path";
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(1))
                    .contains(Messages.minimalCount(1));

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(1, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
            assertThat(uniqueHTML.deletedLineFumens())
                    .hasSize(1)
                    .contains("9ghlh0BtywwhA8glg0R4BtwwA8whA8glR4E8whB8g0?F8whJeAgWGAJtjWCqOBAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                    .hasSize(1)
                    .allMatch(coloredField -> isFilled(height, coloredField));

            // minimal
            PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
            assertThat(minimalHTML)
                    .returns(1, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(1)
                    .contains("9ghlh0BtywwhA8glg0R4BtwwA8whA8glR4E8whB8g0?F8whJeAgWGAJtjWCqOBAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(1)
                    .allMatch(coloredField -> isFilled(height, coloredField));
        }
    }

    @Nested
    class TetfuCase {
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
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(35))
                    .contains(Messages.minimalCount(31))
                    .contains(Messages.useHold());

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(35, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
            assertThat(uniqueHTML.deletedLineFumens())
                    .hasSize(35)
                    .contains("9gB8Bthlwwi0C8BtglxwR4E8glwwR4g0F8zhJeAgWG?AJNWWC6/AAA")
                    .contains("9gB8Bti0hlwhC8BtRpwwglwhE8RpxwwhF8g0wwglwh?JeAgWGAp+KWC6/AAA")
                    .contains("9gB8Bthlwwi0C8BtglzhE8glxwR4F8wwR4g0JeAgWG?AT+TFD0/AAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                    .hasSize(35)
                    .allMatch(coloredField -> isFilled(height, coloredField));

            // minimal
            PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
            assertThat(minimalHTML)
                    .returns(31, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens()).isEmpty();

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(31)
                    .contains("9gB8BtzhhlC8Btg0RpwwglE8i0xwF8RpwwglJeAgWG?A0vKWCa+AAA")
                    .contains("9gB8Bti0glR4C8BtzhwwE8ilxwF8g0R4wwJeAgWGAU?ejWCz/AAA")
                    .contains("9gB8Bti0hlwhC8BtQ4g0wwglwhE8R4xwwhF8Q4wwgl?whJeAgWGAp+TWC6/AAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(31)
                    .allMatch(coloredField -> isFilled(height, coloredField));
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
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("[OSZ]p3")
                    .contains(Messages.uniqueCount(2))
                    .contains(Messages.minimalCount(2));

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(2, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
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

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens()).isEmpty();

            // ライン消去なし
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
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(54))
                    .contains(Messages.minimalCount(42));

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(54, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
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
                    .returns(42, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(42)
                    .contains("9gB8hlwwR4AtRpC8glxwBtRpC8glwwi0F8R4Atg0B8?JeAgWGAKHWWCaNBAA")
                    .contains("9gB8hlwwi0RpC8glxwQ4g0RpC8glBtR4F8wwBtQ4B8?JeAgWGAa9KWC0/AAA")
                    .contains("9gB8BtwwR4i0C8Btzhg0C8ywRpF8R4RpB8JeAgWGAP?ezPCUNBAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(42)
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
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(83))
                    .contains(Messages.minimalCount(74))
                    .contains(Messages.useHold());

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(83, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
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
                    .returns(74, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens()).isEmpty();

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(74)
                    .contains("DhD8GeC8EeE8FeD8JeRPYZAFLDmClcJSAVDEHBEooR?BJoAVB6/dgCsAAAAvhEMpB2qBtrBzqBipB")
                    .contains("DhD8GeC8EeE8FeD8Je/JYZAFLDmClcJSAVDEHBEooR?BToAVBqXegCsAAAAvhEmqBUqBtrBzlBipB")
                    .contains("DhD8GeC8EeE8FeD8JeTKYZAFLDmClcJSAVDEHBEooR?BPoAVB0vTWCpAAAAvhENpBmlB3rB6pBxvB")
                    .contains("DhD8GeC8EeE8FeD8JezKYZAFLDmClcJSAVDEHBEooR?BPoAVBqXWWCpAAAAvhE2uB0kBtrByvBxuB")
                    .contains("DhD8GeC8EeE8FeD8JezKYZAFLDmClcJSAVDEHBEooR?BPoAVBT+VWCqAAAAvhE3pBRmBNkBaqBGrB");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(74)
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
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(54))
                    .contains(Messages.minimalCount(42));

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(54, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
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
                    .returns(42, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens())
                    .isEmpty();

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(42)
                    .contains("9gB8hlwwR4AtRpC8glxwBtRpC8glwwi0F8R4Atg0B8?JeAgWGAKHWWCaNBAA")
                    .contains("9gB8hlwwi0RpC8glxwQ4g0RpC8glBtR4F8wwBtQ4B8?JeAgWGAa9KWC0/AAA")
                    .contains("9gB8BtwwR4i0C8Btzhg0C8ywRpF8R4RpB8JeAgWGAP?ezPCUNBAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(42)
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
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(245))
                    .contains(Messages.minimalCount(199));

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(245, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .hasSize(2)
                    .contains("9gD8ili0D8glAtRpQ4g0D8BtRpR4D8AtzhQ4JeAgWG?AJ3jFDqCBAA")
                    .contains("9gD8Q4zhAtD8R4RpBtD8g0Q4RpAtglD8i0ilJeAgWG?AsvaFDT+AAA");

            // ライン消去なし
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
                    .returns(199, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens())
                    .hasSize(2)
                    .contains("9gD8ili0D8glAtRpQ4g0D8BtRpR4D8AtzhQ4JeAgWG?AJ3jFDqCBAA")
                    .contains("9gD8Q4zhAtD8R4RpBtD8g0Q4RpAtglD8i0ilJeAgWG?AsvaFDT+AAA");

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(197)
                    .contains("9gD8ili0D8RpzhD8RpR4wwg0D8glR4ywJeAgWGAU9C?MCqCBAA")
                    .contains("9gD8ili0D8glywR4D8RpwwR4g0D8RpzhJeAgWGAJ3T?xCs/AAA")
                    .contains("9gD8zhRpD8glQ4BtRpD8glR4i0D8hlQ4Btg0JeAgWG?AqyjFDP+AAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(199)
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
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(10))
                    .contains(Messages.minimalCount(7))
                    .contains(Messages.useHold());

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(10, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .hasSize(5)
                    .contains("9gilywzhglAtB8wwi0RpBtE8g0RpAtI8JeAgWGAv33?LC0CBAA")
                    .contains("9gilywR4RpglAtB8wwR4g0RpBtE8i0AtI8JeAgWGAq?HztC0CBAA")
                    .contains("9gwhh0ywR4hlwhg0B8wwR4Rpglwhg0E8RpglwhI8Je?AgWGAP+rtCqOBAA");

            // ライン消去なし
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
                    .returns(7, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens())
                    .hasSize(5)
                    .contains("9gilywR4RpglAtB8wwR4g0RpBtE8i0AtI8JeAgWGAq?HztC0CBAA")
                    .contains("9gwhh0ywR4hlwhg0B8wwR4Rpglwhg0E8RpglwhI8Je?AgWGAP+rtCqOBAA")
                    .contains("9gwhh0Btywhlwhg0B8BtwwRpglwhg0E8RpglwhI8Je?AgWGAP+TFDqOBAA");

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(2)
                    .contains("9gilBtywR4RpB8Btwwi0RpE8R4g0glI8JeAgWGAvfj?xCzCBAA")
                    .contains("9gilywR4Atg0RpB8wwR4Btg0RpE8Ath0glI8JeAgWG?AvfLuC0CBAA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(7)
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
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("T,*p3")
                    .contains(Messages.uniqueCount(9))
                    .contains(Messages.minimalCount(8))
                    .contains(Messages.noUseHold());

            // unique
            PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
            assertThat(uniqueHTML)
                    .returns(9, PathHTML::pattern);

            // ライン消去あり
            assertThat(uniqueHTML.noDeletedLineFumens())
                    .hasSize(3)
                    .contains("9gF8zhG8i0H8wwg0G8ywJeAgWDA0PdBA")
                    .contains("9gF8BthlG8BtglH8wwglG8ywJeAgWDA0XkBA")
                    .contains("9gF8zhG8ywH8wwglG8ilJeAgWDAsedBA");

            // ライン消去なし
            assertThat(uniqueHTML.deletedLineFumens())
                    .hasSize(6)
                    .contains("9gF8BtR4G8R4wwH8xwG8BtwwJeAgWDAUtDCA")
                    .contains("9gF8zhG8RpwwH8xwG8RpwwJeAgWDAUXdBA");

            // すべての譜面
            assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                    .hasSize(9)
                    .allMatch(coloredField -> isFilled(height, coloredField));

            // minimal
            PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
            assertThat(minimalHTML)
                    .returns(8, PathHTML::pattern);

            // ライン消去あり
            assertThat(minimalHTML.noDeletedLineFumens())
                    .hasSize(3)
                    .contains("9gF8zhG8i0H8wwg0G8ywJeAgWDA0PdBA")
                    .contains("9gF8BthlG8BtglH8wwglG8ywJeAgWDA0XkBA")
                    .contains("9gF8zhG8ywH8wwglG8ilJeAgWDAsedBA");

            // ライン消去なし
            assertThat(minimalHTML.deletedLineFumens())
                    .hasSize(5)
                    .contains("9gF8hlh0G8glg0wwH8xwG8glg0wwJeAgWDA0/jBA")
                    .contains("9gF8BtwwwhG8xwwhH8wwwhG8BtwhJeAgWDApuDCA");

            // すべての譜面
            assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                    .hasSize(8)
                    .allMatch(coloredField -> isFilled(height, coloredField));
        }
    }

    @Nested
    class OptionCase {
        // オプションが正しく反映されているかを確認する

        @Test
        void maxLayer() throws Exception {
            // 計算するレイヤー数を指定

            /*
            comment: 4 -p *p7 -L 1
            _____X____
            _____XX___
            ___XXXXXX_
            ___XXXXXXX
             */

            String tetfu = "v115@ChA8IeB8FeF8DeG8JeAgWVA0no2ANI98AQPcQBFbcs?AMoo2ARAAAA";

            ConfigFileHelper.createPatternFile("*p4");

            String command = String.format("path -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.clearLine(4))
                    .contains(Messages.uniqueCount(4))
                    .doesNotContain(Messages.minimalCount())
                    .contains(Messages.useHold());

            assertThat(OutputFileHelper.existsPathUniqueHTML()).isTrue();
            assertThat(OutputFileHelper.existsPathMinimalHTML()).isFalse();
        }

        @Test
        void page() throws Exception {
            // テト譜のページを指定

            /*
            page: 5
            comment: 4 -p S,[TIOJLZ]p6
            X____XX__X
            X____X__XX
            X____XXXXX
            X____XXXXX
             */

            String tetfu = "v115@9gA8IeA8IeA8IeA8SeSSYMA0no2ANI98AQPk/AvhDM?oBHsBumBAAPbA0no2ANI98Awc88ADYfzBUuaPCsnEHBkYzA?A";

            ConfigFileHelper.createPatternFile("*p2");

            String command = String.format("path --page 5 -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("S,[TIOJLZ]p6")
                    .contains(Messages.clearLine(4))
                    .contains(Messages.uniqueCount(95))
                    .contains(Messages.minimalCount(47))
                    .contains(Messages.useHold());
        }

        @Test
        void format1() throws Exception {
            // フォーマットをCSV

            /*
            comment: 4 -p I,*p6
            X_________
            XXXXX_____
            XXXXX_____
            XXXXX_____
             */

            String tetfu = "v115@9gA8IeE8EeE8EeE8OeAgWQA0no2ANI98AwN88AjPEN?B";

            int height = 4;
            ConfigFileHelper.createFieldFile(FieldFactory.createField(height), height);
            ConfigFileHelper.createPatternFile("*p2");

            String command = String.format("path -t %s -f csv", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            Field field = FieldFactory.createField("" +
                    "X_________" +
                    "XXXXX_____" +
                    "XXXXX_____" +
                    "XXXXX_____"
            );

            assertThat(log.getOutput())
                    .contains("I,*p6")
                    .contains(Messages.clearLine(4))
                    .contains(Messages.uniqueCount(186))
                    .contains(Messages.minimalCount(142))
                    .contains(Messages.useHold());

            // unique
            PathCSV uniqueCSV = OutputFileHelper.loadPathUniqueCSV();
            assertThat(uniqueCSV.operations().stream()
                    .map(operations -> {
                        Field freeze = field.freeze(height);
                        for (Operation operation : operations.getOperations()) {
                            freeze.put(new Mino(operation.getBlock(), operation.getRotate()), operation.getX(), operation.getY());
                            freeze.clearLine();
                        }
                        return freeze;
                    }))
                    .hasSize(186)
                    .allMatch(Field::isPerfect);

            // minimal
            PathCSV minimalCSV = OutputFileHelper.loadPathMinimalCSV();
            assertThat(minimalCSV.operations().stream()
                    .map(operations -> {
                        Field freeze = field.freeze(height);
                        for (Operation operation : operations.getOperations()) {
                            freeze.put(new Mino(operation.getBlock(), operation.getRotate()), operation.getX(), operation.getY());
                            freeze.clearLine();
                        }
                        return freeze;
                    }))
                    .hasSize(142)
                    .allMatch(Field::isPerfect);
        }

        @Test
        void format2() throws Exception {
            // フォーマットをCSV

            /*
            comment: 4 -p *p2
            XXXXXXXXX_
            XXXXXXXXX_
            __XXXXXXX_
            __XXXXXXX_
             */

            String tetfu = "v115@9gI8AeI8CeG8CeG8KeAgWMA0no2ANI98AQPk/A";

            int height = 4;
            ConfigFileHelper.createFieldFile(FieldFactory.createField(height), height);
            ConfigFileHelper.createPatternFile("*p2");

            String command = String.format("path -t %s -f csv -L 1", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p2")
                    .contains(Messages.clearLine(4))
                    .contains(Messages.uniqueCount(1))
                    .doesNotContain(Messages.minimalCount())
                    .contains(Messages.useHold());

            // unique
            PathCSV uniqueCSV = OutputFileHelper.loadPathUniqueCSV();
            assertThat(uniqueCSV.operations().stream()
                    .map(Operations::getOperations))
                    .hasSize(1)
                    .element(0)
                    .isEqualTo(Arrays.<Operation>asList(
                            new SimpleOperation(Block.I, Rotate.Left, 9, 1),
                            new SimpleOperation(Block.O, Rotate.Spawn, 0, 0)
                    ));
        }

        @Test
        void colorTetfu() throws Exception {
            // 色付きのテト譜を指定

            /*
            comment: <Empty>
            S________J
            SS_______J
            JS______JJ
            JJJ___IIII
             */

            String tetfu = "v115@9gA8HeC8GeC8FeE8CeD8JeAgH";

            String command = String.format("path -p *p7 -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.clearLine(4))
                    .contains(Messages.uniqueCount(150))
                    .contains(Messages.minimalCount(123))
                    .contains(Messages.useHold());
        }
    }

    @Nested
    class CountCase {
        @Test
        void pattern1WithoutHold() throws Exception {
            /*
            XXXXXX____
            XXXXXXX___
            XXXXXXXX__
            XXXXXXX___
             */

            String tetfu = "v115@9gF8DeG8CeH8BeG8MeAgH";

            String command = String.format("path -c 4 -p T,*p3 -H no -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("T,*p3")
                    .contains(Messages.uniqueCount(9))
                    .contains(Messages.minimalCount(8))
                    .contains(Messages.noUseHold());
        }

        @Test
        void pattern2WithoutHold() throws Exception {
            /*
            XXXX______
            XXXX______
            XXXX______
            XXXX______
             */

            String tetfu = "m115@9gD8FeD8FeD8FeD8PeAgH";

            String command = String.format("path -c 4 -p *p7 -H no -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(245))
                    .contains(Messages.minimalCount(199))
                    .contains(Messages.noUseHold());
        }

        @Test
        void pattern3WithoutHold() throws Exception {
            /*
            XXX_______
            XXX______X
            XXX_____XX
            XXX______X
             */

            String tetfu = "d115@9gC8GeC8FeD8EeE8FeA8JeAgH";

            String command = String.format("path -c 4 -p *p7 -H no -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(173))
                    .contains(Messages.minimalCount(130))
                    .contains(Messages.noUseHold());
        }

        @Test
        void pattern4() throws Exception {
            /*
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
             */

            String tetfu = "http://fumen.zui.jp/?v115@pgF8DeF8DeF8DeF8DeF8DeF8NeAgH";

            String command = String.format("path -c 6 -p *p7 -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(1002))
                    .contains(Messages.minimalCount(699))
                    .contains(Messages.useHold());
        }

        @Test
        void pattern5() throws Exception {
            /*
            XXXXXX____
            XXXXXX____
            XXXXXX____
            XXXXXX____
             */

            String tetfu = "v115@9gF8DeF8DeF8DeF8NeAgH";

            String command = String.format("path -p *p7 -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*p7")
                    .contains(Messages.uniqueCount(135))
                    .contains(Messages.minimalCount(69))
                    .contains(Messages.useHold());
        }

        @Test
        @Tag("long")
        void pattern6() throws Exception {
            /*
            __________
            __________
            __________
            __________
             */

            String tetfu = "v115@vhAAgH";

            String command = String.format("path -p J,Z,O,S,L,I,I,J,S,O,Z -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("J,Z,O,S,L,I,I,J,S,O,Z")
                    .contains(Messages.uniqueCount(71))
                    .contains(Messages.minimalCount(10))
                    .contains(Messages.useHold());
        }

        @Disabled
        @Test
        @Tag("long")
        void pattern7() throws Exception {
            /*
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
            __XXXXXXXX
             */

            String tetfu = "v115@XgH8BeH8BeH8BeH8BeH8BeH8BeH8BeH8JeAgH";

            String command = String.format("path -c 8 -p *,*p4 -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("*,*p4")
                    .contains(Messages.uniqueCount(298))
                    .contains(Messages.minimalCount(239))
                    .contains(Messages.useHold());
        }

        @Test
        void pattern8() throws Exception {
            /*
            X_________
            XXXXXXXXX_
            XXXXXXXX__
            XXXXXX____
             */

            String tetfu = "http://harddrop.com/fumen/?v115@9gA8IeH8BeI8AeF8NeAgH";

            String command = String.format("path -p S,L,O,I,T -t %s", tetfu);
            Log log = RunnerHelper.runnerCatchingLog(() -> {
                EntryPointMain.main(command.split(" "));
            });

            assertThat(log.getOutput())
                    .contains("S,L,O,I,T")
                    .contains(Messages.uniqueCount(3))
                    .contains(Messages.minimalCount(3))
                    .contains(Messages.useHold());
        }
    }
}