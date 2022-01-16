package common.cover;

import common.cover.reachable.ReachableForCover;
import common.datastore.MinoOperationWithKey;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.Piece;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ClearLinesCover implements Cover {
    interface Progress {
        boolean satisfies(int maxSoftdrop, int maxClearLine);

        boolean isValid();

        Progress useSoftdrop();

        Progress clearLines();

        Progress useSoftdropAndClearLines();
    }

    static class ValidProgress implements Progress {
        static ValidProgress create() {
            return new ValidProgress(0, 0);
        }

        private final int softdropCount;
        private final int clearLineCount;

        private ValidProgress(int softdropCount, int clearLineCount) {
            this.softdropCount = softdropCount;
            this.clearLineCount = clearLineCount;
        }

        @Override
        public boolean satisfies(int maxSoftdrop, int maxClearLine) {
            return softdropCount <= maxSoftdrop && clearLineCount <= maxClearLine;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public Progress useSoftdrop() {
            return new ValidProgress(softdropCount + 1, clearLineCount);
        }

        @Override
        public Progress clearLines() {
            return new ValidProgress(softdropCount, clearLineCount + 1);
        }

        @Override
        public Progress useSoftdropAndClearLines() {
            return new ValidProgress(softdropCount + 1, clearLineCount + 1);
        }
    }

    private static final Progress FAILED_PROGRESS = new Progress() {
        @Override
        public boolean satisfies(int maxSoftdrop, int maxClearLine) {
            return false;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Progress useSoftdrop() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Progress clearLines() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Progress useSoftdropAndClearLines() {
            throw new UnsupportedOperationException();
        }
    };

    interface ProgressGenerator {
        Progress increment(Progress current, Field field, Mino mino, int x, int y);
    }

    interface ClearLinesCondition {
        boolean satisfies(int clearedLine);
    }

    public static ClearLinesCover createEqualTo(int requiredClearLines, boolean allowsPc) {
        return createEqualTo(requiredClearLines, allowsPc, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static ClearLinesCover createEqualTo(int requiredClearLines, boolean allowsPc, int maxSoftdrop, int maxClearLine) {
        return create((clearedLine) -> clearedLine == requiredClearLines, allowsPc, maxSoftdrop, maxClearLine);
    }

    public static ClearLinesCover createEqualToOrGreaterThan(int requiredClearLines, boolean allowsPc) {
        return createEqualToOrGreaterThan(requiredClearLines, allowsPc, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static ClearLinesCover createEqualToOrGreaterThan(int requiredClearLines, boolean allowsPc, int maxSoftdrop, int maxClearLine) {
        return create((clearedLine) -> requiredClearLines <= clearedLine, allowsPc, maxSoftdrop, maxClearLine);
    }

    private static ClearLinesCover create(ClearLinesCondition clearLinesCondition, boolean allowsPc, int maxSoftdrop, int maxClearLine) {
        return new ClearLinesCover(new ProgressGenerator() {
            @Override
            public Progress increment(Progress current, Field field, Mino mino, int x, int y) {
                Progress next = getProgress(current, field, mino, x, y);

                if (next.satisfies(maxSoftdrop, maxClearLine)) {
                    return next;
                }

                return FAILED_PROGRESS;
            }

            private Progress getProgress(Progress current, Field field, Mino mino, int x, int y) {
                Field freeze = field.freeze();
                freeze.put(mino, x, y);
                int clearedLine = freeze.clearLine();

                boolean harddrop = field.canReachOnHarddrop(mino, x, y);

                if (clearedLine == 0) {
                    return harddrop ? current : current.useSoftdrop();
                }

                if (clearLinesCondition.satisfies(clearedLine)) {
                    return harddrop ? current.clearLines() : current.useSoftdropAndClearLines();
                }

                if (allowsPc) {
                    if (freeze.isEmpty()) {
                        return harddrop ? current.clearLines() : current.useSoftdropAndClearLines();
                    } else {
                        return FAILED_PROGRESS;
                    }
                }

                return FAILED_PROGRESS;
            }
        });
    }

    public static ClearLinesCover createNormal() {
        return new ClearLinesCover((current, field, mino, x, y) -> current);
    }

    private final ProgressGenerator generator;

    private ClearLinesCover(ProgressGenerator condition) {
        this.generator = condition;
    }

    @Override
    public boolean canBuild(Field field, Stream<? extends MinoOperationWithKey> operations, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth) {
        if (pieces.size() < maxDepth) {
            return false;
        }

        Progress progress = ValidProgress.create();

        EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks = operations.sequential().collect(() -> new EnumMap<>(Piece.class), (blockLinkedListEnumMap, operationWithKey) -> {
            Piece piece = operationWithKey.getPiece();
            LinkedList<MinoOperationWithKey> operationWithKeys = blockLinkedListEnumMap.computeIfAbsent(piece, b -> new LinkedList<>());
            operationWithKeys.add(operationWithKey);
        }, EnumMap::putAll);

        return existsValidByOrder(field.freeze(height), eachBlocks, pieces, height, reachable, 0, maxDepth, progress);
    }

    private boolean existsValidByOrder(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int depth, int maxDepth, Progress progress) {
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
                    Progress nextProgress = generator.increment(progress, field, mino, x, y);

                    if (nextProgress.isValid()) {
                        operationWithKeys.add(index, key);
                        continue;
                    }

                    if (maxDepth == depth + 1) {
                        return true;
                    }

                    Field nextField = field.freeze(height);
                    nextField.put(mino, x, y);
                    nextField.insertBlackLineWithKey(deleteKey);

                    boolean exists = existsValidByOrder(nextField, eachBlocks, pieces, height, reachable, depth + 1, maxDepth, nextProgress);
                    if (exists) return true;
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

        Progress progress = ValidProgress.create();

        EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks = operations.sequential().collect(() -> new EnumMap<>(Piece.class), (blockLinkedListEnumMap, operationWithKey) -> {
            Piece piece = operationWithKey.getPiece();
            LinkedList<MinoOperationWithKey> operationWithKeys = blockLinkedListEnumMap.computeIfAbsent(piece, b -> new LinkedList<>());
            operationWithKeys.add(operationWithKey);
        }, EnumMap::putAll);

        return existsValidByOrderWithHold(field.freeze(height), eachBlocks, pieces, height, reachable, maxDepth, 1, pieces.get(0), progress);
    }

    private boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth, int depth, Piece hold, Progress progress) {
        long deleteKey = field.clearLineReturnKey();

        Piece piece = depth < pieces.size() ? pieces.get(depth) : null;

        if (hold != null && existsValidByOrderWithHold(field, eachBlocks, pieces, height, reachable, maxDepth, depth, hold, deleteKey, piece, progress)) {
            return true;
        }

        if (piece != null && existsValidByOrderWithHold(field, eachBlocks, pieces, height, reachable, maxDepth, depth, piece, deleteKey, hold, progress)) {
            return true;
        }

        return false;
    }

    private boolean existsValidByOrderWithHold(Field field, EnumMap<Piece, LinkedList<MinoOperationWithKey>> eachBlocks, List<Piece> pieces, int height, ReachableForCover reachable, int maxDepth, int depth, Piece usePiece, long deleteKey, Piece nextHoldPiece, Progress progress) {
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
                Progress nextProgress = generator.increment(progress, field, mino, x, y);

                if (nextProgress.isValid()) {
                    operationWithKeys.add(index, key);
                    continue;
                }

                if (maxDepth == depth) {
                    return true;
                }

                Field nextField = field.freeze(height);
                nextField.put(mino, x, y);
                nextField.insertBlackLineWithKey(deleteKey);

                boolean exists = existsValidByOrderWithHold(nextField, eachBlocks, pieces, height, reachable, maxDepth, depth + 1, nextHoldPiece, nextProgress);
                if (exists) return true;
            }

            operationWithKeys.add(index, key);
        }

        return false;
    }
}
