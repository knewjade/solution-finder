package common.tree;

import common.SyntaxException;
import common.datastore.pieces.LongBlocks;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisitedTreeTest {
    @Test
    void success() {
        VisitedTree tree = new VisitedTree();
        tree.success(Arrays.asList(Block.I, Block.T, Block.O));
        assertThat(tree.isVisited(Arrays.asList(Block.I, Block.T, Block.O))).isTrue();
        assertThat(tree.isSucceed(Arrays.asList(Block.I, Block.T, Block.O))).isEqualTo(VisitedTree.SUCCEED);
    }

    @Test
    void fail() {
        VisitedTree tree = new VisitedTree();
        tree.fail(Arrays.asList(Block.Z, Block.J, Block.L));
        assertThat(tree.isVisited(Arrays.asList(Block.Z, Block.J, Block.L))).isTrue();
        assertThat(tree.isSucceed(Arrays.asList(Block.Z, Block.J, Block.L))).isEqualTo(VisitedTree.FAILED);
    }

    @Test
    void notVisited() {
        VisitedTree tree = new VisitedTree();
        assertThat(tree.isVisited(Arrays.asList(Block.O, Block.O, Block.O))).isFalse();
        assertThat(tree.isSucceed(Arrays.asList(Block.O, Block.O, Block.O))).isEqualTo(VisitedTree.NO_RESULT);
    }

    @Test
    void random() throws SyntaxException {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 7; size++) {
            IBlocksGenerator generator = new BlocksGenerator("*p" + size);

            VisitedTree tree = new VisitedTree();

            HashSet<LongBlocks> success = new HashSet<>();
            HashSet<LongBlocks> failed = new HashSet<>();
            generator.blocksStream()
                    .forEach(pieces -> {
                        boolean flag = randoms.nextBoolean();
                        List<Block> blocks = pieces.getBlocks();
                        tree.set(flag, blocks);

                        LongBlocks longPieces = new LongBlocks(blocks);
                        if (flag) {
                            success.add(longPieces);
                        } else {
                            failed.add(longPieces);
                        }
                    });

            boolean isSucceed = success.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlocks();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.SUCCEED;
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlocks();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.FAILED;
                    });
            assertThat(isFailed).isTrue();
        }
    }

    @Test
    @Tag("long")
    void randomLong() throws SyntaxException {
        Randoms randoms = new Randoms();
        for (int size = 8; size <= 11; size++) {
            IBlocksGenerator generator = new BlocksGenerator("*p7, *p" + (size - 7));

            VisitedTree tree = new VisitedTree();

            HashSet<LongBlocks> success = new HashSet<>();
            HashSet<LongBlocks> failed = new HashSet<>();
            generator.blocksStream()
                    .forEach(pieces -> {
                        boolean flag = randoms.nextBoolean();
                        List<Block> blocks = pieces.getBlocks();
                        tree.set(flag, blocks);

                        LongBlocks longPieces = new LongBlocks(blocks);
                        if (flag) {
                            success.add(longPieces);
                        } else {
                            failed.add(longPieces);
                        }
                    });

            boolean isSucceed = success.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlocks();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.SUCCEED;
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlocks();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.FAILED;
                    });
            assertThat(isFailed).isTrue();
        }
    }
}