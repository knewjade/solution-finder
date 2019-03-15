package common.buildup;

import common.datastore.MinoOperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * マルチスレッド非対応
 */
public class BuildUpStream {
    private static final Comparator<MinoOperationWithKey> KEY_COMPARATOR = (o1, o2) -> {
        int compare = Integer.compare(o1.getY(), o2.getY());
        if (compare != 0)
            return compare;
        return Long.compare(o1.getNeedDeletedKey(), o2.getNeedDeletedKey());
    };

    private final Reachable reachable;
    private final int height;
    private LinkedList<MinoOperationWithKey> currentOperations = new LinkedList<>();
    private Stream.Builder<List<MinoOperationWithKey>> solutions = Stream.builder();

    public BuildUpStream(Reachable reachable, int height) {
        this.reachable = reachable;
        this.height = height;
    }

    // 組み立てられる手順が存在するか確認
    public Stream<List<MinoOperationWithKey>> existsValidBuildPattern(Field fieldOrigin, List<? extends MinoOperationWithKey> operationWithKeys) {
        LinkedList<MinoOperationWithKey> keys = new LinkedList<>(operationWithKeys);
        return existsValidBuildPatternDirectly(fieldOrigin, keys);
    }

    public Stream<List<MinoOperationWithKey>> existsValidBuildPatternDirectly(Field fieldOrigin, LinkedList<MinoOperationWithKey> operationWithKeys) {
        operationWithKeys.sort(KEY_COMPARATOR);
        this.currentOperations = new LinkedList<>();
        this.solutions = Stream.builder();
        existsValidBuildPatternRecursive(fieldOrigin.freeze(height), operationWithKeys);
        return solutions.build();
    }

    private void existsValidBuildPatternRecursive(Field field, LinkedList<MinoOperationWithKey> operationWithKeys) {
        long deleteKey = field.clearLineReturnKey();

        for (int index = 0; index < operationWithKeys.size(); index++) {
            MinoOperationWithKey key = operationWithKeys.remove(index);
            this.currentOperations.addLast(key);

            // 必要な列が消えているかチェック
            long needDeletedKey = key.getNeedDeletedKey();
            if ((deleteKey & needDeletedKey) == needDeletedKey) {

                // すでに下のラインが消えているときは、その分スライドさせる
                int originalY = key.getY();
                int deletedLines = Long.bitCount(KeyOperators.getMaskForKeyBelowY(originalY) & deleteKey);

                Mino mino = key.getMino();
                int x = key.getX();
                int y = originalY - deletedLines;

                if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height)) {
                    if (operationWithKeys.isEmpty()) {
                        // 解をみつけたとき
                        solutions.accept(new ArrayList<>(currentOperations));
                    } else {
                        Field nextField = field.freeze(height);
                        nextField.put(mino, x, y);
                        nextField.insertBlackLineWithKey(deleteKey);

                        existsValidBuildPatternRecursive(nextField, operationWithKeys);
                    }
                }
            }

            this.currentOperations.removeLast();
            operationWithKeys.add(index, key);
        }

        field.insertBlackLineWithKey(deleteKey);
    }
}
