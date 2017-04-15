package concurrent.checker.invoker.v2;

import action.candidate.Candidate;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.invoker.Pair;
import core.field.Field;
import core.mino.Block;
import searcher.checker.Checker;
import searcher.common.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentNoHoldCheckerInvoker {
    private final ExecutorService executorService;
    private final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private final ThreadLocal<Checker<Action>> checkerThreadLocal;

    public ConcurrentNoHoldCheckerInvoker(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, CheckerNoHoldThreadLocal<Action> checkerThreadLocal) {
        this.executorService = executorService;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }

    public List<Pair<List<Block>, Boolean>> search(Field field, List<List<Block>> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        ObjV2 obj = new ObjV2(field, maxClearLine, maxDepth, candidateThreadLocal, checkerThreadLocal);
        ArrayList<TaskV2> tasks = new ArrayList<>();
        for (List<Block> target : searchingPieces)
            tasks.add(new TaskV2(obj, target));

        List<Future<Pair<List<Block>, Boolean>>> futureResults = executorService.invokeAll(tasks);

        // 結果をリストに追加する
        ArrayList<Pair<List<Block>, Boolean>> pairs = new ArrayList<>();
        for (Future<Pair<List<Block>, Boolean>> future : futureResults)
            pairs.add(future.get());

        return pairs;
    }
}

