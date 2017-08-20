package entry.percent;

import common.comparator.FieldComparator;
import core.field.Field;
import core.field.FieldFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PercentSettingParserTest {
    private static void assertField(Field actual, Field expected) {
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(actual, expected)).isEqualTo(0);
    }

    @Test
    void testDefault() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/2line.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();
        String commands = String.format("--field-path %s --patterns-path %s", fieldPath, patternsPath);
        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX________" +
                "XX________"
        );
        
       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/last_output.txt", PercentSettings::getLogFilePath)
                    .returns(2, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p7"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(true, PercentSettings::isUsingHold);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testDefault2() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/template.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/3mino.txt").getPath();
        String commands = String.format("--field-path %s --patterns-path %s", fieldPath, patternsPath);
        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XXXX____XX" +
                "XXXX___XXX" +
                "XXXX__XXXX" +
                "XXXX___XXX"
        );

       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/last_output.txt", PercentSettings::getLogFilePath)
                    .returns(4, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p3"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(true, PercentSettings::isUsingHold);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testHelp() throws Exception {
        String commands = "-h";
        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();
        assertThat(parse.isPresent()).isFalse();
    }

    @Test
    void testTetfu1() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();
        String tetfu = "v115@9gB8DeG8CeH8BeG8CeD8JeAgWBAUAAAA";  // comment: 4
        String commands = String.format("--hold avoid -fp %s -pp %s --tetfu %s", fieldPath, patternsPath, tetfu);

        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX____XXXX" +
                "XXX___XXXX" +
                "XXXX__XXXX" +
                "XXX___XXXX"
        );

       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/last_output.txt", PercentSettings::getLogFilePath)
                    .returns(4, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p7"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(false, PercentSettings::isUsingHold);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu2() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 4 --hold avoid --patterns *p4
        String tetfu = "v115@9gB8DeG8CeH8BeG8CeD8JeAgWlA0no2AtTKNEM388A?wBrNEJ388AwjdOEB/2rDSm0TAS4WOEUAAAA";
        String commands = String.format("--hold use -fp %s -pp %s --tetfu %s --patterns T,Z --log-path output/dummy", fieldPath, patternsPath, tetfu);

        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX____XXXX" +
                "XXX___XXXX" +
                "XXXX__XXXX" +
                "XXX___XXXX"
        );

       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/dummy", PercentSettings::getLogFilePath)
                    .returns(4, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("T,Z"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(true, PercentSettings::isUsingHold);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu3() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 4 --hold avoid --patterns *p4
        String tetfu = "v115@vh2SSYRBFLDmClcJSAVDEHBEooRBMoAVBUujPCv3jx?CPNUPCJHWWCJtPFDs+bgC6P9VCp/dgCzn9VCzvaFDUePFDv?+TWCviLuCqe1LCqHLWCzAAAANpBXqBGjBznB0fB0rBdnBzq?BxvB/tBqsBGjBJnB1vBTmBxkB3pBikBGrByuB9tBXjB0sB0?rBTkBmfBplBxmBirBNpBWyBXqB0fBToBCjBRmBesBTmB0qB?NpBpoBXqB0fBmrBzsB3rB6qBzsBirB0sB/tBGjB1wBNmQSA?0no2AtTKNEM388AwBrNEJnBAA";
        String commands = String.format("--hold avoid -fp %s -pp %s --tetfu %s --patterns *p5 --log-path output/dummy -P 55", fieldPath, patternsPath, tetfu);

        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "____X__XXX" +
                "____XX__XX" +
                "____XXXXXX" +
                "____XXXXXX"
        );

       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/dummy", PercentSettings::getLogFilePath)
                    .returns(4, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p5"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(false, PercentSettings::isUsingHold);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu4() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 3 -p T,S,L,O,L
        String tetfu = "v115@vh2SSYRBFLDmClcJSAVDEHBEooRBMoAVBUujPCv3jx?CPNUPCJHWWCJtPFDs+bgC6P9VCp/dgCzn9VCzvaFDUePFDv?+TWCviLuCqe1LCqHLWCzAAAANpBXqBGjBznB0fB0rBdnBzq?BxvB/tBqsBGjBJnB1vBTmBxkB3pBikBGrByuB9tBXjB0sB0?rBTkBmfBplBxmBirBNpBWyBXqB0fBToBCjBRmBesBTmB0qB?NpBpoBXqB0fBmrBzsQaAzno2ANI98AQe88ADd88AjS88ADX?88AjCBAA3rQjAFLDmClcJSAVztSAVG88A4c88AZyKWCat/w?CJePFDvyzBA6qBzsBirB0sB/tBGjB1wBNmQSA0no2AtTKNE?M388AwBrNEJnBAA";
        String commands = String.format("--hold avoid -fp %s -pp %s --tetfu %s --patterns *p5 --log-path output/dummy -P 46 --tree-depth 1 --failed-count 10", fieldPath, patternsPath, tetfu);

        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX_______X" +
                "XXX______X" +
                "XXX___XXXX"
        );

       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/dummy", PercentSettings::getLogFilePath)
                    .returns(3, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p5"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(false, PercentSettings::isUsingHold)
                    .returns(1, PercentSettings::getTreeDepth)
                    .returns(10, PercentSettings::getFailedCount);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu1InField() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/tetfu1.txt").getPath();
        String commands = String.format("-fp %s -P 46 -td 4", fieldPath);

        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX_______X" +
                "XXX______X" +
                "XXX___XXXX"
        );

       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns(3, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("T,S,L,O,L"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(true, PercentSettings::isUsingHold)
                    .returns(4, PercentSettings::getTreeDepth)
                    .returns(100, PercentSettings::getFailedCount);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu2InField() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/tetfu2.txt").getPath();
        String commands = String.format("-fp %s -P 6 -p *p4 -td 2 -fc 50", fieldPath);

        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "_____XXXXX" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "____XXXXXX" +
                ""
        );

       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns(4, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p4"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(true, PercentSettings::isUsingHold)
                    .returns(2, PercentSettings::getTreeDepth)
                    .returns(50, PercentSettings::getFailedCount);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu3InField() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/tetfu3.txt").getPath();
        String commands = String.format("-fp %s -P 36", fieldPath);

        PercentSettingParser entryPoint = new PercentSettingParser(commands);
        Optional<PercentSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX_____XXX" +
                "XXX____XXX" +
                "XXXX___XXX" +
                "XXX____XXX" +
                ""
        );

       assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns(4, PercentSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p4"), PercentSettings::getPatterns)
                    .returns(true, PercentSettings::isOutputToConsole)
                    .returns(true, PercentSettings::isUsingHold)
                    .returns(3, PercentSettings::getTreeDepth)
                    .returns(100, PercentSettings::getFailedCount);
            assertField(settings.getField(), expectedField);
        });
    }
}
