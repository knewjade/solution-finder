package common.pattern;

import lib.MyIterables;
import common.SyntaxException;
import common.datastore.pieces.Blocks;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.mino.Block.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlocksGeneratorTest {
    @Test
    void toList1() {
        PiecesGenerator generator = new PiecesGenerator("I # comment");
        assertThat(generator.getDepth()).isEqualTo(1);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(1);
        assertThat(pieces.get(0).getBlocks()).isEqualTo(Collections.singletonList(I));
    }

    @Test
    void toList1LowerCase() {
        PiecesGenerator generator = new PiecesGenerator("i");
        assertThat(generator.getDepth()).isEqualTo(1);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(1);
        assertThat(pieces.get(0).getBlocks()).isEqualTo(Collections.singletonList(I));
    }

    @Test
    void toList2() {
        PiecesGenerator generator = new PiecesGenerator("I,J");
        assertThat(generator.getDepth()).isEqualTo(2);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(1);
        assertThat(pieces.get(0).getBlocks()).isEqualTo(Arrays.asList(I, J));
    }

    @Test
    void toList2WithSpace() {
        PiecesGenerator generator = new PiecesGenerator(" I , J ");
        assertThat(generator.getDepth()).isEqualTo(2);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(1);
        assertThat(pieces.get(0).getBlocks()).isEqualTo(Arrays.asList(I, J));
    }

    @Test
    void toListAsterisk() {
        PiecesGenerator generator = new PiecesGenerator(" * ");
        assertThat(generator.getDepth()).isEqualTo(1);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(7);
        for (Blocks piece : pieces)
            assertThat(piece.getBlocks().size()).isEqualTo(1);
    }

    @Test
    void toListAsterisk2() {
        PiecesGenerator generator = new PiecesGenerator(" *, * ");
        assertThat(generator.getDepth()).isEqualTo(2);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(49);
        for (Blocks piece : pieces)
            assertThat(piece.getBlocks().size()).isEqualTo(2);
    }

    @Test
    void toListSelector() {
        PiecesGenerator generator = new PiecesGenerator(" [TSZ] ");
        assertThat(generator.getDepth()).isEqualTo(1);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(3);
        assertThat(pieces.get(0).getBlocks()).isEqualTo(Collections.singletonList(T));
        assertThat(pieces.get(1).getBlocks()).isEqualTo(Collections.singletonList(S));
        assertThat(pieces.get(2).getBlocks()).isEqualTo(Collections.singletonList(Z));
    }

    @Test
    void toListSelector2() {
        PiecesGenerator generator = new PiecesGenerator(" [TsZ] , [IOjl]");
        assertThat(generator.getDepth()).isEqualTo(2);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(12);
        assertThat(pieces.get(0).getBlocks()).isEqualTo(Arrays.asList(T, I));
        assertThat(pieces.get(1).getBlocks()).isEqualTo(Arrays.asList(T, O));
        assertThat(pieces.get(2).getBlocks()).isEqualTo(Arrays.asList(T, J));
        assertThat(pieces.get(3).getBlocks()).isEqualTo(Arrays.asList(T, L));
        assertThat(pieces.get(4).getBlocks()).isEqualTo(Arrays.asList(S, I));
        assertThat(pieces.get(5).getBlocks()).isEqualTo(Arrays.asList(S, O));
        assertThat(pieces.get(6).getBlocks()).isEqualTo(Arrays.asList(S, J));
        assertThat(pieces.get(7).getBlocks()).isEqualTo(Arrays.asList(S, L));
        assertThat(pieces.get(8).getBlocks()).isEqualTo(Arrays.asList(Z, I));
        assertThat(pieces.get(9).getBlocks()).isEqualTo(Arrays.asList(Z, O));
        assertThat(pieces.get(10).getBlocks()).isEqualTo(Arrays.asList(Z, J));
        assertThat(pieces.get(11).getBlocks()).isEqualTo(Arrays.asList(Z, L));
    }

    @Test
    void toListSelectorWithPermutation() {
        PiecesGenerator generator = new PiecesGenerator(" [TSZ]p2 ");
        assertThat(generator.getDepth()).isEqualTo(2);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(6);
        assertThat(pieces.get(0).getBlocks()).isEqualTo(Arrays.asList(S, Z));
        assertThat(pieces.get(1).getBlocks()).isEqualTo(Arrays.asList(Z, S));
        assertThat(pieces.get(3).getBlocks()).isEqualTo(Arrays.asList(Z, T));
        assertThat(pieces.get(2).getBlocks()).isEqualTo(Arrays.asList(T, Z));
        assertThat(pieces.get(4).getBlocks()).isEqualTo(Arrays.asList(T, S));
        assertThat(pieces.get(5).getBlocks()).isEqualTo(Arrays.asList(S, T));
    }

    @Test
    void toListAsteriskWithPermutation() {
        PiecesGenerator generator = new PiecesGenerator(" *p4 ");
        assertThat(generator.getDepth()).isEqualTo(4);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(840);
    }

    @Test
    void toMultiList1() {
        List<String> patterns = Arrays.asList("I#comment", "T", "# comment");
        PiecesGenerator generator = new PiecesGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(1);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(2);
    }

    @Test
    void toMultiList2() {
        List<String> patterns = Arrays.asList("I, *p4", "*p5", "[TISZL]p5");
        PiecesGenerator generator = new PiecesGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(5);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(840 + 2520 + 120);
    }

    @Test
    void toMultiList3() {
        List<String> patterns = Arrays.asList(
                "I, I, *p3",
                "I, S, *p3",
                "",
                "I, Z, *p3",
                "",
                "I, [TOLJ], *p3"
        );
        PiecesGenerator generator = new PiecesGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(5);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(210 + 210 + 210 + 840);
    }

    @Test
    void toMultiList4() {
        List<String> patterns = Arrays.asList("T,T", "Z,* # comment");
        PiecesGenerator generator = new PiecesGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(2);

        List<Blocks> pieces = MyIterables.toList(generator);
        assertThat(pieces.size()).isEqualTo(8);
    }

    @Test
    void errorOverPopPermutation() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" *p8 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorLessPopPermutation() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" *p0 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorIllegalNumberPermutation() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" *pf ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorIllegalNumber2Permutation() {
        assertThrows(SyntaxException.class, () -> {

            try {
                PiecesGenerator.verify(" *7p4 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorNoP() {
        assertThrows(SyntaxException.class, () -> {

            try {
                PiecesGenerator.verify(" *6 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorNoBlocksPermutation() {
        assertThrows(SyntaxException.class, () -> {

            try {
                PiecesGenerator.verify(" []p1 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorAsteriskAndBlocksPermutation() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [SZT]*p2 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorUnknownAndBlocksPermutation() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [SZT]kp2 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorAsteriskAndBlocksPermutation2() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" *[SZT]p2 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorUnknownAndBlocksPermutation2() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" z[SZT]p2 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorNoCloseBracketPermutation() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [TSZ  p1 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorOverOpenBracketPermutation1() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [[TSZ]  p1 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorOverOpenBracketPermutation2() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [TS[Z]  p1 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorIllegalPositionOfBracketPermutation1() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" T[SZ]  p1 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorIllegalPositionOfBracketPermutation2() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [TSZ  p1] ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorNoOpenBracketPermutation() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" TSZ]  p1 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorOverCloseBracketPermutation1() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [TSZ]  p1 ]");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorOverCloseBracketPermutation2() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [TSZ]]  p1 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorIllegalNumberSelector() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [T SZ LJ I]pk ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorSelectorNoP() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [TIJ]2 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorEmptySelector() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [  ] ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorIllegalBlockSelector() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" [X] ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorIllegalBlock() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" X ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorOverBlock() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(" T S ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorDifferentDepthList() {
        assertThrows(SyntaxException.class, () -> {
            try {
                PiecesGenerator.verify(Arrays.asList(" T ,S ", "", "I"));
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }
}