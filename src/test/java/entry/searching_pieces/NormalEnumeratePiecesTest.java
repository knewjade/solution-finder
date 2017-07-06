package entry.searching_pieces;

import common.datastore.pieces.Pieces;
import core.mino.Block;
import common.pattern.PiecesGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NormalEnumeratePiecesTest {
    @Test
    public void enumerateHoldJust() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(840));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateHoldOver() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(2520));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateHoldOver2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I, *p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(840));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateHoldOne() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I, S, Z, O");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, true);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(1));
        assertThat(core.getCounter(), is(1));
    }

    @Test
    public void enumerateHoldMulti() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z, I",
                "J, O, S, T, Z",
                "T, J, O, I, S",
                "T, J, O, Z, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, true);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(3));
        assertThat(core.getCounter(), is(4));
    }

    @Test
    public void enumerateNoHoldJust() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(210));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateNoHoldOver() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(840));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateNoHoldOver2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I, *p7");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(210));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateNoHoldOne() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I, S, Z, O");
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 4, false);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(1));
        assertThat(core.getCounter(), is(1));
    }

    @Test
    public void enumerateNoHoldMulti() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z",
                "J, O, S, T",
                "T, J, O, I"
        ));
        NormalEnumeratePieces core = new NormalEnumeratePieces(generator, 3, false);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(2));
        assertThat(core.getCounter(), is(3));
    }
}