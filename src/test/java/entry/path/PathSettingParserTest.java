package entry.path;

import core.field.Field;
import core.field.FieldFactory;
import common.comparator.FieldComparator;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PathSettingParserTest {
    private static void assertField(Field actual, Field expected) {
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(actual, expected), is(0));
    }

    @Test
    public void testDefault() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/2line.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();
        String commands = String.format("--field-path %s --patterns-path %s", fieldPath, patternsPath);
        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX________" +
                "XX________"
        );

        assertThat(parse.isPresent(), is(true));
        parse.ifPresent(settings -> {
            assertThat(settings.getLogFilePath(), is("output/last_output.txt"));
            assertThat(settings.getMaxClearLine(), is(2));
            assertThat(settings.getPatterns(), is(Collections.singletonList("*p7")));
            assertThat(settings.isOutputToConsole(), is(true));
            assertThat(settings.isUsingHold(), is(true));
            assertField(settings.getField(), expectedField);
            assertThat(settings.getOutputBaseFilePath(), is("output/path.txt"));
            assertThat(settings.getMaxLayer(), is(3));
            assertThat(settings.getOutputType(), is(OutputType.Link));
        });
    }

    @Test
    public void testDefault2() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/template.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/3mino.txt").getPath();
        String commands = String.format("--field-path %s --patterns-path %s", fieldPath, patternsPath);
        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XXXX____XX" +
                "XXXX___XXX" +
                "XXXX__XXXX" +
                "XXXX___XXX"
        );

        assertThat(parse.isPresent(), is(true));
        parse.ifPresent(settings -> {
            assertThat(settings.getLogFilePath(), is("output/last_output.txt"));
            assertThat(settings.getMaxClearLine(), is(4));
            assertThat(settings.getPatterns(), is(Collections.singletonList("*p3")));
            assertThat(settings.isOutputToConsole(), is(true));
            assertThat(settings.isUsingHold(), is(true));
            assertField(settings.getField(), expectedField);
            assertThat(settings.getOutputBaseFilePath(), is("output/path.txt"));
            assertThat(settings.getMaxLayer(), is(3));
            assertThat(settings.getOutputType(), is(OutputType.Link));
        });
    }

    @Test
    public void testHelp() throws Exception {
        String commands = "-h";
        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();
        assertThat(parse.isPresent(), is(false));
    }

    @Test
    public void testTetfu1() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();
        String tetfu = "v115@9gB8DeG8CeH8BeG8CeD8JeAgWBAUAAAA";  // comment: 4
        String commands = String.format("--hold avoid -fp %s -pp %s --tetfu %s --max-layer 1 --format csv", fieldPath, patternsPath, tetfu);

        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX____XXXX" +
                "XXX___XXXX" +
                "XXXX__XXXX" +
                "XXX___XXXX"
        );

        assertThat(parse.isPresent(), is(true));
        parse.ifPresent(settings -> {
            assertThat(settings.getLogFilePath(), is("output/last_output.txt"));
            assertThat(settings.getMaxClearLine(), is(4));
            assertThat(settings.getPatterns(), is(Collections.singletonList("*p7")));
            assertThat(settings.isOutputToConsole(), is(true));
            assertThat(settings.isUsingHold(), is(false));
            assertField(settings.getField(), expectedField);
            assertThat(settings.getOutputBaseFilePath(), is("output/path.txt"));
            assertThat(settings.getMaxLayer(), is(1));
            assertThat(settings.getOutputType(), is(OutputType.CSV));
        });
    }

    @Test
    public void testTetfu2() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 4 --hold avoid --patterns *p4
        String tetfu = "v115@9gB8DeG8CeH8BeG8CeD8JeAgWlA0no2AtTKNEM388A?wBrNEJ388AwjdOEB/2rDSm0TAS4WOEUAAAA";
        String commands = String.format("--hold use -fp %s -pp %s --tetfu %s --patterns 'T, Z' --log-path output/dummy -L 2", fieldPath, patternsPath, tetfu);

        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX____XXXX" +
                "XXX___XXXX" +
                "XXXX__XXXX" +
                "XXX___XXXX"
        );

        assertThat(parse.isPresent(), is(true));
        parse.ifPresent(settings -> {
            assertThat(settings.getLogFilePath(), is("output/dummy"));
            assertThat(settings.getMaxClearLine(), is(4));
            assertThat(settings.getPatterns(), is(Collections.singletonList("*p4")));
            assertThat(settings.isOutputToConsole(), is(true));
            assertThat(settings.isUsingHold(), is(false));
            assertField(settings.getField(), expectedField);
            assertThat(settings.getOutputBaseFilePath(), is("output/path.txt"));
            assertThat(settings.getMaxLayer(), is(2));
            assertThat(settings.getOutputType(), is(OutputType.Link));
        });
    }

    @Test
    public void testTetfu3() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 4 --hold avoid --patterns *p4
        String tetfu = "v115@vh2SSYRBFLDmClcJSAVDEHBEooRBMoAVBUujPCv3jx?CPNUPCJHWWCJtPFDs+bgC6P9VCp/dgCzn9VCzvaFDUePFDv?+TWCviLuCqe1LCqHLWCzAAAANpBXqBGjBznB0fB0rBdnBzq?BxvB/tBqsBGjBJnB1vBTmBxkB3pBikBGrByuB9tBXjB0sB0?rBTkBmfBplBxmBirBNpBWyBXqB0fBToBCjBRmBesBTmB0qB?NpBpoBXqB0fBmrBzsB3rB6qBzsBirB0sB/tBGjB1wBNmQSA?0no2AtTKNEM388AwBrNEJnBAA";
        String commands = String.format("--hold use -fp %s -pp %s --tetfu %s --patterns *p5 --log-path output/dummy -P 55", fieldPath, patternsPath, tetfu);

        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "____X__XXX" +
                "____XX__XX" +
                "____XXXXXX" +
                "____XXXXXX"
        );

        assertThat(parse.isPresent(), is(true));
        parse.ifPresent(settings -> {
            assertThat(settings.getLogFilePath(), is("output/dummy"));
            assertThat(settings.getMaxClearLine(), is(4));
            assertThat(settings.getPatterns(), is(Collections.singletonList("*p5")));
            assertThat(settings.isOutputToConsole(), is(true));
            assertThat(settings.isUsingHold(), is(false));
            assertField(settings.getField(), expectedField);
            assertThat(settings.getOutputBaseFilePath(), is("output/path.txt"));
            assertThat(settings.getMaxLayer(), is(3));
            assertThat(settings.getOutputType(), is(OutputType.Link));
        });
    }

    @Test
    public void testTetfu4() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/4row.txt").getPath();
        String patternsPath = ClassLoader.getSystemResource("patterns/7mino.txt").getPath();

        // comment: 3 -p T,S,L,O,L
        String tetfu = "v115@vh2SSYRBFLDmClcJSAVDEHBEooRBMoAVBUujPCv3jx?CPNUPCJHWWCJtPFDs+bgC6P9VCp/dgCzn9VCzvaFDUePFDv?+TWCviLuCqe1LCqHLWCzAAAANpBXqBGjBznB0fB0rBdnBzq?BxvB/tBqsBGjBJnB1vBTmBxkB3pBikBGrByuB9tBXjB0sB0?rBTkBmfBplBxmBirBNpBWyBXqB0fBToBCjBRmBesBTmB0qB?NpBpoBXqB0fBmrBzsQaAzno2ANI98AQe88ADd88AjS88ADX?88AjCBAA3rQjAFLDmClcJSAVztSAVG88A4c88AZyKWCat/w?CJePFDvyzBA6qBzsBirB0sB/tBGjB1wBNmQSA0no2AtTKNE?M388AwBrNEJnBAA";
        String commands = String.format("--hold use -fp %s -pp %s --tetfu %s --patterns *p5 --log-path output/dummy -P 46 -o output/result_dummy.txt", fieldPath, patternsPath, tetfu);

        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX_______X" +
                "XXX______X" +
                "XXX___XXXX"
        );

        assertThat(parse.isPresent(), is(true));
        parse.ifPresent(settings -> {
            assertThat(settings.getLogFilePath(), is("output/dummy"));
            assertThat(settings.getMaxClearLine(), is(3));
            assertThat(settings.getPatterns(), is(Collections.singletonList("T,S,L,O,L")));
            assertThat(settings.isOutputToConsole(), is(true));
            assertThat(settings.isUsingHold(), is(true));
            assertField(settings.getField(), expectedField);
            assertThat(settings.getOutputBaseFilePath(), is("output/result_dummy.txt"));
            assertThat(settings.getMaxLayer(), is(3));
            assertThat(settings.getOutputType(), is(OutputType.Link));
        });
    }

    @Test
    public void testTetfu1InField() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/tetfu1.txt").getPath();
        String commands = String.format("-fp %s -P 46", fieldPath);

        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "XX_______X" +
                "XXX______X" +
                "XXX___XXXX"
        );

        assertThat(parse.isPresent(), is(true));
        parse.ifPresent(settings -> {
            assertThat(settings.getMaxClearLine(), is(3));
            assertThat(settings.getPatterns(), is(Collections.singletonList("T,S,L,O,L")));
            assertThat(settings.isOutputToConsole(), is(true));
            assertThat(settings.isUsingHold(), is(true));
            assertField(settings.getField(), expectedField);
            assertThat(settings.getOutputBaseFilePath(), is("output/path.txt"));
            assertThat(settings.getMaxLayer(), is(3));
            assertThat(settings.getOutputType(), is(OutputType.Link));
        });
    }

    @Test
    public void testTetfu2InField() throws Exception {
        String fieldPath = ClassLoader.getSystemResource("field/tetfu2.txt").getPath();
        String commands = String.format("-fp %s -P 6 -p *p4", fieldPath);

        PathSettingParser entryPoint = new PathSettingParser(commands);
        Optional<PathSettings> parse = entryPoint.parse();

        Field expectedField = FieldFactory.createField("" +
                "_____XXXXX" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "____XXXXXX" +
                ""
        );

        assertThat(parse.isPresent(), is(true));
        parse.ifPresent(settings -> {
            assertThat(settings.getMaxClearLine(), is(4));
            assertThat(settings.getPatterns(), is(Collections.singletonList("*p4")));
            assertThat(settings.isOutputToConsole(), is(true));
            assertThat(settings.isUsingHold(), is(true));
            assertField(settings.getField(), expectedField);
            assertThat(settings.getOutputBaseFilePath(), is("output/path.txt"));
            assertThat(settings.getMaxLayer(), is(3));
            assertThat(settings.getOutputType(), is(OutputType.Link));
        });
    }
}
