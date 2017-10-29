package common.tree;

import common.SyntaxException;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.pattern.LoadedPatternGenerator;
import common.pattern.PatternGenerator;
import core.mino.Piece;
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
        tree.success(Arrays.asList(Piece.I, Piece.T, Piece.O));
        assertThat(tree.isSucceed(Arrays.asList(Piece.I, Piece.T, Piece.O))).isEqualTo(ConcurrentVisitedTree.SUCCEED);
    }

    @Test
    void fail() {
        ConcurrentVisitedTree tree = new ConcurrentVisitedTree();
        tree.fail(Arrays.asList(Piece.Z, Piece.J, Piece.L));
        assertThat(tree.isSucceed(Arrays.asList(Piece.Z, Piece.J, Piece.L))).isEqualTo(ConcurrentVisitedTree.FAILED);
    }

    @Test
    void notVisited() {
        ConcurrentVisitedTree tree = new ConcurrentVisitedTree();
        assertThat(tree.isSucceed(Arrays.asList(Piece.O, Piece.O, Piece.O))).isEqualTo(ConcurrentVisitedTree.NO_RESULT);
    }

    @Test
    void random() throws SyntaxException {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 7; size++) {
            PatternGenerator generator = new LoadedPatternGenerator("*p" + size);

            ConcurrentVisitedTree tree = new ConcurrentVisitedTree();

            Set<LongPieces> success = Collections.synchronizedSet(new HashSet<>());
            Set<LongPieces> failed = Collections.synchronizedSet(new HashSet<>());
            List<Pieces> piecesList = generator.blocksStream().collect(Collectors.toList());
            piecesList.parallelStream()
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
    @Tag("long")
    void randomLong() throws SyntaxException {
        Randoms randoms = new Randoms();
        for (int size = 8; size <= 11; size++) {
            PatternGenerator generator = new LoadedPatternGenerator("*p7, *p" + (size - 7));

            ConcurrentVisitedTree tree = new ConcurrentVisitedTree();

            Set<LongPieces> success = Collections.synchronizedSet(new HashSet<>());
            Set<LongPieces> failed = Collections.synchronizedSet(new HashSet<>());
            List<Pieces> piecesList = generator.blocksStream().collect(Collectors.toList());
            piecesList.parallelStream()
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