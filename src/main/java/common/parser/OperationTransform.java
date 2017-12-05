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
    public static List<MinoOperationWithKey> parseToOperationWithKeys(Field fieldOrigin, Operations operations, MinoFactory minoFactory, int height) {
        ArrayList<MinoOperationWithKey> keys = new ArrayList<>();
        Field field = fieldOrigin.freeze(height);
        for (Operation op : operations.getOperations()) {
            Mino mino = minoFactory.create(op.getPiece(), op.getRotate());
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
            MinoOperationWithKey operationWithKey = new FullOperationWithKey(mino, x, needDeletedKey, usingKey, lowerY);
            keys.add(operationWithKey);

            // 次のフィールドを作成
            field.put(mino, x, y);
            field.insertBlackLineWithKey(deleteKey);
        }
        return keys;
    }

    // List<Operation>に変換する。正しく組み立てられるかはチェックしない
    // operationWithKeysは組み立てられる順番に並んでいること
    public static Operations parseToOperations(Field fieldOrigin, List<MinoOperationWithKey> operationWithKeys, int height) {
        ArrayList<Operation> operations = new ArrayList<>();

        Field field = fieldOrigin.freeze(height);
        for (MinoOperationWithKey operationWithKey : operationWithKeys) {
            long deleteKey = field.clearLineReturnKey();

            // すでに下のラインが消えているときは、その分スライドさせる
            int originalY = operationWithKey.getY();
            int deletedLines = Long.bitCount(KeyOperators.getMaskForKeyBelowY(originalY) & deleteKey);

            Mino mino = operationWithKey.getMino();
            int x = operationWithKey.getX();
            int y = originalY - deletedLines;

            operations.add(new SimpleOperation(mino.getPiece(), mino.getRotate(), x, y));

            field.put(mino, x, y);
            field.insertBlackLineWithKey(deleteKey);
        }

        return new Operations(operations);
    }

    public static <T extends OperationWithKey> BlockField parseToBlockField(List<T> operationWithKeys, MinoFactory minoFactory, int height) {
        BlockField blockField = new BlockField(height);
        operationWithKeys
                .forEach(key -> {
                    Field test = FieldFactory.createField(height);
                    Mino mino = minoFactory.create(key.getPiece(), key.getRotate());
                    test.put(mino, key.getX(), key.getY());
                    test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                    blockField.merge(test, mino.getPiece());
                });
        return blockField;
    }
}
