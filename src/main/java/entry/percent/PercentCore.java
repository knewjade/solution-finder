package entry.percent;

import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.tree.AnalyzeTree;
import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import concurrent.checker.invoker.no_hold.ConcurrentCheckerNoHoldInvoker;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.field.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

class PercentCore {
    private final ConcurrentCheckerInvoker invoker;

    private AnalyzeTree resultTree;
    private List<Pair<Blocks, Boolean>> resultPairs;

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

    void run(Field field, Set<LongBlocks> searchingPiecesSet, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        List<Blocks> searchingPieces = new ArrayList<>(searchingPiecesSet);

        this.resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 最低限の探索結果を集計する
        this.resultTree = new AnalyzeTree();
        for (Pair<Blocks, Boolean> resultPair : resultPairs) {
            Blocks pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            resultTree.set(result, pieces);
        }
    }

    AnalyzeTree getResultTree() {
        return resultTree;
    }

    List<Pair<Blocks, Boolean>> getResultPairs() {
        return resultPairs;
    }
}
