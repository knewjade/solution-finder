package common.pattern;

import common.SyntaxException;
import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static core.mino.Block.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlocksGeneratorTest {
    @Test
    void toList1() {
        BlocksGenerator generator = new BlocksGenerator("I # comment");
        assertThat(generator.getDepth()).isEqualTo(1);

        assertThat(generator.blocksStream())
                .hasSize(1)
                .containsExactly(new LongBlocks(Collections.singletonList(I)));

        assertThat(generator.blockCountersStream())
                .hasSize(1)
                .containsExactly(new BlockCounter(Collections.singletonList(I)));
    }

    @Test
    void toList1LowerCase() {
        BlocksGenerator generator = new BlocksGenerator("i");
        assertThat(generator.getDepth()).isEqualTo(1);

        assertThat(generator.blocksStream())
                .hasSize(1)
                .containsExactly(new LongBlocks(Collections.singletonList(I)));

        assertThat(generator.blockCountersStream())
                .hasSize(1)
                .containsExactly(new BlockCounter(Collections.singletonList(I)));
    }

    @Test
    void toList2() {
        BlocksGenerator generator = new BlocksGenerator("I,J");
        assertThat(generator.getDepth()).isEqualTo(2);

        assertThat(generator.blocksStream())
                .hasSize(1)
                .containsExactly(new LongBlocks(Arrays.asList(I, J)));

        assertThat(generator.blockCountersStream())
                .hasSize(1)
                .containsExactly(new BlockCounter(Arrays.asList(I, J)));
    }

    @Test
    void toList2WithSpace() {
        BlocksGenerator generator = new BlocksGenerator(" I , J ");
        assertThat(generator.getDepth()).isEqualTo(2);

        assertThat(generator.blocksStream())
                .hasSize(1)
                .containsExactly(new LongBlocks(Arrays.asList(I, J)));

        assertThat(generator.blockCountersStream())
                .hasSize(1)
                .containsExactly(new BlockCounter(Arrays.asList(I, J)));
    }

    @Test
    void toListAsterisk() {
        BlocksGenerator generator = new BlocksGenerator(" * ");
        assertThat(generator.getDepth()).isEqualTo(1);

        assertThat(generator.blocksStream())
                .hasSize(7)
                .allMatch(element -> element.getBlocks().size() == 1);

        assertThat(generator.blockCountersStream())
                .hasSize(7)
                .allMatch(element -> element.getBlocks().size() == 1);
    }

    @Test
    void toListAsterisk2() {
        BlocksGenerator generator = new BlocksGenerator(" *, * ");
        assertThat(generator.getDepth()).isEqualTo(2);

        assertThat(generator.blockCountersStream())
                .hasSize(49)
                .allMatch(element -> element.getBlocks().size() == 2);

        assertThat(generator.blockCountersStream())
                .hasSize(49)
                .allMatch(element -> element.getBlocks().size() == 2);
    }

    @Test
    void toListSelector() {
        BlocksGenerator generator = new BlocksGenerator(" [TSZ] ");
        assertThat(generator.getDepth()).isEqualTo(1);

        assertThat(generator.blocksStream())
                .hasSize(3)
                .contains(new LongBlocks(Collections.singletonList(T)))
                .contains(new LongBlocks(Collections.singletonList(S)))
                .contains(new LongBlocks(Collections.singletonList(Z)));

        assertThat(generator.blockCountersStream())
                .hasSize(3)
                .contains(new BlockCounter(Collections.singletonList(T)))
                .contains(new BlockCounter(Collections.singletonList(S)))
                .contains(new BlockCounter(Collections.singletonList(Z)));
    }

    @Test
    void toListSelector2() {
        BlocksGenerator generator = new BlocksGenerator(" [TsZ] , [IOjl]");
        assertThat(generator.getDepth()).isEqualTo(2);

        assertThat(generator.blocksStream())
                .hasSize(12)
                .contains(new LongBlocks(Arrays.asList(T, I)))
                .contains(new LongBlocks(Arrays.asList(T, O)))
                .contains(new LongBlocks(Arrays.asList(T, J)))
                .contains(new LongBlocks(Arrays.asList(T, L)))
                .contains(new LongBlocks(Arrays.asList(S, I)))
                .contains(new LongBlocks(Arrays.asList(S, O)))
                .contains(new LongBlocks(Arrays.asList(S, J)))
                .contains(new LongBlocks(Arrays.asList(S, L)))
                .contains(new LongBlocks(Arrays.asList(Z, I)))
                .contains(new LongBlocks(Arrays.asList(Z, O)))
                .contains(new LongBlocks(Arrays.asList(Z, J)))
                .contains(new LongBlocks(Arrays.asList(Z, L)));

        assertThat(generator.blockCountersStream())
                .hasSize(12)
                .contains(new BlockCounter(Arrays.asList(T, I)))
                .contains(new BlockCounter(Arrays.asList(T, O)))
                .contains(new BlockCounter(Arrays.asList(T, J)))
                .contains(new BlockCounter(Arrays.asList(T, L)))
                .contains(new BlockCounter(Arrays.asList(S, I)))
                .contains(new BlockCounter(Arrays.asList(S, O)))
                .contains(new BlockCounter(Arrays.asList(S, J)))
                .contains(new BlockCounter(Arrays.asList(S, L)))
                .contains(new BlockCounter(Arrays.asList(Z, I)))
                .contains(new BlockCounter(Arrays.asList(Z, O)))
                .contains(new BlockCounter(Arrays.asList(Z, J)))
                .contains(new BlockCounter(Arrays.asList(Z, L)));
    }

    @Test
    void toListSelectorWithPermutation() {
        BlocksGenerator generator = new BlocksGenerator(" [TSZ]p2 ");
        assertThat(generator.getDepth()).isEqualTo(2);

        assertThat(generator.blocksStream())
                .hasSize(6)
                .contains(new LongBlocks(Arrays.asList(S, Z)))
                .contains(new LongBlocks(Arrays.asList(S, T)))
                .contains(new LongBlocks(Arrays.asList(Z, S)))
                .contains(new LongBlocks(Arrays.asList(Z, T)))
                .contains(new LongBlocks(Arrays.asList(T, Z)))
                .contains(new LongBlocks(Arrays.asList(T, S)));

        assertThat(generator.blockCountersStream())
                .hasSize(3)
                .contains(new BlockCounter(Arrays.asList(T, S)))
                .contains(new BlockCounter(Arrays.asList(T, Z)))
                .contains(new BlockCounter(Arrays.asList(S, Z)));
    }

    @Test
    void toListAsteriskWithPermutation() {
        BlocksGenerator generator = new BlocksGenerator(" *p4 ");
        assertThat(generator.getDepth()).isEqualTo(4);

        assertThat(generator.blocksStream()).hasSize(840);
        assertThat(generator.blockCountersStream()).hasSize(35);
    }

    @Test
    void toMultiList1() {
        List<String> patterns = Arrays.asList("I#comment", "T", "# comment");
        BlocksGenerator generator = new BlocksGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(1);

        assertThat(generator.blocksStream()).hasSize(2);
        assertThat(generator.blockCountersStream()).hasSize(2);
    }

    @Test
    void toMultiList2() {
        List<String> patterns = Arrays.asList("I, *p4", "*p5", "[TISZL]p5");
        BlocksGenerator generator = new BlocksGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(5);

        assertThat(generator.blocksStream()).hasSize(840 + 2520 + 120);
        assertThat(generator.blockCountersStream()).hasSize(35 + 21 + 1);

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
        BlocksGenerator generator = new BlocksGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(5);

        assertThat(generator.blocksStream()).hasSize(210 + 210 + 210 + 840);
        assertThat(generator.blockCountersStream()).hasSize(35 + 35 + 35 + 140);

    }

    @Test
    void toMultiList4() {
        List<String> patterns = Arrays.asList("T,T", "Z,* # comment");
        BlocksGenerator generator = new BlocksGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(2);

        assertThat(generator.blocksStream()).hasSize(8);
        assertThat(generator.blockCountersStream()).hasSize(8);
    }

    @Test
    void factorial1() {
        List<String> patterns = Collections.singletonList("[SZO]!");
        BlocksGenerator generator = new BlocksGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(3);

        assertThat(generator.blocksStream()).hasSize(6);
        assertThat(generator.blockCountersStream()).hasSize(1);
    }

    @Test
    void factorial2() {
        List<String> patterns = Collections.singletonList("*!");
        BlocksGenerator generator = new BlocksGenerator(patterns);
        assertThat(generator.getDepth()).isEqualTo(7);

        assertThat(generator.blocksStream()).hasSize(5040);
        assertThat(generator.blockCountersStream()).hasSize(1);
    }

    @Test
    void singleQuote() {
        BlocksGenerator generator = new BlocksGenerator(" ' *p3, *p2 ' ");
        assertThat(generator.getDepth()).isEqualTo(5);

        List<Blocks> blocks = generator.blocksStream().collect(Collectors.toList());
        assertThat(blocks).hasSize(7 * 6 * 5 * 7 * 6);
    }

    @Test
    void errorOverPopPermutation() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" *p8 ");
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
                BlocksGenerator.verify(" *p0 ");
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
                BlocksGenerator.verify(" *pf ");
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
                BlocksGenerator.verify(" *7p4 ");
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
                BlocksGenerator.verify(" *6 ");
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
                BlocksGenerator.verify(" []p1 ");
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
                BlocksGenerator.verify(" [SZT]*p2 ");
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
                BlocksGenerator.verify(" [SZT]kp2 ");
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
                BlocksGenerator.verify(" *[SZT]p2 ");
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
                BlocksGenerator.verify(" z[SZT]p2 ");
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
                BlocksGenerator.verify(" [TSZ  p1 ");
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
                BlocksGenerator.verify(" [[TSZ]  p1 ");
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
                BlocksGenerator.verify(" [TS[Z]  p1 ");
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
                BlocksGenerator.verify(" T[SZ]  p1 ");
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
                BlocksGenerator.verify(" [TSZ  p1] ");
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
                BlocksGenerator.verify(" TSZ]  p1 ");
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
                BlocksGenerator.verify(" [TSZ]  p1 ]");
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
                BlocksGenerator.verify(" [TSZ]]  p1 ");
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
                BlocksGenerator.verify(" [T SZ LJ I]pk ");
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
                BlocksGenerator.verify(" [TIJ]2 ");
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
                BlocksGenerator.verify(" [  ] ");
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
                BlocksGenerator.verify(" [X] ");
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
                BlocksGenerator.verify(" X ");
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
                BlocksGenerator.verify(" T S ");
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
                BlocksGenerator.verify(Arrays.asList(" T ,S ", "", "I"));
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorFactorial1() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" [SZ]p! ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorFactorial2() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" [SZ]!p ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorFactorial3() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" [SZ]x! ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorFactorial4() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" *p! ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorFactorial5() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" *!p ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorDuplicateBlock() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" [SS]p2 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorSingleQuote1() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" '*p4' ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    @Test
    void errorSingleQuote2() {
        assertThrows(SyntaxException.class, () -> {
            try {
                BlocksGenerator.verify(" '*p4 ");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }
}