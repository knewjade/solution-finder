package misc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.mino.Block.*;
import static org.hamcrest.CoreMatchers.*;
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
        PiecesGenerator generator = new PiecesGenerator("I");
        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(1));
        assertThat(pieces.get(0).getBlocks(), is(Collections.singletonList(I)));
    }

    @Test
    public void toList1LowerCase() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("i");
        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(1));
        assertThat(pieces.get(0).getBlocks(), is(Collections.singletonList(I)));
    }

    @Test
    public void toList2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator("I,J");
        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(1));
        assertThat(pieces.get(0).getBlocks(), is(Arrays.asList(I, J)));
    }

    @Test
    public void toList2WithSpace() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" I , J ");
        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(1));
        assertThat(pieces.get(0).getBlocks(), is(Arrays.asList(I, J)));
    }

    @Test
    public void toListAsterisk() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" * ");
        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(7));
        for (SafePieces piece : pieces)
            assertThat(piece.getBlocks().size(), is(1));
    }

    @Test
    public void toListAsterisk2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" *, * ");
        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(49));
        for (SafePieces piece : pieces)
            assertThat(piece.getBlocks().size(), is(2));
    }

    @Test
    public void toListSelector() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" [TSZ] ");
        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(3));
        assertThat(pieces.get(0).getBlocks(), is(Collections.singletonList(T)));
        assertThat(pieces.get(1).getBlocks(), is(Collections.singletonList(S)));
        assertThat(pieces.get(2).getBlocks(), is(Collections.singletonList(Z)));
    }

    @Test
    public void toListSelector2() throws Exception {
        PiecesGenerator generator = new PiecesGenerator(" [TsZ] , [IOjl]");
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
        PiecesGenerator generator = new PiecesGenerator(" *p2 ");
        List<SafePieces> pieces = toList(generator);
        assertThat(pieces.size(), is(42));
    }
}