package common.comparator;

import common.datastore.pieces.Blocks;
import core.mino.Block;

import java.util.Comparator;
import java.util.List;

public class PiecesNameComparator implements Comparator<Blocks> {
    public static int comparePieces(Blocks o1, Blocks o2) {
        List<Block> blocks1 = o1.getBlockList();
        List<Block> blocks2 = o2.getBlockList();

        int size1 = blocks1.size();
        int size2 = blocks2.size();
        int compareSize = Integer.compare(size1, size2);
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < size1; index++) {
            int compare = blocks1.get(index).getName().compareTo(blocks2.get(index).getName());
            if (compare != 0)
                return compare;
        }

        return 0;
    }

    @Override
    public int compare(Blocks o1, Blocks o2) {
        return comparePieces(o1, o2);
    }
}
