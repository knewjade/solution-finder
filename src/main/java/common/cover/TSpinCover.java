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

public class TSpinCover implements Cover {
    interface TSpinCondition {
        boolean satisfy(Spin spin);
    }

    static class RegularTSpinCondition implements TSpinCondition {
        private final int requiredTSpinLine;

        RegularTSpinCondition(int requiredTSpinLine) {
            this.requiredTSpinLine = requiredTSpinLine;
        }

        @Override
        public boolean satisfy(Spin spin) {
            return spin.getSpin() == TSpins.Regular && requiredTSpinLine <= spin.getClearedLine();
        }
    }

    static class TSpinMiniCondition implements TSpinCondition {
        @Override
        public boolean satisfy(Spin spin) {
            return true;
        }
    }

    public static TSpinCover createRegularTSpinCover(int requiredTSpinLine, boolean use180Rotation) {
        return createRegularTSpinCover(requiredTSpinLine, 0, use180Rotation);
    }

    public static TSpinCover createRegularTSpinCover(int requiredTSpinLine, int b2bContinuousAfterStart, boolean use180Rotation) {
        TSpinCondition tSpinCondition = new RegularTSpinCondition(requiredTSpinLine);
        TSpinGuard tSpinGuard = new TSpinGuard(b2bContinuousAfterStart);
        return new TSpinCover(tSpinCondition, tSpinGuard, use180Rotation);
    }

    public static TSpinCover createTSpinMiniCover(boolean use180Rotation) {
        return createTSpinMiniCover(use180Rotation, 0);
    }

    public static TSpinCover createTSpinMiniCover(boolean use180Rotation, int b2bContinuousAfterStart) {
        TSpinCondition tSpinCondition = new TSpinMiniCondition();
        TSpinGuard tSpinGuard = new TSpinGuard(b2bContinuousAfterStart);
        return new TSpinCover(tSpinCondition, tSpinGuard, use180Rotation);
    }

    private final SpinChecker spinChecker;
    private final TSpinCondition tSpinCondition;
    private final TSpinGuard initGuard;

    private TSpinCover(TSpinCondition tSpinCondition, TSpinGuard initGuard, boolean use180Rotation) {
        int maxY = 24;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = MinoRotation.create();
        MinoRotationDetail minoRotationDetail = new MinoRotationDetail(minoFactory, minoRotation);
        LockedReachable lockedReachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        this.spinChecker = new SpinChecker(minoFactory, minoRotationDetail, lockedReachable, use180Rotation);
        this.initGuard = initGuard;
        this.tSpinCondition = tSpinCondition;
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

        return existsValidByOrder(field.freeze(height), eachBlocks, pieces, height, reachable, 0, maxDepth, initGuard);
    }

