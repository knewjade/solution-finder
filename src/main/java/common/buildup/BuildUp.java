package common.buildup;

import common.datastore.MinoOperation;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.action.Action;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.Piece;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildUp {
    // 指定した手順で組み立てられるか確認
    public static boolean cansBuild(Field fieldOrigin, List<MinoOperationWithKey> operationWithKeys, int height, Reachable reachable) {
        Field field = fieldOrigin.freeze(height);
        for (MinoOperationWithKey operationWithKey : operationWithKeys) {
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

            if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height)) {
                field.put(mino, x, y);
                field.insertBlackLineWithKey(deleteKey);
            } else {
                return false;
            }
        }

        return true;
    }

    // 組み立てられる手順が存在するか確認
    public static boolean existsValidBuildPattern(Field fieldOrigin, Stream<? extends MinoOperationWithKey> operationWithKeys, int height, Reachable reachable) {
        LinkedList<MinoOperationWithKey> keys = operationWithKeys.collect(Collectors.toCollection(LinkedList::new));
        return existsValidBuildPatternDirectly(fieldOrigin, keys, height, reachable);
    }

    public static boolean existsValidBuildPattern(Field fieldOrigin, List<? extends MinoOperationWithKey> operationWithKeys, int height, Reachable reachable) {
        LinkedList<MinoOperationWithKey> keys = new LinkedList<>(operationWithKeys);
        return existsValidBuildPatternDirectly(fieldOrigin, keys, height, reachable);
    }

    public static boolean existsValidBuildPatternDirectly(Field fieldOrigin, LinkedList<MinoOperationWithKey> operationWithKeys, int height, Reachable reachable) {
        operationWithKeys.sort(Comparator.comparingInt((MinoOperationWithKey o) -> o.getY()).thenComparingLong(OperationWithKey::getNeedDeletedKey));
        return existsValidBuildPatternRecursive(fieldOrigin.freeze(height), operationWithKeys, height, reachable);
    }

    private static boolean existsValidBuildPatternRecursive(Field field, LinkedList<MinoOperationWithKey> operationWithKeys, int height, Reachable reachable) {
        long deleteKey = field.clearLineReturnKey();

        for (int index = 0; index < operationWithKeys.size(); index++) {
            MinoOperationWithKey key = operationWithKeys.remove(index);

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

            if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height - mino.getMinY())) {
                if (operationWithKeys.isEmpty())
                    return true;

                Field nextField = field.freeze(height);
                nextField.put(mino, x, y);
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

    // 組み立てられる手順が存在するか確認
    // キーのチェックは行わない
    public static boolean existsValidBuildPatternWithoutKey(Field fieldOrigin, List<? extends MinoOperation> operations, int height, Reachable reachable) {
        LinkedList<MinoOperation> keys = new LinkedList<>(operations);
        return existsValidBuildPatternDirectlyWithoutKey(fieldOrigin, keys, height, reachable);
    }

    public static boolean existsValidBuildPatternDirectlyWithoutKey(Field fieldOrigin, LinkedList<MinoOperation> operations, int height, Reachable reachable) {
        operations.sort(Comparator.comparingInt(Action::getY));
        return existsValidBuildPatternRecursiveWithoutKey(fieldOrigin.freeze(height), operations, height, reachable);
    }

    private static boolean existsValidBuildPatternRecursiveWithoutKey(Field field, LinkedList<MinoOperation> operations, int height, Reachable reachable) {
        long deleteKey = field.clearLineReturnKey();

        for (int index = 0; index < operations.size(); index++) {
            MinoOperation operation = operations.remove(index);

            Mino mino = operation.getMino();
            int x = operation.getX();
            int y = operation.getY();

            if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height - mino.getMinY())) {
                if (operations.isEmpty())
                    return true;

                Field nextField = field.freeze(height);
                nextField.put(mino, x, y);
                nextField.insertBlackLineWithKey(deleteKey);

                boolean exists = existsValidBuildPatternRecursiveWithoutKey(nextField, operations, height, reachable);
                if (exists)
                    return true;
            }

            operations.add(index, operation);
        }

        field.insertBlackLineWithKey(deleteKey);
        return false;
    }

    // deleteKey・usingKeyに矛盾がないか確認
    public static <T extends OperationWithKey> boolean checksKey(List<T> operationWithKeys, long initDeleteKey, int maxClearLine) {
        LinkedList<T> targets = new LinkedList<>(operationWithKeys);
        return checksKeyDirectly(targets, initDeleteKey, maxClearLine);
    }

    public static <T extends OperationWithKey> boolean checksKeyDirectly(LinkedList<T> targets, long initDeleteKey, int maxClearLine) {
        long fillKey = KeyOperators.getMaskForKeyBelowY(maxClearLine);
        long currentValidKey = initDeleteKey;

        while (!targets.isEmpty()) {
            long nextValidKey = fillKey;
            LinkedList<T> next = new LinkedList<>();
            do {
                T operationWithKey = targets.pollFirst();
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
    // operationsで使用するミノとblocksが一致していること
    public static boolean existsValidByOrder(Field field, Stream<? extends MinoOperationWithKey> operations, List<Piece> pieces, int height, Reachable reachable) {
        return existsValidByOrder(field, operations, pieces, height, reachable, pieces.size());
    }

    public static boolean existsValidByOrder(Field field, Stream<? extends MinoOperationWithKey> operations, List<Piece> pieces, int height, Reachable reachable, int maxDepth) {
        if (pieces.size() < maxDepth) {
            return false;
        }

        EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks = operations.sequential().collect(() -> new EnumMap<>(Piece.class), (blockLinkedListEnumMap, operationWithKey) -> {
            Piece piece = operationWithKey.getPiece();
            LinkedList<MinoOperationWithKey> operationWithKeys = blockLinkedListEnumMap.computeIfAbsent(piece, b -> new LinkedList<>());
            operationWithKeys.add(operationWithKey);
        }, EnumMap::putAll);

        return existsValidByOrder(field.freeze(height), eachBlocks, pieces, height, reachable, 0, maxDepth);
    }

    private static boolean existsValidByOrder(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, Reachable reachable, int depth, int maxDepth) {
        long deleteKey = field.clearLineReturnKey();
        Piece piece = pieces.get(depth);
        LinkedList<MinoOperationWithKey> operationWithKeys = eachBlocks.get(piece);

        if (operationWithKeys != null) {
            for (int index = 0; index < operationWithKeys.size(); index++) {
                MinoOperationWithKey key = operationWithKeys.remove(index);

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

                if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height - mino.getMinY())) {
                    if (maxDepth == depth + 1)
                        return true;

                    Field nextField = field.freeze(height);
                    nextField.put(mino, x, y);
                    nextField.insertBlackLineWithKey(deleteKey);

                    boolean exists = existsValidByOrder(nextField, eachBlocks, pieces, height, reachable, depth + 1, maxDepth);
                    if (exists)
                        return true;
                }

                operationWithKeys.add(index, key);
            }
        }

        return false;
    }

    // block順番で組み立てられる手順が存在するかチェックする
    // operationsで使用するミノとblocksが一致していること
    public static boolean existsValidByOrderWithHold(Field field, Stream<MinoOperationWithKey> operations, List<Piece> pieces, int height, Reachable reachable, int maxDepth) {
        if (pieces.size() < maxDepth) {
            return false;
        }

        EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks = operations.sequential().collect(() -> new EnumMap<>(Piece.class), (blockLinkedListEnumMap, operationWithKey) -> {
            Piece piece = operationWithKey.getPiece();
            LinkedList<MinoOperationWithKey> operationWithKeys = blockLinkedListEnumMap.computeIfAbsent(piece, b -> new LinkedList<>());
            operationWithKeys.add(operationWithKey);
        }, EnumMap::putAll);

        return existsValidByOrderWithHold(field.freeze(height), eachBlocks, pieces, height, reachable, maxDepth, 1, pieces.get(0));
    }

    private static boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, Reachable reachable, int maxDepth, int depth, Piece hold) {
        long deleteKey = field.clearLineReturnKey();

        Piece piece = depth < pieces.size() ? pieces.get(depth) : null;

        if (hold != null && existsValidByOrderWithHold(field, eachBlocks, pieces, height, reachable, maxDepth, depth, hold, deleteKey, piece)) {
            return true;
        }

        if (piece != null && existsValidByOrderWithHold(field, eachBlocks, pieces, height, reachable, maxDepth, depth, piece, deleteKey, hold)) {
            return true;
        }

        return false;
    }

    private static boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, Reachable reachable, int maxDepth, int depth, Piece usePiece, long deleteKey, Piece nextHoldPiece) {
        LinkedList<MinoOperationWithKey> operationWithKeys = eachBlocks.get(usePiece);
        if (operationWithKeys == null) {
            return false;
        }

        for (int index = 0; index < operationWithKeys.size(); index++) {
            MinoOperationWithKey key = operationWithKeys.remove(index);

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

            if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height - mino.getMinY())) {
                if (depth == maxDepth)
                    return true;

                Field nextField = field.freeze(height);
                nextField.put(mino, x, y);
                nextField.insertBlackLineWithKey(deleteKey);

                boolean exists = existsValidByOrderWithHold(nextField, eachBlocks, pieces, height, reachable, maxDepth, depth + 1, nextHoldPiece);
                if (exists)
                    return true;
            }

            operationWithKeys.add(index, key);
        }

        return false;
    }
}
