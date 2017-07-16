package common.buildup;

import common.datastore.OperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Block;
import core.mino.Mino;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class BuildUp {
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

    // block順番で組み立てられる手順が存在するかチェックする
    public static boolean existsValidByOrder(Field field, Stream<OperationWithKey> operations, List<Block> blocks, int height, Reachable reachable) {
        EnumMap<Block, LinkedList<OperationWithKey>> eachBlocks = operations.sequential().collect(() -> new EnumMap<Block, LinkedList<OperationWithKey>>(Block.class), (blockLinkedListEnumMap, operationWithKey) -> {
            Mino mino = operationWithKey.getMino();
            Block block = mino.getBlock();
            LinkedList<OperationWithKey> operationWithKeys = blockLinkedListEnumMap.computeIfAbsent(block, b -> new LinkedList<>());
            operationWithKeys.add(operationWithKey);
        }, EnumMap::putAll);

        return existsValidByOrder(field.freeze(height), eachBlocks, blocks, height, reachable, 0);
    }

    private static boolean existsValidByOrder(Field field, EnumMap<Block, LinkedList<OperationWithKey>> eachBlocks, List<Block> blocks, int height, Reachable reachable, int depth) {
        long deleteKey = field.clearLineReturnKey();
        Block block = blocks.get(depth);
        LinkedList<OperationWithKey> operationWithKeys = eachBlocks.get(block);

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
                if (blocks.size() == depth + 1)
                    return true;

                Field nextField = field.freeze(height);
                nextField.putMino(mino, x, y);
                nextField.insertBlackLineWithKey(deleteKey);

                boolean exists = existsValidByOrder(nextField, eachBlocks, blocks, height, reachable, depth + 1);
                if (exists)
                    return true;
            }

            operationWithKeys.add(index, key);
        }

        field.insertBlackLineWithKey(deleteKey);
        return false;
    }
}
