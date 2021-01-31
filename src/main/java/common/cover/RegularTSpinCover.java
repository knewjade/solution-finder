package common.cover;

import common.SpinChecker;
import common.cover.reachable.ReachableForCover;
import common.datastore.MinoOperationWithKey;
import common.datastore.SimpleMinoOperation;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.MinoRotationDetail;
import searcher.spins.spin.Spin;
import searcher.spins.spin.TSpins;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RegularTSpinCover implements Cover {
    private final SpinChecker spinChecker;
    private final int requiredTSpinLine;

    public RegularTSpinCover(int requiredTSpinLine, boolean use180Rotation) {
        int maxY = 24;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = MinoRotation.create();
        MinoRotationDetail minoRotationDetail = new MinoRotationDetail(minoFactory, minoRotation);
        LockedReachable lockedReachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        this.spinChecker = new SpinChecker(minoFactory, minoRotationDetail, lockedReachable, use180Rotation);
        this.requiredTSpinLine = requiredTSpinLine;
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

        return existsValidByOrder(field.freeze(height), eachBlocks, pieces, height, reachable, 0, maxDepth, false);
    }

    private boolean existsValidByOrder(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int depth, int maxDepth, boolean satisfied) {
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
                    boolean newSatisfied = satisfied;

                    {
                        Field freeze = field.freeze(height);
                        freeze.put(mino, x, y);
                        int currentDeletedLines = freeze.clearLine();

                        if (0 < currentDeletedLines && key.getPiece() == Piece.T) {
                            // Tでラインが消去された
                            Optional<Spin> spinOptional = spinChecker.check(field, new SimpleMinoOperation(mino, x, y), height, currentDeletedLines);
                            if (spinOptional.isPresent()) {
                                Spin spin = spinOptional.get();
                                if (spin.getSpin() == TSpins.Regular && requiredTSpinLine <= spin.getClearedLine()) {
                                    newSatisfied = true;
                                }
                            }
                        }

                        if (key.getPiece() == Piece.T && !newSatisfied) {
                            // まだ条件を満たしていない
                            LinkedList<MinoOperationWithKey> ts = eachBlocks.get(Piece.T);
                            if (ts != null && ts.isEmpty()) {
                                // Tミノが残っていない
                                operationWithKeys.add(index, key);
                                continue;
                            }
                        }
                    }

                    if (maxDepth == depth + 1) {
                        if (newSatisfied) {
                            return true;
                        } else {
                            // まだ条件を満たしていない
                            operationWithKeys.add(index, key);
                            continue;
                        }
                    }

                    Field nextField = field.freeze(height);
                    nextField.put(mino, x, y);
                    nextField.insertBlackLineWithKey(deleteKey);

                    boolean exists = existsValidByOrder(nextField, eachBlocks, pieces, height, reachable, depth + 1, maxDepth, newSatisfied);
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

        return existsValidByOrderWithHold(field.freeze(height), eachBlocks, pieces, height, reachable, maxDepth, 1, pieces.get(0), false);
    }

    private boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth, int depth, Piece hold, boolean satisfied) {
        long deleteKey = field.clearLineReturnKey();

        Piece piece = depth < pieces.size() ? pieces.get(depth) : null;

        if (hold != null && existsValidByOrderWithHold(field, eachBlocks, pieces, height, reachable, maxDepth, depth, hold, deleteKey, piece, satisfied)) {
            return true;
        }

        if (piece != null && existsValidByOrderWithHold(field, eachBlocks, pieces, height, reachable, maxDepth, depth, piece, deleteKey, hold, satisfied)) {
            return true;
        }

        return false;
    }

    private boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth, int depth, Piece usePiece, long deleteKey, Piece nextHoldPiece, boolean satisfied) {
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
                boolean newSatisfied = satisfied;

                {
                    Field freeze = field.freeze(height);
                    freeze.put(mino, x, y);
                    int currentDeletedLines = freeze.clearLine();

                    if (0 < currentDeletedLines && key.getPiece() == Piece.T) {
                        // Tでラインが消去された
                        Optional<Spin> spinOptional = spinChecker.check(field, new SimpleMinoOperation(mino, x, y), height, currentDeletedLines);
                        if (spinOptional.isPresent()) {
                            Spin spin = spinOptional.get();
                            if (spin.getSpin() == TSpins.Regular && requiredTSpinLine <= spin.getClearedLine()) {
                                newSatisfied = true;
                            }
                        }
                    }

                    if (key.getPiece() == Piece.T && !newSatisfied) {
                        // まだ条件を満たしていない
                        LinkedList<MinoOperationWithKey> ts = eachBlocks.get(Piece.T);
                        if (ts != null && ts.isEmpty()) {
                            // Tミノが残っていない
                            operationWithKeys.add(index, key);
                            continue;
                        }
                    }
                }

                if (maxDepth == depth) {
                    if (newSatisfied) {
                        return true;
                    } else {
                        // まだ条件を満たしていない
                        operationWithKeys.add(index, key);
                        continue;
                    }
                }

                Field nextField = field.freeze(height);
                nextField.put(mino, x, y);
                nextField.insertBlackLineWithKey(deleteKey);

                boolean exists = existsValidByOrderWithHold(nextField, eachBlocks, pieces, height, reachable, maxDepth, depth + 1, nextHoldPiece, newSatisfied);
                if (exists)
                    return true;
            }

            operationWithKeys.add(index, key);
        }

        return false;
    }
}
