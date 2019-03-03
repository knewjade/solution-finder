package searcher.spins;

import common.datastore.MinoOperationWithKey;
import core.field.Field;
import core.mino.Mino;
import core.neighbor.SimpleOriginalPiece;

import java.util.stream.Stream;

public class SpinCommons {
    public static boolean existsAllOnGroundWithoutT(Field allMergedField, Stream<? extends MinoOperationWithKey> operations) {
        long filledLine = allMergedField.getFilledLine();

        return operations.allMatch(operation -> existsOnGround(allMergedField, filledLine, operation));
    }

    public static boolean existsAllOnGroundWithT(Field allMergedField, Stream<? extends MinoOperationWithKey> operations, SimpleOriginalPiece operationT) {
        Field fieldWithoutT = allMergedField.freeze();
        fieldWithoutT.reduce(operationT.getMinoField());

        long filledLine = fieldWithoutT.getFilledLine();

        return operations.allMatch(operation -> existsOnGround(fieldWithoutT, filledLine, operation));
    }

    public static boolean existsOnGround(Field allMergedField, long filledLine, MinoOperationWithKey operation) {
        long needDeletedKey = operation.getNeedDeletedKey();
        if ((filledLine & needDeletedKey) != needDeletedKey) {
            return false;
        }

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
