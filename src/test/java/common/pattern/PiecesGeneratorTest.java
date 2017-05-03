package common.pattern;

import common.SyntaxException;
import common.datastore.SafePieces;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PiecesGeneratorTest {
    private List<SafePieces> toList(Iterable<SafePieces> iterable) {
        ArrayList<SafePieces> list = new ArrayList<>();
        for (SafePieces pieces : iterable)
            list.add(pieces);
        return list;
    }

    @Test
    public void toList1() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I # comment");
        assertThat(generator.getDepth(), is(1));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(1));
        assertThat(pieces.get(0).getBlocks(), is(Collections.singletonList(I)));
    }

    @Test
    public void toList1LowerCase() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("i");
        assertThat(generator.getDepth(), is(1));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(1));
        assertThat(pieces.get(0).getBlocks(), is(Collections.singletonList(I)));
    }

    @Test
    public void toList2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I,J");
        assertThat(generator.getDepth(), is(2));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(1));
        assertThat(pieces.get(0).getBlocks(), is(Arrays.asList(I, J)));
    }

    @Test
    public void toList2WithSpace() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" I , J ");
        assertThat(generator.getDepth(), is(2));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(1));
        assertThat(pieces.get(0).getBlocks(), is(Arrays.asList(I, J)));
    }

    @Test
    public void toListAsterisk() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" * ");
        assertThat(generator.getDepth(), is(1));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(7));
        for (SafePieces piece : pieces)
            assertThat(piece.getBlocks().size(), is(1));
    }

    @Test
    public void toListAsterisk2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" *, * ");
        assertThat(generator.getDepth(), is(2));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(49));
        for (SafePieces piece : pieces)
            assertThat(piece.getBlocks().size(), is(2));
    }

    @Test
    public void toListSelector() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" [TSZ] ");
        assertThat(generator.getDepth(), is(1));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(3));
        assertThat(pieces.get(0).getBlocks(), is(Collections.singletonList(T)));
        assertThat(pieces.get(1).getBlocks(), is(Collections.singletonList(S)));
        assertThat(pieces.get(2).getBlocks(), is(Collections.singletonList(Z)));
    }

    @Test
    public void toListSelector2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" [TsZ] , [IOjl]");
        assertThat(generator.getDepth(), is(2));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(12));
        assertThat(pieces.get(0).getBlocks(), is(Arrays.asList(T, I)));
        assertThat(pieces.get(1).getBlocks(), is(Arrays.asList(T, O)));
        assertThat(pieces.get(2).getBlocks(), is(Arrays.asList(T, J)));
        assertThat(pieces.get(3).getBlocks(), is(Arrays.asList(T, L)));
        assertThat(pieces.get(4).getBlocks(), is(Arrays.asList(S, I)));
        assertThat(pieces.get(5).getBlocks(), is(Arrays.asList(S, O)));
        assertThat(pieces.get(6).getBlocks(), is(Arrays.asList(S, J)));
        assertThat(pieces.get(7).getBlocks(), is(Arrays.asList(S, L)));
        assertThat(pieces.get(8).getBlocks(), is(Arrays.asList(Z, I)));
        assertThat(pieces.get(9).getBlocks(), is(Arrays.asList(Z, O)));
        assertThat(pieces.get(10).getBlocks(), is(Arrays.asList(Z, J)));
        assertThat(pieces.get(11).getBlocks(), is(Arrays.asList(Z, L)));
    }

    @Test
    public void toListSelectorWithPermutation() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" [TSZ]p2 ");
        assertThat(generator.getDepth(), is(2));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(6));
        assertThat(pieces.get(0).getBlocks(), is(Arrays.asList(S, Z)));
        assertThat(pieces.get(1).getBlocks(), is(Arrays.asList(Z, S)));
        assertThat(pieces.get(3).getBlocks(), is(Arrays.asList(Z, T)));
        assertThat(pieces.get(2).getBlocks(), is(Arrays.asList(T, Z)));
        assertThat(pieces.get(4).getBlocks(), is(Arrays.asList(T, S)));
        assertThat(pieces.get(5).getBlocks(), is(Arrays.asList(S, T)));
    }

    @Test
    public void toListAsteriskWithPermutation() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" *p4 ");
        assertThat(generator.getDepth(), is(4));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(840));
    }

    @Test
    public void toMultiList1() throws Exception {
        List<String> patterns = Arrays.asList("I#comment", "T", "# comment");
        PiecesGenerator generator = new PiecesGenerator(patterns);
        assertThat(generator.getDepth(), is(1));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(2));
    }

    @Test
    public void toMultiList2() throws Exception {
        List<String> patterns = Arrays.asList("I, *p4", "*p5", "[TISZL]p5");
        PiecesGenerator generator = new PiecesGenerator(patterns);
        assertThat(generator.getDepth(), is(5));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(840 + 2520 + 120));
    }

    @Test
    public void toMultiList3() throws Exception {
        List<String> patterns = Arrays.asList(
                "I, I, *p3",
                "I, S, *p3",
                "",
                "I, Z, *p3",
                "",
                "I, [TOLJ], *p3"
        );
        PiecesGenerator generator = new PiecesGenerator(patterns);
        assertThat(generator.getDepth(), is(5));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(210 + 210 + 210 + 840));
    }

    @Test
    public void toMultiList4() throws Exception {
        List<String> patterns = Arrays.asList("T,T", "Z,* # comment");
        PiecesGenerator generator = new PiecesGenerator(patterns);
        assertThat(generator.getDepth(), is(2));

        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(8));
    }

    @Test(expected = SyntaxException.class)
    public void errorOverPopPermutation() throws Exception {
        try {
            PiecesGenerator.verify(" *p8 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorLessPopPermutation() throws Exception {
        try {
            PiecesGenerator.verify(" *p0 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorIllegalNumberPermutation() throws Exception {
        try {
            PiecesGenerator.verify(" *pf ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorIllegalNumber2Permutation() throws Exception {
        try {
            PiecesGenerator.verify(" *7p4 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorNoP() throws Exception {
        try {
            PiecesGenerator.verify(" *6 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorNoBlocksPermutation() throws Exception {
        try {
            PiecesGenerator.verify(" []p1 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorAsteriskAndBlocksPermutation() throws Exception {
        try {
            PiecesGenerator.verify(" [SZT]*p2 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorUnknownAndBlocksPermutation() throws Exception {
        try {
            PiecesGenerator.verify(" [SZT]kp2 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorAsteriskAndBlocksPermutation2() throws Exception {
        try {
            PiecesGenerator.verify(" *[SZT]p2 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorUnknownAndBlocksPermutation2() throws Exception {
        try {
            PiecesGenerator.verify(" z[SZT]p2 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorNoCloseBracketPermutation() throws Exception {
        try {
            PiecesGenerator.verify(" [TSZ  p1 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorOverOpenBracketPermutation1() throws Exception {
        try {
            PiecesGenerator.verify(" [[TSZ]  p1 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorOverOpenBracketPermutation2() throws Exception {
        try {
            PiecesGenerator.verify(" [TS[Z]  p1 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorIllegalPositionOfBracketPermutation1() throws Exception {
        try {
            PiecesGenerator.verify(" T[SZ]  p1 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorIllegalPositionOfBracketPermutation2() throws Exception {
        try {
            PiecesGenerator.verify(" [TSZ  p1] ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorNoOpenBracketPermutation() throws Exception {
        try {
            PiecesGenerator.verify(" TSZ]  p1 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorOverCloseBracketPermutation1() throws Exception {
        try {
            PiecesGenerator.verify(" [TSZ]  p1 ]");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorOverCloseBracketPermutation2() throws Exception {
        try {
            PiecesGenerator.verify(" [TSZ]]  p1 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorIllegalNumberSelector() throws Exception {
        try {
            PiecesGenerator.verify(" [T SZ LJ I]pk ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorSelectorNoP() throws Exception {
        try {
            PiecesGenerator.verify(" [TIJ]2 ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorEmptySelector() throws Exception {
        try {
            PiecesGenerator.verify(" [  ] ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorIllegalBlockSelector() throws Exception {
        try {
            PiecesGenerator.verify(" [X] ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorIllegalBlock() throws Exception {
        try {
            PiecesGenerator.verify(" X ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorOverBlock() throws Exception {
        try {
            PiecesGenerator.verify(" T S ");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = SyntaxException.class)
    public void errorDifferentDepthList() throws Exception {
        try {
            PiecesGenerator.verify(Arrays.asList(" T ,S ", "", "I"));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}