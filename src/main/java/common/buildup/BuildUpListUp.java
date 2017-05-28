package common.buildup;

import common.datastore.OperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * マルチスレッド非対応
 */
// TODO: unittest
public class BuildUpListUp {
    private final Reachable reachable;
    private final int height;
    private LinkedList<OperationWithKey> currentOperations = new LinkedList<>();
    private Stream.Builder<List<OperationWithKey>> solutions = Stream.builder();

    public BuildUpListUp(Reachable reachable, int height) {
        this.reachable = reachable;
        this.height = height;
    }

    // 組み立てられる手順が存在するか確認
    public Stream<List<OperationWithKey>> existsValidBuildPattern(Field fieldOrigin, List<OperationWithKey> operationWithKeys) {
        LinkedList<OperationWithKey> keys = new LinkedList<>(operationWithKeys);
        return existsValidBuildPatternDirectly(fieldOrigin, keys);
    }

    public Stream<List<OperationWithKey>> existsValidBuildPatternDirectly(Field fieldOrigin, LinkedList<OperationWithKey> operationWithKeys) {
        operationWithKeys.sort((o1, o2) -> {
            int compare = Integer.compare(o1.getY(), o2.getY());
            if (compare != 0)
                return compare;
            return Long.compare(o1.getNeedDeletedKey(), o2.getNeedDeletedKey());
        });
        this.currentOperations = new LinkedList<>();
        this.solutions = Stream.builder();
        existsValidBuildPatternRecursive(fieldOrigin.freeze(height), operationWithKeys);
        return solutions.build();
    }

    private void existsValidBuildPatternRecursive(Field field, LinkedList<OperationWithKey> operationWithKeys) {
        long deleteKey = field.clearLineReturnKey();

        for (int index = 0; index < operationWithKeys.size(); index++) {
            OperationWithKey key = operationWithKeys.remove(index);
            this.currentOperations.addLast(key);

            long needDeletedKey = key.getNeedDeletedKey();
            if ((deleteKey & needDeletedKey) != needDeletedKey) {
                // 必要な列が消えていない
                this.currentOperations.removeLast();
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
                if (operationWithKeys.isEmpty()) {
                    // 解をみつけたとき
                    solutions.accept(new ArrayList<>(currentOperations));
                } else {
                    Field nextField = field.freeze(height);
                    nextField.putMino(mino, x, y);
                    nextField.insertBlackLineWithKey(deleteKey);

                    existsValidBuildPatternRecursive(nextField, operationWithKeys);
                }
            }

            this.currentOperations.removeLast();
            operationWithKeys.add(index, key);
        }

        field.insertBlackLineWithKey(deleteKey);
    }
}
