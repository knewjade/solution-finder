package misc;

import core.action.reachable.Reachable;
import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import searcher.common.Operation;
import searcher.common.Operations;

import java.util.ArrayList;
import java.util.List;

public class Build {
    public static List<OperationWithKey> createOperationWithKeys(Field fieldOrigin, Operations operations, MinoFactory minoFactory, int height) {
        ArrayList<OperationWithKey> objs = new ArrayList<>();
        Field field = fieldOrigin.freeze(height);
        for (Operation op : operations.getOperations()) {
            Mino mino = minoFactory.create(op.getBlock(), op.getRotate());
            int x = op.getX();
            int y = op.getY();

            long deleteKey = field.clearLineReturnKey();

            // 一番上と一番下のy座標を抽出
            Field vanilla = FieldFactory.createField(height);
            vanilla.putMino(mino, x, y);
            vanilla.insertWhiteLineWithKey(deleteKey);
            int lowerY = vanilla.getLowerY();
            int upperY = vanilla.getUpperYWith4Blocks();

            // 接着に必ず消去されている必要がある行を抽出
            long aboveLowerY = KeyOperators.getMaskForKeyAboveY(lowerY);
            long belowUpperY = KeyOperators.getMaskForKeyBelowY(upperY);
            long needDeletedKey = deleteKey & aboveLowerY & belowUpperY;

            // 操作・消去されている必要がある行をセットで記録
            OperationWithKey operationWithKey = new OperationWithKey(mino, x, needDeletedKey, lowerY);
            objs.add(operationWithKey);

            // 次のフィールドを作成
            field.putMino(mino, x, y);
            field.insertBlackLineWithKey(deleteKey);
        }
        return objs;
    }

    // 指定した手順で組み立てられるか確認
    public static boolean cansBuild(Field fieldOrigin, List<OperationWithKey> operationWithKeys, int height, Reachable reachable) {
        Field field = fieldOrigin.freeze(height);
        for (OperationWithKey operationWithKey : operationWithKeys) {
            long deleteKey = field.clearLineReturnKey();
            long needDeletedKey = operationWithKey.getNeedDeletedKey();
            if ((deleteKey & needDeletedKey) != needDeletedKey) {
                // 必要な列が消えていない
                return false;
            }

            // すでに下のラインが消えているときは、その分スライドさせる
            int originalY = operationWithKey.getY();
            int deletedLines = Long.bitCount(KeyOperators.getMaskForKeyBelowY(originalY) & deleteKey);

            Mino mino = operationWithKey.getMino();
            int x = operationWithKey.getX();
            int y = originalY - deletedLines;

            if (field.isOnGround(mino, x, y) && field.canPutMino(mino, x, y) && reachable.checks(field, mino, x, y, height)) {
                field.putMino(mino, x, y);
                field.insertBlackLineWithKey(deleteKey);
            } else {
                return false;
            }
        }
        return true;
    }
}
