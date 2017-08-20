package common.tree;

import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.pattern.BlocksGenerator;
import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentVisitedTreeTest {
    @Test
    void success() {
        ConcurrentVisitedTree tree = new ConcurrentVisitedTree();
        tree.success(Arrays.asList(Block.I, Block.T, Block.O));
        assertThat(tree.isSucceed(Arrays.asList(Block.I, Block.T, Block.O))).isEqualTo(ConcurrentVisitedTree.SUCCEED);
    }

    @Test
    void fail() {
        ConcurrentVisitedTree tree = new ConcurrentVisitedTree();
        tree.fail(Arrays.asList(Block.Z, Block.J, Block.L));
        assertThat(tree.isSucceed(Arrays.asList(Block.Z, Block.J, Block.L))).isEqualTo(ConcurrentVisitedTree.FAILED);
    }

    @Test
    void notVisited() {
        ConcurrentVisitedTree tree = new ConcurrentVisitedTree();
        assertThat(tree.isSucceed(Arrays.asList(Block.O, Block.O, Block.O))).isEqualTo(ConcurrentVisitedTree.NO_RESULT);
    }

    @Test
    void random() {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 7; size++) {
            BlocksGenerator generator = new BlocksGenerator("*p" + size);

            ConcurrentVisitedTree tree = new ConcurrentVisitedTree();

            Set<LongBlocks> success = Collections.synchronizedSet(new HashSet<>());
            Set<LongBlocks> failed = Collections.synchronizedSet(new HashSet<>());
            List<Blocks> blocksList = generator.stream().collect(Collectors.toList());
            blocksList.parallelStream()
                    .forEach(pieces -> {
                        boolean flag = randoms.nextBoolean();
                        List<Block> blocks = pieces.getBlockList();
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
                        List<Block> blocks = pieces.getBlockList();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.SUCCEED;
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlockList();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.FAILED;
                    });
            assertThat(isFailed).isTrue();
        }
    }

    @Test
    @Tag("long")
    void randomLong() {
        Randoms randoms = new Randoms();
        for (int size = 8; size <= 11; size++) {
            BlocksGenerator generator = new BlocksGenerator("*p7, *p" + (size - 7));

            ConcurrentVisitedTree tree = new ConcurrentVisitedTree();

            Set<LongBlocks> success = Collections.synchronizedSet(new HashSet<>());
            Set<LongBlocks> failed = Collections.synchronizedSet(new HashSet<>());
            List<Blocks> blocksList = generator.stream().collect(Collectors.toList());
            blocksList.parallelStream()
                    .forEach(pieces -> {
                        boolean flag = randoms.nextBoolean();
                        List<Block> blocks = pieces.getBlockList();
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
                        List<Block> blocks = pieces.getBlockList();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.SUCCEED;
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlockList();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.FAILED;
                    });
            assertThat(isFailed).isTrue();
        }
    }
}