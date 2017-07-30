package searcher.pack;

import core.mino.Mino;
import searcher.pack.separable_mino.SeparableMino;

import java.util.Comparator;

public class SeparableMinoComparator implements Comparator<SeparableMino> {
    public static int compareMinoField(SeparableMino o1, SeparableMino o2) {
        Mino mino1 = o1.getMino();
        Mino mino2 = o2.getMino();

        int blockCompare = mino1.getBlock().compareTo(mino2.getBlock());
        if (blockCompare != 0)
            return blockCompare;

        int rotateCompare = mino1.getRotate().compareTo(mino2.getRotate());
        if (rotateCompare != 0)
            return rotateCompare;

        int xCompare = Integer.compare(o1.getX(), o2.getX());
        if (xCompare != 0)
            return xCompare;

        int yCompare = Integer.compare(o1.getLowerY(), o2.getLowerY());
        if (yCompare != 0)
            return yCompare;

        return Long.compare(o1.getDeleteKey(), o2.getDeleteKey());
    }

    @Override
    public int compare(SeparableMino o1, SeparableMino o2) {
        return compareMinoField(o1, o2);
    }
}
