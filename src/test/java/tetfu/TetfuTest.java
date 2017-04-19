package tetfu;

import core.mino.Block;
import core.mino.MinoFactory;
import core.srs.Rotate;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.mino.Block.J;
import static core.mino.Block.L;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TetfuTest {
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
                new TetfuElement(ColorType.L, Rotate.Right, 0, 3, "f"),
                new TetfuElement(ColorType.T, Rotate.Reverse, 7, 1)
        );

        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(field, elements);
        assertThat(encode, is("vhGBQYBABBAAAnmQBACBAAA+tQBADBAAALpQBAEBAA?AcqQBAFBAAAKfQBAGBAAAlsQAA"));
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
        tetfu.decode(value);
    }

    @Test
    public void decode2() throws Exception {
        String value = "bhzhPexAcFAooMDEPBAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        tetfu.decode(value);
    }

    @Test
    public void decode3() throws Exception {
        // empty
        String value = "vhAAgH";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        tetfu.decode(value);
    }
}