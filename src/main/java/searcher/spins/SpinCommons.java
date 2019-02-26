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
}
