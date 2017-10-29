package concurrent.checker.invoker.no_hold;

import common.datastore.blocks.Pieces;
import core.action.candidate.Candidate;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import common.datastore.Pair;
import core.field.Field;
import searcher.checker.Checker;
import common.datastore.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentCheckerNoHoldInvoker implements ConcurrentCheckerInvoker {
    private final ExecutorService executorService;
    private final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private final ThreadLocal<Checker<Action>> checkerThreadLocal;

    public ConcurrentCheckerNoHoldInvoker(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, CheckerNoHoldThreadLocal<Action> checkerThreadLocal) {
        this.executorService = executorService;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }

    @Override
    public List<Pair<Pieces, Boolean>> search(Field field, List<Pieces> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        Obj obj = new Obj(field, maxClearLine, maxDepth, candidateThreadLocal, checkerThreadLocal);
        ArrayList<Task> tasks = new ArrayList<>();
        for (Pieces target : searchingPieces)
            tasks.add(new Task(obj, target));

        List<Future<Pair<Pieces, Boolean>>> futureResults = executorService.invokeAll(tasks);

        // 結果をリストに追加する
        ArrayList<Pair<Pieces, Boolean>> pairs = new ArrayList<>();
        for (Future<Pair<Pieces, Boolean>> future : futureResults)
            pairs.add(future.get());

        return pairs;
    }
}

