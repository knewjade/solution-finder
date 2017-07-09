package common.comparator;

import common.datastore.pieces.LongPieces;
import core.mino.Block;
import lib.Randoms;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class PiecesNameComparatorTest {
    @Test
    public void compare() throws Exception {
        LongPieces pieces1 = new LongPieces(Arrays.asList(Block.T, Block.O, Block.I));
        LongPieces pieces2 = new LongPieces(Arrays.asList(Block.T, Block.O, Block.I));
        PiecesNameComparator comparator = new PiecesNameComparator();
        assertThat(comparator.compare(pieces1, pieces2), is(0));
        assertThat(comparator.compare(pieces2, pieces1), is(0));
    }

    @Test
    public void compareDiffSize() throws Exception {
        LongPieces pieces1 = new LongPieces(Arrays.asList(Block.T, Block.O, Block.I));
        LongPieces pieces2 = new LongPieces(Arrays.asList(Block.T, Block.O, Block.I, Block.J));

        // assert is not 0 & sign reversed
        PiecesNameComparator comparator = new PiecesNameComparator();
        assertThat(pieces2.toString(), comparator.compare(pieces1, pieces2) * comparator.compare(pieces2, pieces1), is(lessThan(0)));
    }

    @Test
    public void compareDiffRandom() throws Exception {
        List<Block> allBlocks = Arrays.asList(Block.T, Block.I, Block.O, Block.S, Block.Z, Block.J, Block.L, Block.T, Block.I, Block.O, Block.S, Block.Z, Block.J, Block.L);

        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            List<Block> blocks1 = randoms.sample(allBlocks, randoms.nextInt(10));
            List<Block> blocks2 = randoms.sample(allBlocks, randoms.nextInt(10));

            if (blocks1.equals(blocks2))
                blocks1.add(Block.O);

            LongPieces pieces1 = new LongPieces(blocks1);
            LongPieces pieces2 = new LongPieces(blocks2);

            // assert is not 0 & sign reversed
            PiecesNameComparator comparator = new PiecesNameComparator();
            assertThat(pieces2.toString(), comparator.compare(pieces1, pieces2) * comparator.compare(pieces2, pieces1), is(lessThan(0)));
        }
    }
}