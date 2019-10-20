package common.buildup;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.action.Action;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.Piece;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

/**
 * マルチスレッド非対応
 */
public class BuildUpStream {
    private static final Comparator<MinoOperationWithKey> KEY_COMPARATOR = Comparator
            .comparingInt((ToIntFunction<MinoOperationWithKey>) Action::getY)
            .thenComparingLong(OperationWithKey::getNeedDeletedKey);

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

    // block順番で組み立てられる手順が存在するかチェックする
    // operationsで使用するミノとblocksが一致していること
    // 4ライン消し（テトリス）限定
    public Optional<List<MinoOperationWithKey>> existsValidByOrderForTetris(Field field, Stream<? extends MinoOperationWithKey> operations, List<Piece> pieces) {
        return existsValidByOrderForTetris(field, operations, pieces, pieces.size());
    }

    public Optional<List<MinoOperationWithKey>> existsValidByOrderForTetris(Field field, Stream<? extends MinoOperationWithKey> operations, List<Piece> pieces, int maxDepth) {
        EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks = operations.sequential().collect(() -> new EnumMap<>(Piece.class), (blockLinkedListEnumMap, operationWithKey) -> {
            Piece piece = operationWithKey.getPiece();
            LinkedList<MinoOperationWithKey> operationWithKeys = blockLinkedListEnumMap.computeIfAbsent(piece, b -> new LinkedList<>());
            operationWithKeys.add(operationWithKey);
        }, EnumMap::putAll);

        LinkedList<MinoOperationWithKey> results = new LinkedList<>();

        return existsValidByOrderForTetris(field.freeze(height), eachBlocks, pieces, 0, maxDepth, results);
    }

    private Optional<List<MinoOperationWithKey>> existsValidByOrderForTetris(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int depth, int maxDepth, LinkedList<MinoOperationWithKey> results) {
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
                
                // テトリス以外のとき
                if (0 < deletedLines && deletedLines < 4) {
                    operationWithKeys.add(index, key);
                    continue;
                }

                Mino mino = key.getMino();
                int x = key.getX();
                int y = originalY - deletedLines;

                if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height - mino.getMinY())) {
                    results.addLast(key);

                    if (maxDepth == depth + 1)
                        return Optional.of(results);

                    Field nextField = field.freeze(height);
                    nextField.put(mino, x, y);
                    nextField.insertBlackLineWithKey(deleteKey);

                    Optional<List<MinoOperationWithKey>> exists = existsValidByOrderForTetris(nextField, eachBlocks, pieces, depth + 1, maxDepth, results);
                    if (exists.isPresent())
                        return Optional.of(results);

                    results.removeLast();
                }

                operationWithKeys.add(index, key);
            }
        }

        return Optional.empty();
    }
}
