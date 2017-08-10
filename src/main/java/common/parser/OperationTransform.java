package common.parser;

import common.datastore.*;
import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;

import java.util.ArrayList;
import java.util.List;

public class OperationTransform {
    // List<OperationWithKey>に変換する。正しく組み立てられるかはチェックしない
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
            vanilla.put(mino, x, y);
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
            field.put(mino, x, y);
            field.insertBlackLineWithKey(deleteKey);
        }
        return keys;
    }

    // List<Operation>に変換する。正しく組み立てられるかはチェックしない
    // operationWithKeysは組み立てられる順番に並んでいること
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

            field.put(mino, x, y);
            field.insertBlackLineWithKey(deleteKey);
        }

        return new Operations(operations);
    }
}
