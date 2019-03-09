package searcher.spins;

import common.datastore.MinoOperationWithKey;
import core.field.Field;
import core.mino.Mino;

import java.util.stream.Stream;

public class SpinCommons {
    public static boolean existsOnGround(Field allMergedField, long allMergedFillLine, MinoOperationWithKey operation) {
        // operationで使われているラインは揃わない
        long fillLine = allMergedFillLine & ~operation.getUsingKey();

        // operationを置くのに消えている必要があるライン
        long needDeletedKey = operation.getNeedDeletedKey();
        if ((fillLine & needDeletedKey) != needDeletedKey) {
            return false;
        }

        // operationが地面の上なのか
        Field freeze = allMergedField.freeze();
        freeze.deleteLineWithKey(needDeletedKey);
        Mino mino = operation.getMino();
        int x = operation.getX();
        int y = operation.getY();
        freeze.remove(mino, x, y);
        return freeze.isOnGround(mino, x, y);
    }

    // Tスピンか判定
    public static boolean canTSpin(Field field, int x, int y, long needDeletedKey) {
        int slide = Long.bitCount(needDeletedKey);
        return 3L <= Stream.of(
                isBlock(field, x - 1, y - 1),
                isBlock(field, x - 1, y + 1 + slide),
                isBlock(field, x + 1, y - 1),
                isBlock(field, x + 1, y + 1 + slide)
        ).filter(Boolean::booleanValue).count();
    }

    private static boolean isBlock(Field field, int x, int y) {
        if (x < 0 || 10 <= x || y < 0) {
            return true;
        }
        return !field.isEmpty(x, y);
    }

    // Tスピンか判定
    public static boolean canTSpin(Field field, int x, int y) {
        return 3L <= Stream.of(
                isBlock(field, x - 1, y - 1),
                isBlock(field, x - 1, y + 1),
                isBlock(field, x + 1, y - 1),
                isBlock(field, x + 1, y + 1)
        ).filter(Boolean::booleanValue).count();
    }
}
