package common.tree;

import common.SyntaxException;
import common.datastore.blocks.LongPieces;
import common.pattern.LoadedPatternGenerator;
import common.pattern.PatternGenerator;
import core.mino.Piece;
import lib.Randoms;
import module.LongTest;
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
        tree.success(Arrays.asList(Piece.I, Piece.T, Piece.O));
        assertThat(tree.isVisited(Arrays.asList(Piece.I, Piece.T, Piece.O))).isTrue();
        assertThat(tree.isSucceed(Arrays.asList(Piece.I, Piece.T, Piece.O))).isEqualTo(VisitedTree.SUCCEED);
    }

    @Test
    void fail() {
        VisitedTree tree = new VisitedTree();
        tree.fail(Arrays.asList(Piece.Z, Piece.J, Piece.L));
        assertThat(tree.isVisited(Arrays.asList(Piece.Z, Piece.J, Piece.L))).isTrue();
        assertThat(tree.isSucceed(Arrays.asList(Piece.Z, Piece.J, Piece.L))).isEqualTo(VisitedTree.FAILED);
    }

    @Test
    void notVisited() {
        VisitedTree tree = new VisitedTree();
        assertThat(tree.isVisited(Arrays.asList(Piece.O, Piece.O, Piece.O))).isFalse();
        assertThat(tree.isSucceed(Arrays.asList(Piece.O, Piece.O, Piece.O))).isEqualTo(VisitedTree.NO_RESULT);
    }

    @Test
    void random() throws SyntaxException {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 7; size++) {
            PatternGenerator generator = new LoadedPatternGenerator("*p" + size);

            VisitedTree tree = new VisitedTree();

            HashSet<LongPieces> success = new HashSet<>();
            HashSet<LongPieces> failed = new HashSet<>();
            generator.blocksStream()
                    .forEach(pieces -> {
                        boolean flag = randoms.nextBoolean();
                        List<Piece> blocks = pieces.getPieces();
                        tree.set(flag, blocks);

                        LongPieces longPieces = new LongPieces(blocks);
                        if (flag) {
                            success.add(longPieces);
                        } else {
                            failed.add(longPieces);
                        }
                    });

            boolean isSucceed = success.stream()
                    .allMatch(pieces -> {
                        List<Piece> blocks = pieces.getPieces();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.SUCCEED;
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Piece> blocks = pieces.getPieces();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.FAILED;
                    });
            assertThat(isFailed).isTrue();
        }
    }

    @Test
    @LongTest
    void randomLong() throws SyntaxException {
        Randoms randoms = new Randoms();
        for (int size = 8; size <= 10; size++) {
            PatternGenerator generator = new LoadedPatternGenerator("*p7, *p" + (size - 7));

            VisitedTree tree = new VisitedTree();

            HashSet<LongPieces> success = new HashSet<>();
            HashSet<LongPieces> failed = new HashSet<>();
            generator.blocksStream()
                    .forEach(pieces -> {
                        boolean flag = randoms.nextBoolean();
                        List<Piece> blocks = pieces.getPieces();
                        tree.set(flag, blocks);

                        LongPieces longPieces = new LongPieces(blocks);
                        if (flag) {
                            success.add(longPieces);
                        } else {
                            failed.add(longPieces);
                        }
                    });

            boolean isSucceed = success.stream()
                    .allMatch(pieces -> {
                        List<Piece> blocks = pieces.getPieces();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.SUCCEED;
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Piece> blocks = pieces.getPieces();
                        return tree.isSucceed(blocks) == ConcurrentVisitedTree.FAILED;
                    });
            assertThat(isFailed).isTrue();
        }
    }
}