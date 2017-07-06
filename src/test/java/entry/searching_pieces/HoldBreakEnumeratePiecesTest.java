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

public class HoldBreakEnumeratePiecesTest {
    @Test
    public void enumerateJust() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(210));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateOver() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("*p7");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 4);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(840));
        assertThat(core.getCounter(), is(5040));
    }

    @Test
    public void enumerateOne() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("T, J, O, Z");
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(8));
        assertThat(core.getCounter(), is(1));
    }

    @Test
    public void enumerateMulti() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(Arrays.asList(
                "T, J, O, Z",
                "T, O, J, T",
                "T, J, O, Z"
        ));
        HoldBreakEnumeratePieces core = new HoldBreakEnumeratePieces(generator, 3);
        Set<Pieces> pieces = core.enumerate();
        assertThat(pieces.size(), is(13));
        assertThat(core.getCounter(), is(3));
    }
}