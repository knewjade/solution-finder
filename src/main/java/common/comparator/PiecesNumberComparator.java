package common.comparator;

import common.datastore.blocks.Pieces;
import core.mino.Piece;

import java.util.Comparator;
import java.util.List;

public class PiecesNumberComparator implements Comparator<Pieces> {
    public static int comparePieces(Pieces o1, Pieces o2) {
        List<Piece> blocks1 = o1.getPieces();
        List<Piece> blocks2 = o2.getPieces();

        int size1 = blocks1.size();
        int size2 = blocks2.size();
        int compareSize = Integer.compare(size1, size2);
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < size1; index++) {
            int compare = blocks1.get(index).compareTo(blocks2.get(index));
            if (compare != 0)
                return compare;
        }

        return 0;
    }

    @Override
    public int compare(Pieces o1, Pieces o2) {
        return comparePieces(o1, o2);
    }
}
