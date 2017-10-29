package _implements.parity_based_pack.step2;

import core.mino.Mino;

import java.util.List;

public class FullLimitedMinos implements Comparable<FullLimitedMinos> {
    private final List<FullLimitedMino> fullLimitedMinos;

    public FullLimitedMinos(List<FullLimitedMino> fullLimitedMinos) {
        this.fullLimitedMinos = fullLimitedMinos;
    }

    public int getDepth() {
        return fullLimitedMinos.size();
    }

    public FullLimitedMino get(int depth) {
        assert depth < getDepth();
        return fullLimitedMinos.get(depth);
    }

    @Override
    public int compareTo(FullLimitedMinos o) {
        int depth = getDepth();
        int oDepth = o.getDepth();
        int compare = Integer.compare(depth, oDepth);
        if (compare != 0)
            return compare;

        for (int index = 0; index < depth; index++) {
            FullLimitedMino o1 = fullLimitedMinos.get(index);
            FullLimitedMino o2 = o.fullLimitedMinos.get(index);
            int compareMino = compareToMino(o1, o2);
            if (compareMino != 0)
                return compareMino;
        }

        return 0;
    }

    private int compareToMino(FullLimitedMino o1, FullLimitedMino o2) {
        Mino mino = o1.getMino();
        Mino oMino = o2.getMino();

        int block = mino.getPiece().compareTo(oMino.getPiece());
        if (block != 0)
            return block;

        int rotate = mino.getRotate().compareTo(oMino.getRotate());
        if (rotate != 0)
            return rotate;

        int position = o1.getPositionLimit().compareTo(o2.getPositionLimit());
        if (position != 0)
            return position;

        int deleteKey = Long.compare(o1.getDeleteKey(), o2.getDeleteKey());
        if (deleteKey != 0)
            return deleteKey;

        return Integer.compare(o1.getLowerY(), o2.getLowerY());
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "FullLimitedMinos{" +
                "fullLimitedMinos=" + fullLimitedMinos +
                '}';
    }
}
