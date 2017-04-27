package entry.percent;

import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import concurrent.checker.invoker.Pair;
import concurrent.checker.invoker.no_hold.ConcurrentCheckerNoHoldInvoker;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.field.Field;
import core.mino.Block;
import searcher.common.action.Action;
import misc.tree.AnalyzeTree;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

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

    void run(Field field, List<List<Block>> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
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
