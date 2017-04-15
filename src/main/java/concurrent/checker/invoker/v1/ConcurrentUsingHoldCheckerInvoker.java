package concurrent.checker.invoker.v1;

import action.candidate.Candidate;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.Pair;
import core.field.Field;
import core.mino.Block;
import searcher.checker.Checker;
import searcher.common.action.Action;
import tree.ConcurrentVisitedTree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentUsingHoldCheckerInvoker {
    private final ExecutorService executorService;
    private final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private final ThreadLocal<Checker<Action>> checkerThreadLocal;

    public ConcurrentUsingHoldCheckerInvoker(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, CheckerUsingHoldThreadLocal<Action> checkerThreadLocal) {
        this.executorService = executorService;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }

    public List<Pair<List<Block>, Boolean>> search(Field field, List<List<Block>> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        ConcurrentVisitedTree visitedTree = new ConcurrentVisitedTree();

        Obj obj = new Obj(field, maxClearLine, maxDepth, visitedTree, candidateThreadLocal, checkerThreadLocal);
        ArrayList<Task> tasks = new ArrayList<>();
        for (List<Block> target : searchingPieces)
            tasks.add(new Task(obj, target));

        List<Future<Pair<List<Block>, Boolean>>> futureResults = executorService.invokeAll(tasks);

        // 結果をリストに追加する
        ArrayList<Pair<List<Block>, Boolean>> pairs = new ArrayList<>();
        for (Future<Pair<List<Block>, Boolean>> future : futureResults)
            pairs.add(future.get());

        return pairs;
    }
}

