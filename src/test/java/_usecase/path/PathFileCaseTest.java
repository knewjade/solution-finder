package _usecase.path;

import _usecase.ConfigFileHelper;
import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.path.files.OutputFileHelper;
import _usecase.path.files.PathHTML;
import com.google.common.base.Charsets;
import core.field.Field;
import core.field.FieldFactory;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PathFileCaseTest extends PathUseCaseBaseTest {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

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
        Charset charset = Charsets.UTF_8;

        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.createFieldFile(field, height, charset);

        ConfigFileHelper.deletePatternFile();
        ConfigFileHelper.createPatternFile("*p4", charset);

        String command = "path";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

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
    void useFieldFileAndPatternsFile2() throws Exception {
        // フィールドファイル + パターンファイル (デフォルト以外の場所)

        Field field = FieldFactory.createField("" +
                "XX_____XXX" +
                "XX______XX" +
                "XX____XXXX" +
                "XX_____XXX"
        );

        int height = 4;
        ConfigFileHelper.createFieldFile(field, height, "another_field");
        ConfigFileHelper.createPatternFile("*p6", "another_pattern");

        String command = "path -fp input/another_field.txt -pp input/another_pattern.txt";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p6")
                .contains(Messages.uniqueCount(47))
                .contains(Messages.minimalCount(37))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(47, PathHTML::pattern);

        // ライン消去あり
        assertThat(uniqueHTML.noDeletedLineFumens())
                .hasSize(1)
                .contains("9gB8RpQ4BtE8RpR4BtD8ilQ4F8glzhC8JeAgWFApyj?FDvAAAA");

        // ライン消去なし
        assertThat(uniqueHTML.deletedLineFumens())
                .hasSize(46)
                .contains("9gB8Bti0E8wwBtilD8xwR4F8wwR4glg0C8JeAgWFAz?OUFDqAAAA")
                .contains("9gB8zhwwE8i0ywD8RpBtF8Rpg0BtC8JeAgWFAaHmPC?pAAAA")
                .contains("9gB8Bti0E8wwBtilD8xwR4F8wwR4glg0C8JeAgWFAz?OUFDqAAAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(uniqueHTML.allFumens()))
                .hasSize(47)
                .allMatch(coloredField -> isFilled(height, coloredField));

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(37, PathHTML::pattern);

        // ライン消去あり
        assertThat(minimalHTML.noDeletedLineFumens())
                .hasSize(1)
                .contains("9gB8RpQ4BtE8RpR4BtD8ilQ4F8glzhC8JeAgWFApyj?FDvAAAA");

        // ライン消去なし
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(36)
                .contains("9gB8zhwwE8g0R4ywD8R4BtF8i0BtC8JeAgWFA6+jPC?pAAAA")
                .contains("9gB8h0Q4BtE8g0wwR4BtD8ywQ4F8g0zhC8JeAgWFAp?+jPC6AAAA")
                .contains("9gB8ilBtE8zhBtD8glR4wwF8R4ywC8JeAgWFAUtbMC?sAAAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(37)
                .allMatch(coloredField -> isFilled(height, coloredField));
    }

    @Test
    void useFieldFileAndPatternsFile3() throws Exception {
        // フィールドファイル + パターンファイル (デフォルト以外の場所)

        Field field = FieldFactory.createField("" +
                "XX_____XXX" +
                "XX____XXXX" +
                "XX___XXXXX" +
                "XXIIIIXXXX"
        );

        int height = 4;
        ConfigFileHelper.createFieldFile(field, height);
        ConfigFileHelper.createPatternFile("*p3");

        String command = "path";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p3")
                .contains(Messages.uniqueCount(14))
                .contains(Messages.minimalCount(12))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(14, PathHTML::pattern);

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(12, PathHTML::pattern);
    }

    @Test
    void useFieldFileAndPatternsFile4() throws Exception {
        // フィールドファイル + パターンファイル (デフォルト以外の場所)

        ConfigFileHelper.createFieldFile(String.join(LINE_SEPARATOR,
                "4",
                "XX_____XXX",
                "XX____XXXX",
                "XX___XXXXX",
                "XXIIIIXXXX"
        ));
        ConfigFileHelper.createPatternFile("I,*p3");

        String command = "path -r true";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode())
                .as(log.getError())
                .isEqualTo(0);

        assertThat(log.getOutput())
                .contains("I,*p3")
                .contains(Messages.uniqueCount(14))
                .contains(Messages.minimalCount(12))
                .contains(Messages.useHold());

        // unique
        PathHTML uniqueHTML = OutputFileHelper.loadPathUniqueHTML();
        assertThat(uniqueHTML)
                .returns(14, PathHTML::pattern);

        // minimal
        PathHTML minimalHTML = OutputFileHelper.loadPathMinimalHTML();
        assertThat(minimalHTML)
                .returns(12, PathHTML::pattern);
    }

    @Test
    void getLog() throws Exception {
        // フィールドファイル, パターンファイル, ログファイル (場所を変更する)

        /*
        ______XXXX
        ______XXXX
        _____XXXXX
        _______XXX
         */

        int height = 4;

        ConfigFileHelper.createFieldFile("http://fumen.zui.jp/?v115@DhD8FeD8EeE8GeC8JeAgH", "test_field", "input");
        ConfigFileHelper.createPatternFile("*p7", "input", "test_patterns");

        String command = "path -fp input/test_field.txt -pp input/test_patterns.txt -o test_output/test_path.txt --log-path test_output_log/test_last_output.txt";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        String logFile;
        try (Stream<String> lines = Files.lines(Paths.get("test_output_log/test_last_output.txt"))) {
            logFile = lines.collect(Collectors.joining(LINE_SEPARATOR)) + LINE_SEPARATOR;
        }

        assertThat(log.getOutput())
                .isEqualTo(logFile);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.uniqueCount(82))
                .contains(Messages.minimalCount(66))
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
                .returns(66, PathHTML::pattern);

        // ライン消去あり
        assertThat(minimalHTML.noDeletedLineFumens())
                .isEmpty();

        // ライン消去なし
        assertThat(minimalHTML.deletedLineFumens())
                .hasSize(66)
                .contains("9gBthlRpD8wwBtglRpD8xwR4g0E8wwR4gli0C8JeAg?WGAKNWWCvXBAA")
                .contains("9gwhhlg0BtD8whwwgli0D8whxwRpE8whwwglRpBtC8?JeAgWGAP+VWCqXBAA")
                .contains("9gh0wwilD8g0xwglBtD8g0R4RpE8R4wwRpBtC8JeAg?WGAPt/wCsXBAA");

        // すべての譜面
        assertThat(parseLastPageTetfu(minimalHTML.allFumens()))
                .hasSize(66)
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
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

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

    @Test
    void manyPatternsFile() throws Exception {
        ConfigFileHelper.createPatternFileFromCommand("*!");

        String tetfu = "v115@9gB8HeC8GeD8FeC8QeAgH";
        String command = String.format("path -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains(Messages.uniqueCount(116))
                .contains(Messages.minimalCount(76))
                .contains("... and more, total 5040 lines");

        assertThat(log.getError()).isEmpty();
    }
}
