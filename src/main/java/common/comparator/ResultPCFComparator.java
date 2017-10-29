package common.comparator;

import common.datastore.Result;
import common.datastore.action.Action;
import core.mino.Piece;

import java.util.Comparator;

public class ResultPCFComparator implements Comparator<Result> {
    public static int compareResult(Result o1, Result o2) {
        Piece lastHold1 = o1.getLastHold();
        Piece lastHold2 = o2.getLastHold();
        if (lastHold1 != null && lastHold2 != null) {
            int lastHold = lastHold1.compareTo(lastHold2);
            if (lastHold != 0)
                return lastHold;
        } else if (lastHold1 == null && lastHold2 != null) {
            return -1;
        } else if (lastHold1 != null && lastHold2 == null) {
            return 1;
        }

        int lastBlock = o1.getLastPiece().compareTo(o2.getLastPiece());
        if (lastBlock != 0)
            return lastBlock;

        Action action1 = o1.getLastAction();
        Action action2 = o2.getLastAction();
        int rotate = action1.getRotate().compareTo(action2.getRotate());
        if (rotate != 0)
            return rotate;

        int x = Long.compare(action1.getX(), action2.getX());
        if (x != 0)
            return x;

        return Long.compare(action1.getY(), action2.getY());
    }

    @Override
    public int compare(Result o1, Result o2) {
        return compareResult(o1, o2);
    }
}
