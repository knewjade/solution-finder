package common.comparator;

import common.datastore.pieces.Pieces;
import core.mino.Block;

import java.util.Comparator;
import java.util.List;

public class PiecesComparator implements Comparator<Pieces> {
    public static int comparePieces(Pieces o1, Pieces o2) {
        List<Block> blocks1 = o1.getBlocks();
        List<Block> blocks2 = o2.getBlocks();

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
