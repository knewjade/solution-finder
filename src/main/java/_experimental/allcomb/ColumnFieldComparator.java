package _experimental.allcomb;

import java.util.Comparator;

public class ColumnFieldComparator implements Comparator<ColumnField> {
    public static int compareColumnField(ColumnField o1, ColumnField o2) {
        int boardCount1 = o1.getBoardCount();
        int boardCount2 = o2.getBoardCount();

        if (boardCount1 < boardCount2) {
            return compareDifferentSize(o1, boardCount1, o2, boardCount2);
        } else if (boardCount2 < boardCount1) {
            return -compareDifferentSize(o2, boardCount2, o1, boardCount1);
        }

        return compareSameSize(o1, o2, boardCount1);
    }

    private static int compareDifferentSize(ColumnField small, int smallBoardCount, ColumnField large, int largeBoardCount) {
        int index = 0;
        for (; index < smallBoardCount; index++) {
            int compare = Long.compare(small.getBoard(index), large.getBoard(index));
            if (compare != 0)
                return compare;
        }
        for (; index < largeBoardCount; index++) {
            long board = large.getBoard(index);
            if (board != 0L)
                return Long.compare(0L, board);
        }
        return 0;
    }

    private static int compareSameSize(ColumnField field1, ColumnField field2, int boardCount) {
        for (int index = 0; index < boardCount; index++) {
            int compare = Long.compare(field1.getBoard(index), field2.getBoard(index));
            if (compare != 0)
                return compare;
        }
        return 0;
    }

    @Override
    public int compare(ColumnField o1, ColumnField o2) {
        return compareColumnField(o1, o2);
    }
}
