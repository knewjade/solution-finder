package entry.path;

import common.comparator.FieldComparator;
import core.field.Field;
import core.field.FieldFactory;
import org.apache.commons.cli.DefaultParser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PathSettingParserTest {
    private static void assertField(Field actual, Field expected) {
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(actual, expected)).isEqualTo(0);
    }

    @Test
    void testDefault() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/2line.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();
        String commands = String.format("--field-path %s --patterns-path %s", fieldPath, patternsPath);
        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));

        Field expectedField = FieldFactory.createField("" +
                "XX________" +
                "XX________"
        );

        assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/last_output.txt", PathSettings::getLogFilePath)
                    .returns(2, PathSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p7"), PathSettings::getPatterns)
                    .returns(true, PathSettings::isLogOutputToConsole)
                    .returns(true, PathSettings::isUsingHold)
                    .returns("output/path.html", PathSettings::getOutputBaseFilePath)
                    .returns(PathLayer.Minimal, PathSettings::getPathLayer)
                    .returns(OutputType.HTML, PathSettings::getOutputType)
                    .returns(false, PathSettings::isTetfuSplit)
                    .returns(0, PathSettings::getCachedMinBit);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testDefault2() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/template.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/3mino.txt").getPath();
        String commands = String.format("--field-path %s --patterns-path %s", fieldPath, patternsPath);
        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));

        Field expectedField = FieldFactory.createField("" +
                "XXXX____XX" +
                "XXXX___XXX" +
                "XXXX__XXXX" +
                "XXXX___XXX"
        );

        assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/last_output.txt", PathSettings::getLogFilePath)
                    .returns(4, PathSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p3"), PathSettings::getPatterns)
                    .returns(true, PathSettings::isLogOutputToConsole)
                    .returns(true, PathSettings::isUsingHold)
                    .returns("output/path.html", PathSettings::getOutputBaseFilePath)
                    .returns(PathLayer.Minimal, PathSettings::getPathLayer)
                    .returns(OutputType.HTML, PathSettings::getOutputType)
                    .returns(false, PathSettings::isTetfuSplit)
                    .returns(0, PathSettings::getCachedMinBit);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testHelp() throws Exception {
        String commands = "-h";
        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));
        assertThat(parse.isPresent()).isFalse();
    }

    @Test
    void testTetfu1() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();
        String tetfu = "v115@9gB8DeG8CeH8BeG8CeD8JeAgWBAUAAAA";  // comment: 4
        String commands = String.format("--hold avoid -fp %s -pp %s --tetfu %s --max-layer 1 --format csv --cached-bit 1", fieldPath, patternsPath, tetfu);

        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));

        Field expectedField = FieldFactory.createField("" +
                "XX____XXXX" +
                "XXX___XXXX" +
                "XXXX__XXXX" +
                "XXX___XXXX"
        );

        assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/last_output.txt", PathSettings::getLogFilePath)
                    .returns(4, PathSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p7"), PathSettings::getPatterns)
                    .returns(true, PathSettings::isLogOutputToConsole)
                    .returns(false, PathSettings::isUsingHold)
                    .returns("output/path.html", PathSettings::getOutputBaseFilePath)
                    .returns(PathLayer.Unique, PathSettings::getPathLayer)
                    .returns(OutputType.CSV, PathSettings::getOutputType)
                    .returns(false, PathSettings::isTetfuSplit)
                    .returns(1, PathSettings::getCachedMinBit);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu2() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 4 --hold avoid --patterns T,Z
        String tetfu = "v115@9gB8DeG8CeH8BeG8CeD8JeAgWnA0no2AtTKNEM388A?wBrNEJ388AwjdOEB/2rDSm0TASYtSAyUDCA";
        String commands = String.format("--hold use -fp %s -pp %s --tetfu %s --patterns *p4 --log-path output/dummy -L 2", fieldPath, patternsPath, tetfu);

        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));

        Field expectedField = FieldFactory.createField("" +
                "XX____XXXX" +
                "XXX___XXXX" +
                "XXXX__XXXX" +
                "XXX___XXXX"
        );

        assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/dummy", PathSettings::getLogFilePath)
                    .returns(4, PathSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p4"), PathSettings::getPatterns)
                    .returns(true, PathSettings::isLogOutputToConsole)
                    .returns(true, PathSettings::isUsingHold)
                    .returns("output/path.html", PathSettings::getOutputBaseFilePath)
                    .returns(PathLayer.Minimal, PathSettings::getPathLayer)
                    .returns(OutputType.HTML, PathSettings::getOutputType)
                    .returns(false, PathSettings::isTetfuSplit)
                    .returns(0, PathSettings::getCachedMinBit);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu3() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 4 --hold avoid --patterns *p4
        String tetfu = "v115@vh2SSYRBFLDmClcJSAVDEHBEooRBMoAVBUujPCv3jx?CPNUPCJHWWCJtPFDs+bgC6P9VCp/dgCzn9VCzvaFDUePFDv?+TWCviLuCqe1LCqHLWCzAAAANpBXqBGjBznB0fB0rBdnBzq?BxvB/tBqsBGjBJnB1vBTmBxkB3pBikBGrByuB9tBXjB0sB0?rBTkBmfBplBxmBirBNpBWyBXqB0fBToBCjBRmBesBTmB0qB?NpBpoBXqB0fBmrBzsB3rB6qBzsBirB0sB/tBGjB1wBNmQSA?0no2AtTKNEM388AwBrNEJnBAA";
        String commands = String.format("--hold use -fp %s -pp %s --tetfu %s --patterns T,*p5 --log-path output/dummy -P 55", fieldPath, patternsPath, tetfu);

        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));

        Field expectedField = FieldFactory.createField("" +
                "____X__XXX" +
                "____XX__XX" +
                "____XXXXXX" +
                "____XXXXXX"
        );

        assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/dummy", PathSettings::getLogFilePath)
                    .returns(4, PathSettings::getMaxClearLine)
                    .returns(Collections.singletonList("T,*p5"), PathSettings::getPatterns)
                    .returns(true, PathSettings::isLogOutputToConsole)
                    .returns(true, PathSettings::isUsingHold)
                    .returns("output/path.html", PathSettings::getOutputBaseFilePath)
                    .returns(PathLayer.Minimal, PathSettings::getPathLayer)
                    .returns(OutputType.HTML, PathSettings::getOutputType)
                    .returns(false, PathSettings::isTetfuSplit)
                    .returns(0, PathSettings::getCachedMinBit);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu4() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 4 -p T,S,L,O,L
        String tetfu = "v115@vh2SSYRBFLDmClcJSAVDEHBEooRBMoAVBUujPCv3jx?CPNUPCJHWWCJtPFDs+bgC6P9VCp/dgCzn9VCzvaFDUePFDv?+TWCviLuCqe1LCqHLWCzAAAANpBXqBGjBznB0fB0rBdnBzq?BxvB/tBqsBGjBJnB1vBTmBxkB3pBikBGrByuB9tBXjB0sB0?rBTkBmfBplBxmBirBNpBWyBXqB0fBToBCjBRmBesBTmB0qB?NpBpoBXqB0fBmrBzsQaA0no2ANI98AQe88ADd88AjS88ADX?88AjCBAA3rQjAFLDmClcJSAVztSAVG88A4c88AZyKWCat/w?CJePFDvyzBA6qBzsBirB0sB/tBGjB1wBNmQSA0no2AtTKNE?M388AwBrNEJnBAA";
        String commands = String.format("--hold avoid -fp %s -pp %s --tetfu %s --patterns *p5 --log-path output/dummy -P 46 -o output/result_dummy.txt -s yes -th 4", fieldPath, patternsPath, tetfu);

        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));

        Field expectedField = FieldFactory.createField("" +
                "XX_______X" +
                "XXX______X" +
                "XXXXXXXXXX" +
                "XXX___XXXX"
        );

        assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns("output/dummy", PathSettings::getLogFilePath)
                    .returns(4, PathSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p5"), PathSettings::getPatterns)
                    .returns(true, PathSettings::isLogOutputToConsole)
                    .returns(false, PathSettings::isUsingHold)
                    .returns("output/result_dummy.txt", PathSettings::getOutputBaseFilePath)
                    .returns(PathLayer.Minimal, PathSettings::getPathLayer)
                    .returns(OutputType.HTML, PathSettings::getOutputType)
                    .returns(true, PathSettings::isTetfuSplit)
                    .returns(0, PathSettings::getCachedMinBit)
                    .returns(4, PathSettings::getThreadCount);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu1InField() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/tetfu1.txt").getPath();
        String commands = String.format("-fp %s -P 46", fieldPath);

        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));

        Field expectedField = FieldFactory.createField("" +
                "XX_______X" +
                "XXX______X" +
                "XXXXXXXXXX" +
                "XXX___XXXX"
        );

        assertThat(parse).isPresent();
        parse.ifPresent(settings -> {
            assertThat(settings)
                    .returns(4, PathSettings::getMaxClearLine)
                    .returns(Collections.singletonList("T,S,L,O,L"), PathSettings::getPatterns)
                    .returns(true, PathSettings::isLogOutputToConsole)
                    .returns(true, PathSettings::isUsingHold)
                    .returns("output/path.html", PathSettings::getOutputBaseFilePath)
                    .returns(PathLayer.Minimal, PathSettings::getPathLayer)
                    .returns(OutputType.HTML, PathSettings::getOutputType)
                    .returns(false, PathSettings::isTetfuSplit)
                    .returns(0, PathSettings::getCachedMinBit)
                    .returns(-1, PathSettings::getThreadCount);
            assertField(settings.getField(), expectedField);
        });
    }

    @Test
    void testTetfu2InField() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/tetfu2.txt").getPath();
        String commands = String.format("-fp %s -P 6 -p *p4 -th 1", fieldPath);

        PathSettingParser entryPoint = new PathSettingParser(PathOptions.create(), new DefaultParser());
        Optional<PathSettings> parse = entryPoint.parse(Arrays.asList(commands.split(" ")));

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
                    .returns(4, PathSettings::getMaxClearLine)
                    .returns(Collections.singletonList("*p4"), PathSettings::getPatterns)
                    .returns(true, PathSettings::isLogOutputToConsole)
                    .returns(true, PathSettings::isUsingHold)
                    .returns("output/path.html", PathSettings::getOutputBaseFilePath)
                    .returns(PathLayer.Minimal, PathSettings::getPathLayer)
                    .returns(OutputType.HTML, PathSettings::getOutputType)
                    .returns(false, PathSettings::isTetfuSplit)
                    .returns(0, PathSettings::getCachedMinBit)
                    .returns(1, PathSettings::getThreadCount);
            assertField(settings.getField(), expectedField);
        });
    }
}
