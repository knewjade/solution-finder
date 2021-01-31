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
import static org.assertj.core.api.Assertions.offset;

class AnalyzeTreeTest {
    @Test
    void success() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.success(Arrays.asList(Piece.I, Piece.T, Piece.O));
        assertThat(tree.isVisited(Arrays.asList(Piece.I, Piece.T, Piece.O))).isTrue();
        assertThat(tree.isSucceed(Arrays.asList(Piece.I, Piece.T, Piece.O))).isTrue();
    }

    @Test
    void fail() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.fail(Arrays.asList(Piece.Z, Piece.J, Piece.L));
        assertThat(tree.isVisited(Arrays.asList(Piece.Z, Piece.J, Piece.L))).isTrue();
        assertThat(tree.isSucceed(Arrays.asList(Piece.Z, Piece.J, Piece.L))).isFalse();
    }

    @Test
    void notVisited() {
        AnalyzeTree tree = new AnalyzeTree();
        assertThat(tree.isVisited(Arrays.asList(Piece.O, Piece.O, Piece.O))).isFalse();
        assertThat(tree.isSucceed(Arrays.asList(Piece.O, Piece.O, Piece.O))).isFalse();
    }

    @Test
    void show1() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.success(Arrays.asList(Piece.I, Piece.T, Piece.O));
        tree.success(Arrays.asList(Piece.I, Piece.T, Piece.J));
        tree.success(Arrays.asList(Piece.I, Piece.T, Piece.L));
        tree.fail(Arrays.asList(Piece.I, Piece.T, Piece.S));
        tree.fail(Arrays.asList(Piece.I, Piece.T, Piece.Z));

        assertThat(tree.getSuccessPercent()).isEqualTo(0.6);
        assertThat(tree.show())
                .contains("60.00%")
                .contains("3/5");
    }

    @Test
    void show2() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.success(Arrays.asList(Piece.I, Piece.T));
        tree.success(Arrays.asList(Piece.I, Piece.S));
        tree.success(Arrays.asList(Piece.S, Piece.T));
        tree.success(Arrays.asList(Piece.S, Piece.O));
        tree.success(Arrays.asList(Piece.S, Piece.J));
        tree.success(Arrays.asList(Piece.O, Piece.I));
        tree.fail(Arrays.asList(Piece.I, Piece.I));
        tree.fail(Arrays.asList(Piece.I, Piece.Z));
        tree.fail(Arrays.asList(Piece.Z, Piece.I));
        tree.fail(Arrays.asList(Piece.Z, Piece.L));
        tree.fail(Arrays.asList(Piece.Z, Piece.L));  // same

        assertThat(tree.getSuccessPercent()).isCloseTo(0.5454545, offset(0.0000001));
        assertThat(tree.show())
                .contains("54.55%")
                .contains("6/11");
    }

    @Test
    void random() throws SyntaxException {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 7; size++) {
            PatternGenerator generator = new LoadedPatternGenerator("*p" + size);

            AnalyzeTree tree = new AnalyzeTree();
            HashSet<LongPieces> success = new HashSet<>();
            HashSet<LongPieces> failed = new HashSet<>();

            generator.blocksStream()
                    .forEach(blocks -> {
                        boolean flag = randoms.nextBoolean();
                        List<Piece> pieceList = blocks.getPieces();
                        tree.set(flag, pieceList);

                        LongPieces longPieces = new LongPieces(pieceList);
                        if (flag) {
                            success.add(longPieces);
                        } else {
                            failed.add(longPieces);
                        }
                    });

            boolean isSucceed = success.stream()
                    .allMatch(pieces -> {
                        List<Piece> blocks = pieces.getPieces();
                        return tree.isVisited(blocks) && tree.isSucceed(blocks);
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Piece> blocks = pieces.getPieces();
                        return tree.isVisited(blocks) && !tree.isSucceed(blocks);
                    });
            assertThat(isFailed).isTrue();

            double percent = (double) success.size() / (success.size() + failed.size());
            assertThat(tree.getSuccessPercent()).isCloseTo(percent, offset(0.0001));
        }
    }

    @Test
    @LongTest
    void randomLong() throws SyntaxException {
        Randoms randoms = new Randoms();
        for (int size = 8; size <= 10; size++) {
            PatternGenerator generator = new LoadedPatternGenerator("*p7, *p" + (size - 7));

            AnalyzeTree tree = new AnalyzeTree();
            HashSet<LongPieces> success = new HashSet<>();
            HashSet<LongPieces> failed = new HashSet<>();
            generator.blocksStream()
                    .forEach(blocks -> {
                        boolean flag = randoms.nextBoolean();
                        List<Piece> pieceList = blocks.getPieces();
                        tree.set(flag, pieceList);

                        LongPieces longPieces = new LongPieces(pieceList);
                        if (flag) {
                            success.add(longPieces);
                        } else {
                            failed.add(longPieces);
                        }
                    });

            boolean isSucceed = success.stream()
                    .allMatch(pieces -> {
                        List<Piece> blocks = pieces.getPieces();
                        return tree.isVisited(blocks) && tree.isSucceed(blocks);
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Piece> blocks = pieces.getPieces();
                        return tree.isVisited(blocks) && !tree.isSucceed(blocks);
                    });
            assertThat(isFailed).isTrue();

            double percent = (double) success.size() / (success.size() + failed.size());
            assertThat(tree.getSuccessPercent()).isCloseTo(percent, offset(0.0001));
        }
    }

    @Test
    void tree() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.success(Arrays.asList(Piece.S, Piece.S));
        tree.success(Arrays.asList(Piece.S, Piece.T));
        tree.fail(Arrays.asList(Piece.S, Piece.T));
        tree.success(Arrays.asList(Piece.Z, Piece.Z));
        tree.fail(Arrays.asList(Piece.O, Piece.O));

        assertThat(tree.tree(1))
                .contains("60.00 %")
                .contains("S -> 66.67 %")
                .contains("Z -> 100.00 %")
                .contains("O -> 0.00 %")
                .doesNotContain("L")
                .doesNotContain("J");

        assertThat(tree.tree(2))
                .contains("60.00 %")
                .contains("SS -> 100.00 %")
                .contains("ST -> 50.00 %")
                .contains("ZZ -> 100.00 %")
                .contains("OO -> 0.00 %")
                .doesNotContain("L")
                .doesNotContain("J");

        assertThat(tree.tree(-1)).isEqualTo(tree.tree(2));
    }
}