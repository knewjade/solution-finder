package common.buildup;

import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// TODO: rewrite 移動したり、クラス化したり
// TODO: unittest
public class BuildUp {
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

    // 組み立てられる手順が存在するか確認
    public static boolean existsValidBuildPattern(Field fieldOrigin, List<OperationWithKey> operationWithKeys, int height, Reachable reachable) {
        LinkedList<OperationWithKey> keys = new LinkedList<>(operationWithKeys);
        keys.sort((o1, o2) -> {
            int compare = Integer.compare(o1.getY(), o2.getY());
            if (compare != 0)
                return compare;
            return Long.compare(o1.getNeedDeletedKey(), o2.getNeedDeletedKey());
        });
        return existsValidBuildPatternRecursive(fieldOrigin.freeze(height), keys, height, reachable);
    }

    private static boolean existsValidBuildPatternRecursive(Field field, LinkedList<OperationWithKey> operationWithKeys, int height, Reachable reachable) {
        long deleteKey = field.clearLineReturnKey();

        for (int index = 0; index < operationWithKeys.size(); index++) {
            OperationWithKey key = operationWithKeys.remove(index);

            long needDeletedKey = key.getNeedDeletedKey();
            if ((deleteKey & needDeletedKey) != needDeletedKey) {
                // 必要な列が消えていない
                operationWithKeys.add(index, key);
                continue;
            }

            // すでに下のラインが消えているときは、その分スライドさせる
            int originalY = key.getY();
            int deletedLines = Long.bitCount(KeyOperators.getMaskForKeyBelowY(originalY) & deleteKey);

            Mino mino = key.getMino();
            int x = key.getX();
            int y = originalY - deletedLines;

            if (field.isOnGround(mino, x, y) && field.canPutMino(mino, x, y) && reachable.checks(field, mino, x, y, height)) {
                if (operationWithKeys.isEmpty())
                    return true;

                Field nextField = field.freeze(height);
                nextField.putMino(mino, x, y);
                nextField.insertBlackLineWithKey(deleteKey);

                boolean exists = existsValidBuildPatternRecursive(nextField, operationWithKeys, height, reachable);
                if (exists)
                    return true;
            }

            operationWithKeys.add(index, key);
        }

        field.insertBlackLineWithKey(deleteKey);
        return false;
    }
}
