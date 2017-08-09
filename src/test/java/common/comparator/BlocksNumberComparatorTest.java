package common.comparator;

import common.datastore.pieces.LongBlocks;
import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BlocksNumberComparatorTest {
    @Test
    void compare() throws Exception {
        LongBlocks pieces1 = new LongBlocks(Arrays.asList(Block.T, Block.O, Block.I));
        LongBlocks pieces2 = new LongBlocks(Arrays.asList(Block.T, Block.O, Block.I));
        PiecesNumberComparator comparator = new PiecesNumberComparator();
        assertThat(comparator.compare(pieces1, pieces2)).isEqualTo(0);
        assertThat(comparator.compare(pieces2, pieces1)).isEqualTo(0);
    }

    @Test
    void compareDiffSize() throws Exception {
        LongBlocks pieces1 = new LongBlocks(Arrays.asList(Block.T, Block.O, Block.I));
        LongBlocks pieces2 = new LongBlocks(Arrays.asList(Block.T, Block.O, Block.I, Block.J));

        // assert is not 0 & sign reversed
        PiecesNumberComparator comparator = new PiecesNumberComparator();
        assertThat(comparator.compare(pieces1, pieces2) * comparator.compare(pieces2, pieces1))
                .as(pieces2.toString())
                .isLessThan(0);
    }

    @Test
    void compareDiffRandom() throws Exception {
        List<Block> allBlocks = Arrays.asList(Block.T, Block.I, Block.O, Block.S, Block.Z, Block.J, Block.L, Block.T, Block.I, Block.O, Block.S, Block.Z, Block.J, Block.L);

        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            List<Block> blocks1 = randoms.sample(allBlocks, randoms.nextInt(10));
            List<Block> blocks2 = randoms.sample(allBlocks, randoms.nextInt(10));

            if (blocks1.equals(blocks2))
                blocks1.add(Block.O);

            LongBlocks pieces1 = new LongBlocks(blocks1);
            LongBlocks pieces2 = new LongBlocks(blocks2);

            // assert is not 0 & sign reversed
            PiecesNumberComparator comparator = new PiecesNumberComparator();
            assertThat(comparator.compare(pieces1, pieces2) * comparator.compare(pieces2, pieces1))
                    .as(pieces2.toString())
                    .isLessThan(0);
        }
    }
}