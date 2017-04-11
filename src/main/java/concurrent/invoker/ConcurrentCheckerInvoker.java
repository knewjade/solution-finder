package concurrent.invoker;

import action.candidate.Candidate;
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

public class ConcurrentCheckerInvoker {
    private final ExecutorService executorService;
    private final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private final ThreadLocal<Checker<Action>> checkerThreadLocal;

    public ConcurrentCheckerInvoker(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, ThreadLocal<Checker<Action>> checkerThreadLocal) {
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

