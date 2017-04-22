package entry.searching_pieces;

import core.mino.Block;
import misc.PiecesGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NormalEnumeratePiecesTest {
    @Test
    public void enumerateHold1() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        List<List<Block>> pieces = core.enumerate();
        assertThat(pieces.size(), is(840));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateHold2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        List<List<Block>> pieces = core.enumerate();
        assertThat(pieces.size(), is(2520));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateHold3() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z, I",
                "J, O, S, T, Z",
                "T, J, O, I, S",
                "T, J, O, Z, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        List<List<Block>> pieces = core.enumerate();
        assertThat(pieces.size(), is(3));
        assertThat(core.getCounter(), is(4));
    }

    @Test
    public void enumerateNoHold1() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        List<List<Block>> pieces = core.enumerate();
        assertThat(pieces.size(), is(210));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateNoHold2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        List<List<Block>> pieces = core.enumerate();
        assertThat(pieces.size(), is(840));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateNoHold3() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z",
                "J, O, S, T",
                "T, J, O, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        List<List<Block>> pieces = core.enumerate();
        assertThat(pieces.size(), is(2));
        assertThat(core.getCounter(), is(3));
    }
}