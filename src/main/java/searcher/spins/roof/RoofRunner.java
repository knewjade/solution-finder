package searcher.spins.roof;

import common.datastore.PieceCounter;
import core.action.reachable.RotateReachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.Solutions;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.results.Result;
import searcher.spins.roof.results.AddLastRoofResult;
import searcher.spins.roof.results.EmptyRoofResult;
import searcher.spins.roof.results.RoofResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoofRunner {
    private static final Comparator<RoofResult> COMPARATOR = Comparator.comparingInt(RoofResult::getNumOfUsingPiece);

    private final Roofs roofs;
    private final ThreadLocal<? extends RotateReachable> rotateReachableThreadLocal;
    private final int fieldHeight;
    private final int maxRoofNum;

    public RoofRunner(
            Roofs roofs,
            ThreadLocal<? extends RotateReachable> rotateReachableThreadLocal,
            int maxRoofNum,
            int fieldHeight
    ) {
        this.roofs = roofs;
        this.rotateReachableThreadLocal = rotateReachableThreadLocal;
        this.maxRoofNum = maxRoofNum;
        this.fieldHeight = fieldHeight;
    }

    public Stream<RoofResult> search(CandidateWithMask candidateWithMask) {
        long filledLineWithoutT = candidateWithMask.getAllMergedFilledLineWithoutT();
        EmptyRoofResult emptyResult = new EmptyRoofResult(candidateWithMask);
        return search(emptyResult, filledLineWithoutT);
    }

    private Stream<RoofResult> search(RoofResult roofResult, long filledLineWithoutT) {
        // 解であるか確認
        if (isSolution(roofResult)) {
            return Stream.of(roofResult);
        }

        // 屋根として使える個数を満たしていないか
        if (maxRoofNum <= roofResult.getNumOfRoofPieces()) {
            return Stream.empty();
        }

        // 残りのミノがある
        Result lastResult = roofResult.getLastResult();
        if (lastResult.getRemainderPieceCounter().isEmpty()) {
            return Stream.empty();
        }

        // 消去されるラインを記録
        long filledLine = lastResult.getAllMergedFilledLine();

        Solutions<Long> visitedKeys = new Solutions<>();
        Solutions<Long> solutions = new Solutions<>();

        PriorityQueue<RoofResult> candidates = new PriorityQueue<>(COMPARATOR);
        candidates.add(roofResult);

        Stream.Builder<RoofResult> builder = Stream.builder();

        while (!candidates.isEmpty()) {
            RoofResult poll = candidates.poll();
            List<RoofResult> results = this.localSearch(poll, visitedKeys, solutions, filledLine, filledLineWithoutT, builder);

            candidates.addAll(results);
        }

        return builder.build();
    }

    private boolean isSolution(RoofResult roofResult) {
        RotateReachable rotateReachable = rotateReachableThreadLocal.get();

        Result result = roofResult.getLastResult();
        Field field = result.getAllMergedField().freeze();
        SimpleOriginalPiece operationT = roofResult.getOperationT();

        // Tが回転入れで終了する
        field.reduce(operationT.getMinoField());
        long filledLineWithoutT = field.getFilledLine();
        assert operationT.getNeedDeletedKey() == 0L || (filledLineWithoutT & operationT.getNeedDeletedKey()) != 0L;
        field.clearLine();

        Mino mino = operationT.getMino();
        int y = operationT.getY();
        int slideY = Long.bitCount(filledLineWithoutT & KeyOperators.getMaskForKeyBelowY(y + mino.getMinY()));
        return rotateReachable.checks(field, mino, operationT.getX(), y - slideY, fieldHeight);
    }

    private List<RoofResult> localSearch(
            RoofResult initRoofResult, Solutions<Long> visitedKeys, Solutions<Long> solutions,
            long initFilledLine, long filledLineWithoutT, Stream.Builder<RoofResult> builder
    ) {
        List<RoofResult> candidates = new ArrayList<>();

        Result lastResult = initRoofResult.getLastResult();
        PieceCounter remainderPieceCounter = lastResult.getRemainderPieceCounter();

        roofs.get(initRoofResult.getAllMergedFieldWithoutT(), initRoofResult.getNotAllowedWithT(), filledLineWithoutT).forEach(originalPiece -> {
            // まだ未使用のミノである
            PieceCounter currentPieceCounter = PieceCounter.getSinglePieceCounter(originalPiece.getPiece());
            if (!remainderPieceCounter.containsAll(currentPieceCounter)) {
                return;
            }

            // キーに変換
            long key = originalPiece.toUniqueKey();
            Set<Long> prevKeys = initRoofResult.toKeyStream().collect(Collectors.toSet());
            Set<Long> currentKeys = new HashSet<>(prevKeys);
            currentKeys.add(key);

            // まだ探索されていない
            if (visitedKeys.contains(currentKeys)) {
                return;
            }

            // 探索されたことを記録する
            visitedKeys.add(currentKeys);

            // 次のresultを作る
            RoofResult nextResult = new AddLastRoofResult(initRoofResult, originalPiece);

            // 消去されるライン数が変わらない
            long filledLine = nextResult.getLastResult().getAllMergedFilledLine();
            if (filledLine != initFilledLine) {
                return;
            }

            if (isUniqueSolution(nextResult, prevKeys, key, solutions)) {
                solutions.add(currentKeys);
                builder.accept(nextResult);
            } else {
                // 屋根として使える個数を満たしていないか
                if (maxRoofNum <= nextResult.getNumOfRoofPieces()) {
                    return;
                }

                // まだ使用していないミノが残っている
                if (nextResult.getLastResult().getRemainderPieceCounter().isEmpty()) {
                    return;
                }

                candidates.add(nextResult);
            }
        });

        return candidates;
    }

    private boolean isUniqueSolution(RoofResult nextResult, Set<Long> prevKeys, long currentKey, Solutions<Long> solutions) {
        if (!isSolution(nextResult)) {
            return false;
        }

        // 現在の組み合わせでまだ解が見つかっていない
        return !solutions.partialContains(prevKeys, currentKey);
    }
}
