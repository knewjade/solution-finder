package common.cover;

import common.SpinChecker;
import common.cover.reachable.ReachableForCover;
import common.datastore.MinoOperation;
import common.datastore.MinoOperationWithKey;
import common.datastore.SimpleMinoOperation;
import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.MinoRotationDetail;
import searcher.spins.spin.Spin;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class B2BContinuousCover implements Cover {
    interface B2BCondition {
        boolean checks(Field field, int height, MinoOperation key);
    }

    private final B2BCondition b2BCondition;

    public B2BContinuousCover(MinoRotation minoRotation, boolean use180Rotation) {
        if (use180Rotation && minoRotation.noSupports180()) {
            throw new IllegalArgumentException("kicks do not support 180");
        }

        int maxY = 24;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotationDetail minoRotationDetail = new MinoRotationDetail(minoFactory, minoRotation);
        ILockedReachable lockedReachable = ReachableFacade.createLocked(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
        SpinChecker spinChecker = new SpinChecker(minoFactory, minoRotationDetail, lockedReachable, use180Rotation);

        this.b2BCondition = (Field field, int height, MinoOperation key) -> {
            Field freeze = field.freeze(height);
            freeze.put(key.getMino(), key.getX(), key.getY());
            int currentDeletedLines = freeze.clearLine();

            if (0 < currentDeletedLines) {
                // ラインが消去された
                switch (key.getPiece()) {
                    case T: {
                        Optional<Spin> spinOptional = spinChecker.check(field, key, height, currentDeletedLines);
                        if (!spinOptional.isPresent()) {
                            // Tスピンできない
                            return false;
                        }
                        break;
                    }
                    case I: {
                        if (currentDeletedLines != 4) {
                            // テトリスできない
                            return false;
                        }
                        break;
                    }
                    default: {
                        // T,I以外でライン消去された
                        return false;
                    }
                }
            }

            return true;
        };
    }

    @Override
    public boolean canBuild(Field field, Stream<? extends MinoOperationWithKey> operations, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth) {
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

    private boolean existsValidByOrder(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int depth, int maxDepth) {
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

                if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height - mino.getMinY(), maxDepth - depth)) {
                    if (!b2BCondition.checks(field, height, new SimpleMinoOperation(mino, x, y))) {
                        operationWithKeys.add(index, key);
                        continue;
                    }

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

    @Override
    public boolean canBuildWithHold(Field field, Stream<MinoOperationWithKey> operations, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth) {
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

    private boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth, int depth, Piece hold) {
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

    private boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth, int depth, Piece usePiece, long deleteKey, Piece nextHoldPiece) {
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

            if (field.isOnGround(mino, x, y) && field.canPut(mino, x, y) && reachable.checks(field, mino, x, y, height - mino.getMinY(), maxDepth - depth + 1)) {
                if (!b2BCondition.checks(field, height, new SimpleMinoOperation(mino, x, y))) {
                    operationWithKeys.add(index, key);
                    continue;
                }

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
