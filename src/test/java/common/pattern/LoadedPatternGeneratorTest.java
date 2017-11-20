package common.pattern;

import common.SyntaxException;
import common.datastore.PieceCounter;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static core.mino.Piece.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadedPatternGeneratorTest {
    @Nested
    class Regular {
        private void assertSame(PatternGenerator actual, PatternGenerator expected) {
            assertThat(actual.getDepth()).isEqualTo(expected.getDepth());

            List<Pieces> blocks = expected.blocksStream().collect(Collectors.toList());
            assertThat(actual.blocksStream())
                    .hasSize(blocks.size())
                    .containsAll(blocks);

            List<PieceCounter> pieceCounters = expected.blockCountersStream().collect(Collectors.toList());
            assertThat(actual.blockCountersStream())
                    .hasSize(pieceCounters.size())
                    .containsAll(pieceCounters);
        }

        @Test
        void toList1() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("I");
            assertThat(generator.getDepth()).isEqualTo(1);

            assertThat(generator.blocksStream())
                    .hasSize(1)
                    .containsExactly(new LongPieces(Collections.singletonList(I)));

            assertThat(generator.blockCountersStream())
                    .hasSize(1)
                    .containsExactly(new PieceCounter(Collections.singletonList(I)));
        }

        @Test
        void toList1WithComment() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("I # comment");
            assertSame(generator, new LoadedPatternGenerator("I"));
        }

        @Test
        void toList1LowerCase() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("i");
            assertSame(generator, new LoadedPatternGenerator("I"));
        }

        @Test
        void toList2() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("I,J");
            assertThat(generator.getDepth()).isEqualTo(2);

            assertThat(generator.blocksStream())
                    .hasSize(1)
                    .containsExactly(new LongPieces(Arrays.asList(I, J)));

            assertThat(generator.blockCountersStream())
                    .hasSize(1)
                    .containsExactly(new PieceCounter(Arrays.asList(I, J)));
        }

        @Test
        void toList2WithSpace() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator(" I , J ");
            assertSame(generator, new LoadedPatternGenerator("I,J"));
        }

        @Test
        void toList2WithSpace2() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator(" I J ");
            assertSame(generator, new LoadedPatternGenerator("I,J"));
        }

        @Test
        void toList2WithoutSeparator() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator(" IJ ");
            assertSame(generator, new LoadedPatternGenerator("I,J"));
        }

        @Test
        void toListAsterisk() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("*");
            assertThat(generator.getDepth()).isEqualTo(1);

            assertThat(generator.blocksStream())
                    .hasSize(7)
                    .allMatch(element -> element.getPieces().size() == 1);

            assertThat(generator.blockCountersStream())
                    .hasSize(7)
                    .allMatch(element -> element.getBlocks().size() == 1);
        }

        @Test
        void toListAsterisk2() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("*,*");
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
            PatternGenerator generator = new LoadedPatternGenerator("**");
            assertSame(generator, new LoadedPatternGenerator("*,*"));
        }

        @Test
        void toListAsterisk2WithSpace() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator(" * * ");
            assertSame(generator, new LoadedPatternGenerator("*,*"));
        }

        @Test
        void toListSelector() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("[TSZ]");
            assertThat(generator.getDepth()).isEqualTo(1);

            assertThat(generator.blocksStream())
                    .hasSize(3)
                    .contains(new LongPieces(Collections.singletonList(T)))
                    .contains(new LongPieces(Collections.singletonList(S)))
                    .contains(new LongPieces(Collections.singletonList(Z)));

            assertThat(generator.blockCountersStream())
                    .hasSize(3)
                    .contains(new PieceCounter(Collections.singletonList(T)))
                    .contains(new PieceCounter(Collections.singletonList(S)))
                    .contains(new PieceCounter(Collections.singletonList(Z)));
        }

        @Test
        void toListSelectorWithSpace() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("[ T S Z ]");
            assertSame(generator, new LoadedPatternGenerator("[TSZ]"));
        }

        @Test
        void toListSelector2() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("[TSZ],[IOJL]");
            assertThat(generator.getDepth()).isEqualTo(2);

            assertThat(generator.blocksStream())
                    .hasSize(12)
                    .contains(new LongPieces(Arrays.asList(T, I)))
                    .contains(new LongPieces(Arrays.asList(T, O)))
                    .contains(new LongPieces(Arrays.asList(T, J)))
                    .contains(new LongPieces(Arrays.asList(T, L)))
                    .contains(new LongPieces(Arrays.asList(S, I)))
                    .contains(new LongPieces(Arrays.asList(S, O)))
                    .contains(new LongPieces(Arrays.asList(S, J)))
                    .contains(new LongPieces(Arrays.asList(S, L)))
                    .contains(new LongPieces(Arrays.asList(Z, I)))
                    .contains(new LongPieces(Arrays.asList(Z, O)))
                    .contains(new LongPieces(Arrays.asList(Z, J)))
                    .contains(new LongPieces(Arrays.asList(Z, L)));

            assertThat(generator.blockCountersStream())
                    .hasSize(12)
                    .contains(new PieceCounter(Arrays.asList(T, I)))
                    .contains(new PieceCounter(Arrays.asList(T, O)))
                    .contains(new PieceCounter(Arrays.asList(T, J)))
                    .contains(new PieceCounter(Arrays.asList(T, L)))
                    .contains(new PieceCounter(Arrays.asList(S, I)))
                    .contains(new PieceCounter(Arrays.asList(S, O)))
                    .contains(new PieceCounter(Arrays.asList(S, J)))
                    .contains(new PieceCounter(Arrays.asList(S, L)))
                    .contains(new PieceCounter(Arrays.asList(Z, I)))
                    .contains(new PieceCounter(Arrays.asList(Z, O)))
                    .contains(new PieceCounter(Arrays.asList(Z, J)))
                    .contains(new PieceCounter(Arrays.asList(Z, L)));
        }

        @Test
        void toListSelector2WithLowercase() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator(" [TsZ] , [IOjl]");
            assertSame(generator, new LoadedPatternGenerator("[TSZ],[IOJL]"));
        }

        @Test
        void toListSelector2WithSpace() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator(" [ T S Z ] , [ I O J L ]");
            assertSame(generator, new LoadedPatternGenerator("[TSZ],[IOJL]"));
        }

        @Test
        void toListSelectorWithPermutation() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("[TSZ]p2");
            assertThat(generator.getDepth()).isEqualTo(2);

            assertThat(generator.blocksStream())
                    .hasSize(6)
                    .contains(new LongPieces(Arrays.asList(S, Z)))
                    .contains(new LongPieces(Arrays.asList(S, T)))
                    .contains(new LongPieces(Arrays.asList(Z, S)))
                    .contains(new LongPieces(Arrays.asList(Z, T)))
                    .contains(new LongPieces(Arrays.asList(T, Z)))
                    .contains(new LongPieces(Arrays.asList(T, S)));

            assertThat(generator.blockCountersStream())
                    .hasSize(3)
                    .contains(new PieceCounter(Arrays.asList(T, S)))
                    .contains(new PieceCounter(Arrays.asList(T, Z)))
                    .contains(new PieceCounter(Arrays.asList(S, Z)));
        }

        @Test
        void toListAsteriskWithPermutation() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator("*p4");
            assertSame(generator, new LoadedPatternGenerator("[TIOJLSZ]p4"));
        }

        @Test
        void toMultiList1() throws SyntaxException {
            List<String> patterns = Arrays.asList("I#comment", "T", "# comment");
            PatternGenerator generator = new LoadedPatternGenerator(patterns);
            assertThat(generator.getDepth()).isEqualTo(1);

            assertThat(generator.blocksStream()).hasSize(2);
            assertThat(generator.blockCountersStream()).hasSize(2);
        }

        @Test
        void toMultiList2() throws SyntaxException {
            List<String> patterns = Arrays.asList("I, *p4", "*p5", "[TISZL]p5");
            PatternGenerator generator = new LoadedPatternGenerator(patterns);
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
            PatternGenerator generator = new LoadedPatternGenerator(patterns);
            assertThat(generator.getDepth()).isEqualTo(5);

            assertThat(generator.blocksStream()).hasSize(210 + 210 + 210 + 840);
            assertThat(generator.blockCountersStream()).hasSize(35 + 35 + 35 + 140);

        }

        @Test
        void toMultiList4() throws SyntaxException {
            List<String> patterns = Arrays.asList("T,T", "Z,* # comment");
            PatternGenerator generator = new LoadedPatternGenerator(patterns);
            assertThat(generator.getDepth()).isEqualTo(2);

            assertThat(generator.blocksStream()).hasSize(8);
            assertThat(generator.blockCountersStream()).hasSize(8);
        }

        @Test
        void factorial1() throws SyntaxException {
            List<String> patterns = Collections.singletonList("[SZO]!");
            PatternGenerator generator = new LoadedPatternGenerator(patterns);
            assertSame(generator, new LoadedPatternGenerator("[SZO]p3"));
        }

        @Test
        void factorial2() throws SyntaxException {
            List<String> patterns = Collections.singletonList("*!");
            PatternGenerator generator = new LoadedPatternGenerator(patterns);
            assertSame(generator, new LoadedPatternGenerator("[TIOSZLJ]p7"));
        }

        @Test
        void complexWithoutComma() throws SyntaxException {
            List<String> patterns = Collections.singletonList("TI[SZ]p1**p3[S]!O[SZ]");
            PatternGenerator generator = new LoadedPatternGenerator(patterns);
            assertSame(generator, new LoadedPatternGenerator("T,I,[SZ]p1,*,*p3,[S]!,O,[SZ]"));
        }

        @Test
        void singleQuote() throws SyntaxException {
            PatternGenerator generator = new LoadedPatternGenerator(" ' *p3, *p2 ' ");
            assertThat(generator.getDepth()).isEqualTo(5);

            List<Pieces> blocks = generator.blocksStream().collect(Collectors.toList());
            assertThat(blocks).hasSize(7 * 6 * 5 * 7 * 6);
        }
    }

    @Nested
    class Irregular {
        @Test
        void errorOverPopPermutation() {
            assertThrows(SyntaxException.class, () -> {
                try {
                    new LoadedPatternGenerator(" *p8 ");
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
                    new LoadedPatternGenerator(" *p0 ");
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
                    new LoadedPatternGenerator(" *pf ");
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
                    new LoadedPatternGenerator(" *7p4 ");
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
                    new LoadedPatternGenerator(" *6 ");
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
                    new LoadedPatternGenerator(" []p1 ");
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
                    new LoadedPatternGenerator(" [SZT]kp2 ");
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
                    new LoadedPatternGenerator(" [TSZ  p1 ");
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
                    new LoadedPatternGenerator(" [[TSZ]  p1 ");
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
                    new LoadedPatternGenerator(" [TS[Z]  p1 ");
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
                    new LoadedPatternGenerator(" T[SZ]  p1 ");
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
                    new LoadedPatternGenerator(" IJ LS [SZ] ! ");
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
                    new LoadedPatternGenerator(" [TSZ  p1] ");
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
                    new LoadedPatternGenerator(" * p1 ");
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
                    new LoadedPatternGenerator(" * ! ");
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
                    new LoadedPatternGenerator(" TSZ]  p1 ");
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
                    new LoadedPatternGenerator(" [TSZ]  p1 ]");
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
                    new LoadedPatternGenerator(" [TSZ]]  p1 ");
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
                    new LoadedPatternGenerator(" [T SZ LJ I]pk ");
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
                    new LoadedPatternGenerator(" [TIJ]2 ");
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
                    new LoadedPatternGenerator(" [  ] ");
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
                    new LoadedPatternGenerator(" [X] ");
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
                    new LoadedPatternGenerator(" X ");
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
                    new LoadedPatternGenerator(Arrays.asList(" T ,S ", "", "I"));
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
                    new LoadedPatternGenerator(" [SZ]p! ");
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
                    new LoadedPatternGenerator(" [SZ]!p ");
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
                    new LoadedPatternGenerator(" [SZ]x! ");
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
                    new LoadedPatternGenerator(" *p! ");
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
                    new LoadedPatternGenerator(" *!p ");
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
                    new LoadedPatternGenerator("[tt]");
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
                    new LoadedPatternGenerator(" [SS]p2 ");
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
                    new LoadedPatternGenerator(" '*p4 ");
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
                    new LoadedPatternGenerator("[S,Z,T,O]");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        }
    }
}