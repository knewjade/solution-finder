package searcher.spins.roof;

import common.datastore.PieceCounter;
import core.action.reachable.RotateReachable;
import core.field.Field;
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
    //    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final ThreadLocal<? extends RotateReachable> rotateReachableThreadLocal;
    private final int maxTargetHeight;

    public RoofRunner(
            Roofs roofs,
//            ThreadLocal<? extends Reachable> reachableThreadLocal,
            ThreadLocal<? extends RotateReachable> rotateReachableThreadLocal,
            int maxTargetHeight
    ) {
        this.roofs = roofs;
//        this.reachableThreadLocal = reachableThreadLocal;
        this.rotateReachableThreadLocal = rotateReachableThreadLocal;
        this.maxTargetHeight = maxTargetHeight;
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
//        Reachable reachable = reachableThreadLocal.get();
        RotateReachable rotateReachable = rotateReachableThreadLocal.get();

        Result result = roofResult.getLastResult();
        Field field = result.freezeAllMergedField();
        SimpleOriginalPiece operationT = roofResult.getOperationT();

        // Tが回転入れで終了する
        field.reduce(operationT.getMinoField());
        field.deleteLineWithKey(operationT.getNeedDeletedKey());
        if (!rotateReachable.checks(field, operationT.getMino(), operationT.getX(), operationT.getY(), maxTargetHeight)) {
            return false;
        }

//        Result result = roofResult.getLastResult();
//        Field field = result.freezeAllMergedField();
//
//        // 空中に浮いているミノがない
//        SimpleOriginalPiece operationT = roofResult.getOperationT();
//        if (!SpinCommons.existsAllOnGroundWithT(field, roofResult.targetOperationStream(), operationT)) {
//            return false;
//        }
//
//        // T以外のミノを実際に組む手順が存在する
//        Field initField = result.getInitField();
//        List<SimpleOriginalPiece> operations = result.operationStream().collect(Collectors.toList());
//        return BuildUp.existsValidBuildPattern(initField, operations, maxTargetHeight, reachable);

        return true;
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
