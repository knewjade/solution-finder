package core.mino;

import core.srs.Rotate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MinoTest {
    private void assertMinMax(Mino mino, int xMin, int xMax, int yMin, int yMax) {
        assertThat(mino.getMinX(), is(xMin));
        assertThat(mino.getMaxX(), is(xMax));
        assertThat(mino.getMinY(), is(yMin));
        assertThat(mino.getMaxY(), is(yMax));
    }

    @Test
    public void testTMinoMinMax() throws Exception {
        assertMinMax(new Mino(Block.T, Rotate.Spawn), -1, 1, 0, 1);
        assertMinMax(new Mino(Block.T, Rotate.Right), 0, 1, -1, 1);
        assertMinMax(new Mino(Block.T, Rotate.Reverse), -1, 1, -1, 0);
        assertMinMax(new Mino(Block.T, Rotate.Left), -1, 0, -1, 1);
    }

    @Test
    public void testIMinoMinMax() throws Exception {
        assertMinMax(new Mino(Block.I, Rotate.Spawn), -1, 2, 0, 0);
        assertMinMax(new Mino(Block.I, Rotate.Right), 0, 0, -2, 1);
        assertMinMax(new Mino(Block.I, Rotate.Reverse), -2, 1, 0, 0);
        assertMinMax(new Mino(Block.I, Rotate.Left), 0, 0, -1, 2);
    }

    @Test
    public void testTMinoMask() throws Exception {
        assertThat(new Mino(Block.T, Rotate.Spawn).getMask(1, 0), is(0x807L));
        assertThat(new Mino(Block.T, Rotate.Right).getMask(0, 1), is(0x100c01L));
        assertThat(new Mino(Block.T, Rotate.Reverse).getMask(1, 1), is(0x1c02L));
        assertThat(new Mino(Block.T, Rotate.Left).getMask(1, 1), is(0x200c02L));
    }
}