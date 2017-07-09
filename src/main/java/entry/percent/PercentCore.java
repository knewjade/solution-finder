package entry.percent;

import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.pieces.LongPieces;
import common.tree.AnalyzeTree;
import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import concurrent.checker.invoker.no_hold.ConcurrentCheckerNoHoldInvoker;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.field.Field;
import core.mino.Block;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

class PercentCore {
    private final ConcurrentCheckerInvoker invoker;

    private AnalyzeTree resultTree;
    private List<Pair<List<Block>, Boolean>> resultPairs;

    PercentCore(int maxClearLine, ExecutorService executorService, boolean isUsingHold) {
        this.invoker = createConcurrentCheckerInvoker(maxClearLine, executorService, isUsingHold);
    }

    private ConcurrentCheckerInvoker createConcurrentCheckerInvoker(int maxClearLine, ExecutorService executorService, boolean isUsingHold) {
        if (isUsingHold) {
            CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
            LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
            return new ConcurrentCheckerUsingHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);
        } else {
            CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
            LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
            return new ConcurrentCheckerNoHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);
        }
    }

    void run(Field field, Set<LongPieces> searchingPiecesSet, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        List<List<Block>> searchingPieces = searchingPiecesSet.stream()
                .map(LongPieces::getBlocks)
                .collect(Collectors.toList());

        this.resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 最低限の探索結果を集計する
        this.resultTree = new AnalyzeTree();
        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
            List<Block> pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            resultTree.set(result, pieces);
        }
    }

    AnalyzeTree getResultTree() {
        return resultTree;
    }

    List<Pair<List<Block>, Boolean>> getResultPairs() {
        return resultPairs;
    }
}
