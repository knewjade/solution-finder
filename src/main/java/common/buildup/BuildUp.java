package common.buildup;

import common.datastore.*;
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
import java.util.stream.Stream;

// TODO: rewrite 移動したり、クラス化したり
// TODO: unittest
public class BuildUp {
    public static List<OperationWithKey> parseToOperationWithKeys(Field fieldOrigin, Operations operations, MinoFactory minoFactory, int height) {
        ArrayList<OperationWithKey> keys = new ArrayList<>();
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
            long belowUpperY = KeyOperators.getMaskForKeyBelowY(upperY + 1);
            long keyLine = aboveLowerY & belowUpperY;
            long needDeletedKey = deleteKey & keyLine;
            long usingKey = keyLine & ~needDeletedKey;

            // 操作・消去されている必要がある行をセットで記録
            OperationWithKey operationWithKey = new SimpleOperationWithKey(mino, x, needDeletedKey, usingKey, lowerY);
            keys.add(operationWithKey);

            // 次のフィールドを作成
            field.putMino(mino, x, y);
            field.insertBlackLineWithKey(deleteKey);
        }
        return keys;
    }

    // List<Operation>に変換する。正しく組み立てられるかはチェックしない
    public static Operations parseToOperations(Field fieldOrigin, List<OperationWithKey> operationWithKeys, int height) {
        ArrayList<Operation> operations = new ArrayList<>();

        Field field = fieldOrigin.freeze(height);
        for (OperationWithKey operationWithKey : operationWithKeys) {
            long deleteKey = field.clearLineReturnKey();

            // すでに下のラインが消えているときは、その分スライドさせる
            int originalY = operationWithKey.getY();
            int deletedLines = Long.bitCount(KeyOperators.getMaskForKeyBelowY(originalY) & deleteKey);

            Mino mino = operationWithKey.getMino();
            int x = operationWithKey.getX();
            int y = originalY - deletedLines;

            operations.add(new SimpleOperation(mino.getBlock(), mino.getRotate(), x, y));

            field.putMino(mino, x, y);
            field.insertBlackLineWithKey(deleteKey);
        }

        return new Operations(operations);
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
        return existsValidBuildPatternDirectly(fieldOrigin, keys, height, reachable);
    }

    public static boolean existsValidBuildPatternDirectly(Field fieldOrigin, LinkedList<OperationWithKey> operationWithKeys, int height, Reachable reachable) {
        operationWithKeys.sort((o1, o2) -> {
            int compare = Integer.compare(o1.getY(), o2.getY());
            if (compare != 0)
                return compare;
            return Long.compare(o1.getNeedDeletedKey(), o2.getNeedDeletedKey());
        });
        return existsValidBuildPatternRecursive(fieldOrigin.freeze(height), operationWithKeys, height, reachable);
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

            if (field.isOnGround(mino, x, y) && field.canPutMino(mino, x, y) && reachable.checks(field, mino, x, y, height - mino.getMinY())) {
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

    // deleteKey・usingKeyに矛盾がないか確認
    public static boolean checksKey(List<OperationWithKey> operationWithKeys, long initDeleteKey, int maxClearLine) {
        LinkedList<OperationWithKey> targets = new LinkedList<>(operationWithKeys);
        return checksKeyDirectly(targets, initDeleteKey, maxClearLine);
    }

    public static boolean checksKeyDirectly(LinkedList<OperationWithKey> targets, long initDeleteKey, int maxClearLine) {
        long fillKey = KeyOperators.getMaskForKeyBelowY(maxClearLine);
        long currentValidKey = initDeleteKey;

        while (!targets.isEmpty()) {
            long nextValidKey = fillKey;
            LinkedList<OperationWithKey> next = new LinkedList<>();
            do {
                OperationWithKey operationWithKey = targets.pollFirst();
                long deletedKey = operationWithKey.getNeedDeletedKey();

                // まだ必要なライン消去がされていないか確認
                if (!includesChildKey(currentValidKey, deletedKey)) {
                    // 次にも探索する
                    next.add(operationWithKey);

                    // このブロックで使用されている列はまだ無効
                    long usingKey = operationWithKey.getUsingKey();
                    nextValidKey &= ~usingKey;
                }
            } while (!targets.isEmpty());

            // keyに変化がないときは探索が停滞しているため、ビルドできない
            if (currentValidKey == nextValidKey)
                return false;

            // 次の探索の準備
            assert includesChildKey(nextValidKey, currentValidKey);
            targets = next;
            currentValidKey = nextValidKey;
        }

        return true;
    }

    private static boolean includesChildKey(long parent, long child) {
        return (parent | child) == parent;
    }
}
