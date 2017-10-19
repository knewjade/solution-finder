package common.pattern;

import common.SyntaxException;
import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static core.mino.Block.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlocksGeneratorTest {
    @Nested
    class Regular {
        private void assertSame(IBlocksGenerator actual, IBlocksGenerator expected) {
            assertThat(actual.getDepth()).isEqualTo(expected.getDepth());

            List<Blocks> blocks = expected.blocksStream().collect(Collectors.toList());
            assertThat(actual.blocksStream())
                    .hasSize(blocks.size())
                    .containsAll(blocks);

            List<BlockCounter> blockCounters = expected.blockCountersStream().collect(Collectors.toList());
            assertThat(actual.blockCountersStream())
                    .hasSize(blockCounters.size())
                    .containsAll(blockCounters);
        }

        @Test
        void toList1() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("I");
            assertThat(generator.getDepth()).isEqualTo(1);

            assertThat(generator.blocksStream())
                    .hasSize(1)
                    .containsExactly(new LongBlocks(Collections.singletonList(I)));

            assertThat(generator.blockCountersStream())
                    .hasSize(1)
                    .containsExactly(new BlockCounter(Collections.singletonList(I)));
        }

        @Test
        void toList1WithComment() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("I # comment");
            assertSame(generator, new BlocksGenerator("I"));
        }

        @Test
        void toList1LowerCase() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("i");
            assertSame(generator, new BlocksGenerator("I"));
        }

        @Test
        void toList2() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("I,J");
            assertThat(generator.getDepth()).isEqualTo(2);

            assertThat(generator.blocksStream())
                    .hasSize(1)
                    .containsExactly(new LongBlocks(Arrays.asList(I, J)));

            assertThat(generator.blockCountersStream())
                    .hasSize(1)
                    .containsExactly(new BlockCounter(Arrays.asList(I, J)));
        }

        @Test
        void toList2WithSpace() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator(" I , J ");
            assertSame(generator, new BlocksGenerator("I,J"));
        }

        @Test
        void toList2WithSpace2() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator(" I J ");
            assertSame(generator, new BlocksGenerator("I,J"));
        }

        @Test
        void toList2WithoutSeparator() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator(" IJ ");
            assertSame(generator, new BlocksGenerator("I,J"));
        }

        @Test
        void toListAsterisk() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("*");
            assertThat(generator.getDepth()).isEqualTo(1);

            assertThat(generator.blocksStream())
                    .hasSize(7)
                    .allMatch(element -> element.getBlocks().size() == 1);

            assertThat(generator.blockCountersStream())
                    .hasSize(7)
                    .allMatch(element -> element.getBlocks().size() == 1);
        }

        @Test
        void toListAsterisk2() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("*,*");
            assertThat(generator.getDepth()).isEqualTo(2);

            assertThat(generator.blockCountersStream())
                    .hasSize(49)
                    .allMatch(element -> element.getBlocks().size() == 2);

            assertThat(generator.blockCountersStream())
                    .hasSize(49)
                    .allMatch(element -> element.getBlocks().size() == 2);
        }

        @Test
        void toListAsterisk2WithoutSeparator() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("**");
            assertSame(generator, new BlocksGenerator("*,*"));
        }

        @Test
        void toListAsterisk2WithSpace() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator(" * * ");
            assertSame(generator, new BlocksGenerator("*,*"));
        }

        @Test
        void toListSelector() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("[TSZ]");
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
        void toListSelectorWithSpace() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("[ T S Z ]");
            assertSame(generator, new BlocksGenerator("[TSZ]"));
        }

        @Test
        void toListSelector2() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("[TSZ],[IOJL]");
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
        void toListSelector2WithLowercase() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator(" [TsZ] , [IOjl]");
            assertSame(generator, new BlocksGenerator("[TSZ],[IOJL]"));
        }

        @Test
        void toListSelector2WithSpace() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator(" [ T S Z ] , [ I O J L ]");
            assertSame(generator, new BlocksGenerator("[TSZ],[IOJL]"));
        }

        @Test
        void toListSelectorWithPermutation() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("[TSZ]p2");
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
        void toListAsteriskWithPermutation() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator("*p4");
            assertSame(generator, new BlocksGenerator("[TIOJLSZ]p4"));
        }

        @Test
        void toMultiList1() throws SyntaxException {
            List<String> patterns = Arrays.asList("I#comment", "T", "# comment");
            IBlocksGenerator generator = new BlocksGenerator(patterns);
            assertThat(generator.getDepth()).isEqualTo(1);

            assertThat(generator.blocksStream()).hasSize(2);
            assertThat(generator.blockCountersStream()).hasSize(2);
        }

        @Test
        void toMultiList2() throws SyntaxException {
            List<String> patterns = Arrays.asList("I, *p4", "*p5", "[TISZL]p5");
            IBlocksGenerator generator = new BlocksGenerator(patterns);
            assertThat(generator.getDepth()).isEqualTo(5);

            assertThat(generator.blocksStream()).hasSize(840 + 2520 + 120);
            assertThat(generator.blockCountersStream()).hasSize(35 + 21 + 1);

        }

        @Test
        void toMultiList3() throws SyntaxException {
            List<String> patterns = Arrays.asList(
                    "I, I, *p3",
                    "I, S, *p3",
                    "",
                    "I, Z, *p3",
                    "",
                    "I, [TOLJ], *p3"
            );
            IBlocksGenerator generator = new BlocksGenerator(patterns);
            assertThat(generator.getDepth()).isEqualTo(5);

            assertThat(generator.blocksStream()).hasSize(210 + 210 + 210 + 840);
            assertThat(generator.blockCountersStream()).hasSize(35 + 35 + 35 + 140);

        }

        @Test
        void toMultiList4() throws SyntaxException {
            List<String> patterns = Arrays.asList("T,T", "Z,* # comment");
            IBlocksGenerator generator = new BlocksGenerator(patterns);
            assertThat(generator.getDepth()).isEqualTo(2);

            assertThat(generator.blocksStream()).hasSize(8);
            assertThat(generator.blockCountersStream()).hasSize(8);
        }

        @Test
        void factorial1() throws SyntaxException {
            List<String> patterns = Collections.singletonList("[SZO]!");
            IBlocksGenerator generator = new BlocksGenerator(patterns);
            assertSame(generator, new BlocksGenerator("[SZO]p3"));
        }

        @Test
        void factorial2() throws SyntaxException {
            List<String> patterns = Collections.singletonList("*!");
            IBlocksGenerator generator = new BlocksGenerator(patterns);
            assertSame(generator, new BlocksGenerator("[TIOSZLJ]p7"));
        }

        @Test
        void complexWithoutComma() throws SyntaxException {
            List<String> patterns = Collections.singletonList("TI[SZ]p1**p3[S]!O[SZ]");
            IBlocksGenerator generator = new BlocksGenerator(patterns);
            assertSame(generator, new BlocksGenerator("T,I,[SZ]p1,*,*p3,[S]!,O,[SZ]"));
        }

        @Test
        void singleQuote() throws SyntaxException {
            IBlocksGenerator generator = new BlocksGenerator(" ' *p3, *p2 ' ");
            assertThat(generator.getDepth()).isEqualTo(5);

            List<Blocks> blocks = generator.blocksStream().collect(Collectors.toList());
            assertThat(blocks).hasSize(7 * 6 * 5 * 7 * 6);
        }
    }

    @Nested
    class Irregular {
        @Test
        void errorOverPopPermutation() {
            assertThrows(SyntaxException.class, () -> {
                try {
                    new BlocksGenerator(" *p8 ");
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
                    new BlocksGenerator(" *p0 ");
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
                    new BlocksGenerator(" *pf ");
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
                    new BlocksGenerator(" *7p4 ");
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
                    new BlocksGenerator(" *6 ");
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
                    new BlocksGenerator(" []p1 ");
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
                    new BlocksGenerator(" [SZT]kp2 ");
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
                    new BlocksGenerator(" [TSZ  p1 ");
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
                    new BlocksGenerator(" [[TSZ]  p1 ");
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
                    new BlocksGenerator(" [TS[Z]  p1 ");
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
                    new BlocksGenerator(" T[SZ]  p1 ");
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
                    new BlocksGenerator(" IJ LS [SZ] ! ");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }

        @Test
        void errorIllegalPositionOfBracketPermutation3() {
            assertThrows(SyntaxException.class, () -> {
                try {
                    new BlocksGenerator(" [TSZ  p1] ");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }

        @Test
        void errorIllegalPositionOfAsteriskPermutation1() {
            assertThrows(SyntaxException.class, () -> {
                try {
                    new BlocksGenerator(" * p1 ");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }

        @Test
        void errorIllegalPositionOfAsteriskPermutation2() {
            assertThrows(SyntaxException.class, () -> {
                try {
                    new BlocksGenerator(" * ! ");
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
                    new BlocksGenerator(" TSZ]  p1 ");
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
                    new BlocksGenerator(" [TSZ]  p1 ]");
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
                    new BlocksGenerator(" [TSZ]]  p1 ");
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
                    new BlocksGenerator(" [T SZ LJ I]pk ");
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
                    new BlocksGenerator(" [TIJ]2 ");
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
                    new BlocksGenerator(" [  ] ");
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
                    new BlocksGenerator(" [X] ");
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
                    new BlocksGenerator(" X ");
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
                    new BlocksGenerator(Arrays.asList(" T ,S ", "", "I"));
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
                    new BlocksGenerator(" [SZ]p! ");
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
                    new BlocksGenerator(" [SZ]!p ");
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
                    new BlocksGenerator(" [SZ]x! ");
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
                    new BlocksGenerator(" *p! ");
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
                    new BlocksGenerator(" *!p ");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }

        @Test
        void errorDuplicateBlock1() {
            assertThrows(SyntaxException.class, () -> {
                try {
                    new BlocksGenerator("[tt]");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }

        @Test
        void errorDuplicateBlock2() {
            assertThrows(SyntaxException.class, () -> {
                try {
                    new BlocksGenerator(" [SS]p2 ");
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
                    new BlocksGenerator(" '*p4 ");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }

        @Test
        void errorInvalidSeparatorInSelector() {
            assertThrows(SyntaxException.class, () -> {
                try {
                    new BlocksGenerator("[S,Z,T,O]");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }
    }
}