package concurrent.checker.invoker.using_hold;

import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.pieces.Blocks;
import common.tree.ConcurrentVisitedTree;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import core.action.candidate.Candidate;
import core.field.Field;
import searcher.checker.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentCheckerUsingHoldInvoker implements ConcurrentCheckerInvoker {
    private final ExecutorService executorService;
    private final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private final ThreadLocal<Checker<Action>> checkerThreadLocal;

    public ConcurrentCheckerUsingHoldInvoker(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, CheckerUsingHoldThreadLocal<Action> checkerThreadLocal) {
        this.executorService = executorService;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }

    @Override
    public List<Pair<Blocks, Boolean>> search(Field field, List<Blocks> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        ConcurrentVisitedTree visitedTree = new ConcurrentVisitedTree();

        Obj obj = new Obj(field, maxClearLine, maxDepth, visitedTree, candidateThreadLocal, checkerThreadLocal);
        ArrayList<Task> tasks = new ArrayList<>();
        for (Blocks target : searchingPieces)
            tasks.add(new Task(obj, target));

        List<Future<Pair<Blocks, Boolean>>> futureResults = executorService.invokeAll(tasks);

        // 結果をリストに追加する
        ArrayList<Pair<Blocks, Boolean>> pairs = new ArrayList<>();
        for (Future<Pair<Blocks, Boolean>> future : futureResults)
            pairs.add(future.get());

        return pairs;
    }
}