    private boolean existsValidByOrder(
            Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height,
            ReachableForCover reachable, int depth, int maxDepth, TSpinGuard guard
    ) {
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
                    TSpinGuard newGuard = guard;

                    {
                        Field freeze = field.freeze(height);
                        freeze.put(mino, x, y);
                        int currentDeletedLines = freeze.clearLine();

                        // ラインが消去されたか
                        if (0 < currentDeletedLines) {
                            //// Tスピンであるか
                            Optional<Spin> spinOptional = Optional.empty();
                            if (key.getPiece() == Piece.T) {
                                spinOptional = spinChecker.check(field, new SimpleMinoOperation(mino, x, y), height, currentDeletedLines);
                            }

                            if (spinOptional.isPresent()) {
                                Spin spin = spinOptional.get();
                                if (tSpinCondition.satisfy(spin)) {
                                    // 対象内でTスピン
                                    newGuard = newGuard.recordRequiredTSpin();
                                } else {
                                    // 対象外でTスピン
                                    newGuard = newGuard.recordUnrequiredTSpin();
                                }
                            } else {
                                // Tスピンではない
                                newGuard = newGuard.recordNormalClearedLine(currentDeletedLines);
                            }
                        }

                        if ((key.getPiece() == Piece.T || key.getPiece() == Piece.I) && newGuard.isAmbiguous()) {
                            // まだ条件を満たしていない
                            LinkedList<MinoOperationWithKey> ts = eachBlocks.get(Piece.T);
                            LinkedList<MinoOperationWithKey> is = eachBlocks.get(Piece.I);
                            if ((ts == null || ts.isEmpty()) && (is == null || is.isEmpty())) {
                                // Tミノ・Iミノともに残っていない
                                operationWithKeys.add(index, key);
                                continue;
                            }
                        }
                    }

                    if (newGuard.isFailed()) {
                        // まだ条件を満たしていない
                        operationWithKeys.add(index, key);
                        continue;
                    }

                    if (maxDepth == depth + 1) {
                        if (newGuard.isSatisfied()) {
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

                    boolean exists = existsValidByOrder(nextField, eachBlocks, pieces, height, reachable, depth + 1, maxDepth, newGuard);
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

        return existsValidByOrderWithHold(field.freeze(height), eachBlocks, pieces, height, reachable, maxDepth, 1, pieces.get(0), initGuard);
    }

    private boolean existsValidByOrderWithHold(
            Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height,
            ReachableForCover reachable, int maxDepth, int depth, Piece hold, TSpinGuard guard
    ) {
        long deleteKey = field.clearLineReturnKey();

        Piece piece = depth < pieces.size() ? pieces.get(depth) : null;

        if (hold != null && existsValidByOrderWithHold(field, eachBlocks, pieces, height, reachable, maxDepth, depth, hold, deleteKey, piece, guard)) {
            return true;
        }

        return piece != null && existsValidByOrderWithHold(field, eachBlocks, pieces, height, reachable, maxDepth, depth, piece, deleteKey, hold, guard);
    }

    private boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth, int depth, Piece usePiece, long deleteKey, Piece nextHoldPiece, TSpinGuard guard) {
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
                TSpinGuard newGuard = guard;

                {
                    Field freeze = field.freeze(height);
                    freeze.put(mino, x, y);
                    int currentDeletedLines = freeze.clearLine();

                    // ラインが消去されたか
                    if (0 < currentDeletedLines) {
                        //// Tスピンであるか
                        Optional<Spin> spinOptional = Optional.empty();
                        if (key.getPiece() == Piece.T) {
                            spinOptional = spinChecker.check(field, new SimpleMinoOperation(mino, x, y), height, currentDeletedLines);
                        }

                        if (spinOptional.isPresent()) {
                            Spin spin = spinOptional.get();
                            if (tSpinCondition.satisfy(spin)) {
                                // 対象内でTスピン
                                newGuard = newGuard.recordRequiredTSpin();
                            } else {
                                // 対象外でTスピン
                                newGuard = newGuard.recordUnrequiredTSpin();
                            }
                        } else {
                            // Tスピンではない
                            newGuard = newGuard.recordNormalClearedLine(currentDeletedLines);
                        }
                    }

                    if ((key.getPiece() == Piece.T || key.getPiece() == Piece.I) && newGuard.isAmbiguous()) {
                        // まだ条件を満たしていない
                        LinkedList<MinoOperationWithKey> ts = eachBlocks.get(Piece.T);
                        LinkedList<MinoOperationWithKey> is = eachBlocks.get(Piece.I);
                        if ((ts == null || ts.isEmpty()) && (is == null || is.isEmpty())) {
                            // Tミノ・Iミノともに残っていない
                            operationWithKeys.add(index, key);
                            continue;
                        }
                    }
                }

                if (newGuard.isFailed()) {
                    // まだ条件を満たしていない
                    operationWithKeys.add(index, key);
                    continue;
                }

                if (maxDepth == depth) {
                    if (newGuard.isSatisfied()) {
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

                boolean exists = existsValidByOrderWithHold(nextField, eachBlocks, pieces, height, reachable, maxDepth, depth + 1, nextHoldPiece, newGuard);
                if (exists)
                    return true;
            }

            operationWithKeys.add(index, key);
        }

        return false;
    }
}
