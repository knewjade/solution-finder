package misc.tetfu;

import core.mino.Block;
import core.mino.MinoFactory;
import core.srs.Rotate;
import org.junit.Test;
import misc.tetfu.common.ColorConverter;
import misc.tetfu.common.ColorType;
import misc.tetfu.field.ArrayColoredField;
import misc.tetfu.field.ColoredField;
import misc.tetfu.field.ColoredFieldFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.mino.Block.J;
import static core.mino.Block.L;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TetfuTest {
    private static void assertField(ColoredField actual, ColoredField expected) {
        for (int y = 0; y < 24; y++)
            for (int x = 0; x < 10; x++)
                assertThat(actual.getBlockNumber(x, y), is(expected.getBlockNumber(x, y)));
    }

    @Test
    public void encode1() throws Exception {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.T, Rotate.Spawn, 5, 0)
        );

        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("vhA1QJ"));
    }

    @Test
    public void encode2() throws Exception {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.L, Rotate.Spawn, 4, 0),
                new TetfuElement(ColorType.J, Rotate.Spawn, 8, 0),
                new TetfuElement(ColorType.I, Rotate.Left, 6, 1),
                new TetfuElement(ColorType.S, Rotate.Spawn, 4, 1),
                new TetfuElement(ColorType.Z, Rotate.Spawn, 8, 1),
                new TetfuElement(ColorType.T, Rotate.Spawn, 4, 3),
                new TetfuElement(ColorType.O, Rotate.Spawn, 0, 0),
                new TetfuElement(ColorType.J, Rotate.Right, 0, 3)
        );

        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("vhHSQJWyBJnBXmBUoBVhBTpBOfB"));
    }

    @Test
    public void encode3() throws Exception {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.I, Rotate.Reverse, 5, 0, "a"),
                new TetfuElement(ColorType.S, Rotate.Reverse, 5, 2, "b"),
                new TetfuElement(ColorType.J, Rotate.Left, 9, 1, "c"),
                new TetfuElement(ColorType.O, Rotate.Right, 0, 1, "d"),
                new TetfuElement(ColorType.Z, Rotate.Left, 3, 1, "e"),
                new TetfuElement(ColorType.L, Rotate.Right, 0, 3, "日本語"),
                new TetfuElement(ColorType.T, Rotate.Reverse, 7, 1)
        );

        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("vhGBQYBABBAAAnmQBACBAAA+tQBADBAAALpQBAEBAA?AcqQBAFBAAAKfQSAlfrHBFwDfE2Cx2Bl/PwB53AAAlsQAA"));
    }

    @Test
    public void encode4() throws Exception {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.I, Rotate.Spawn, 5, 0)
        );

        MinoFactory factory = new MinoFactory();
        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);
        field.putMino(factory.create(Block.I, Rotate.Spawn), 1, 0);

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("bhzhPexQJ"));
    }

    @Test
    public void encode5() throws Exception {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.I, Rotate.Reverse, 6, 0)
        );

        MinoFactory factory = new MinoFactory();
        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);
        field.putMino(factory.create(Block.I, Rotate.Spawn), 1, 0);

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("bhzhPehQJ"));
    }

    @Test
    public void encode6() throws Exception {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.Empty, Rotate.Spawn, 6, 0)
        );

        MinoFactory factory = new MinoFactory();
        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("vhAAgH"));
    }

    @Test
    public void encodeQuiz1() throws Exception {
        List<Block> orders = Collections.singletonList(L);
        String quiz = Tetfu.encodeForQuiz(orders);

        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.L, Rotate.Right, 0, 1, quiz)
        );

        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("vhAKJYUAFLDmClcJSAVDEHBEooRBMoAVB"));
    }

    @Test
    public void encodeQuiz2() throws Exception {
        List<Block> orders = Arrays.asList(J, L);
        String quiz = Tetfu.encodeForQuiz(orders, L);

        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.L, Rotate.Right, 0, 1, quiz),
                new TetfuElement(ColorType.J, Rotate.Left, 3, 1, quiz)
        );

        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("vhBKJYVAFLDmClcJSAVTXSAVG88AYS88AZAAAA+qB"));
    }

    @Test
    public void decode1() throws Exception {
        String value = "bhzhPexAN";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages.size(), is(1));
        assertThat(pages.get(0).getColorType(), is(ColorType.I));
        assertThat(pages.get(0).getRotate(), is(Rotate.Spawn));
        assertThat(pages.get(0).getX(), is(5));
        assertThat(pages.get(0).getY(), is(0));
        assertThat(pages.get(0).getComment(), is(""));
        assertField(ColoredFieldFactory.createColoredField("IIII______"), pages.get(0).getField());
    }

    @Test
    public void decode2() throws Exception {
        String value = "bhzhPexAcFAooMDEPBAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages.size(), is(1));
        assertThat(pages.get(0).getColorType(), is(ColorType.I));
        assertThat(pages.get(0).getRotate(), is(Rotate.Spawn));
        assertThat(pages.get(0).getX(), is(5));
        assertThat(pages.get(0).getY(), is(0));
        assertThat(pages.get(0).getComment(), is("hello"));
        assertField(ColoredFieldFactory.createColoredField("IIII______"), pages.get(0).getField());
    }

    @Test
    public void decode3() throws Exception {
        // empty
        String value = "vhAAgH";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages.size(), is(1));
        assertThat(pages.get(0).getColorType(), is(ColorType.Empty));
        assertThat(pages.get(0).getRotate(), is(Rotate.Reverse));
        assertThat(pages.get(0).getX(), is(0));
        assertThat(pages.get(0).getY(), is(22));
        assertThat(pages.get(0).getComment(), is(""));
        assertField(ColoredFieldFactory.createColoredField(""), pages.get(0).getField());
    }

    @Test
    public void decode4() throws Exception {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.I, Rotate.Reverse, 5, 0, "a"),
                new TetfuElement(ColorType.S, Rotate.Reverse, 5, 2, "b"),
                new TetfuElement(ColorType.J, Rotate.Left, 9, 1, "c"),
                new TetfuElement(ColorType.O, Rotate.Right, 0, 1, "hello world!"),
                new TetfuElement(ColorType.Z, Rotate.Left, 3, 1, "こんにちは"),
                new TetfuElement(ColorType.L, Rotate.Right, 0, 3, "x ~= 1;"),
                new TetfuElement(ColorType.T, Rotate.Reverse, 7, 1)
        );

        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);

        List<TetfuPage> pages = tetfu.decode(encode);

        assertThat(pages.size(), is(elements.size()));
        for (int index = 0; index < pages.size(); index++) {
            TetfuPage page = pages.get(index);
            TetfuElement element = elements.get(index);
            assertThat(page.getColorType(), is(element.getColorType()));
            assertThat(page.getRotate(), is(element.getRotate()));
            assertThat(page.getX(), is(element.getX()));
            assertThat(page.getY(), is(element.getY()));
            assertThat(page.getComment(), is(element.getComment()));
        }
    }

    @Test
    public void decode5() throws Exception {
        String value = "bhzhFeH8Bex4OvhAAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages.size(), is(2));
        assertThat(pages.get(0).getColorType(), is(ColorType.I));
        assertThat(pages.get(0).getRotate(), is(Rotate.Spawn));
        assertThat(pages.get(0).getX(), is(5));
        assertThat(pages.get(0).getY(), is(0));
        assertThat(pages.get(0).getComment(), is(""));
        assertField(ColoredFieldFactory.createColoredField(
                "IIII______"
        ), pages.get(0).getField());

        assertThat(pages.get(1).getColorType(), is(ColorType.Empty));
        assertThat(pages.get(1).getComment(), is(""));
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "__IIIIIIII" +
                        "__XXXXXXXX"
        ), pages.get(1).getField());
    }

    @Test
    public void decode6() throws Exception {
        String value = "VhRpHeRpNeAgHvhIAAAAAAAAAAAAAAAAAAAAAAAAAA?A";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages.size(), is(10));
        for (int index = 0; index < 10; index++) {
            assertThat(pages.get(index).getColorType(), is(ColorType.Empty));
            assertThat(pages.get(index).getComment(), is(""));
        }
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "____OO____" +
                        "____OO____"
        ), pages.get(9).getField());
    }

    @Test
    public void decode7() throws Exception {
        String value = "+gH8AeI8BeH8AeI8KeAgHvhBpoBAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages.size(), is(3));
        assertThat(pages.get(0).getColorType(), is(ColorType.Empty));
        assertThat(pages.get(1).getColorType(), is(ColorType.I));
        assertThat(pages.get(2).getColorType(), is(ColorType.Empty));
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "_XXXXXXXXI" +
                        "_XXXXXXXXI"
        ), pages.get(2).getField());
    }
}