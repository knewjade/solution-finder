package concurrent.full_checkmate.invoker.no_hold;

import core.action.candidate.Candidate;
import concurrent.checker.invoker.Pair;
import concurrent.full_checkmate.FullCheckmateNoHoldThreadLocal;
import core.field.Field;
import searcher.common.Operation;
import searcher.common.Result;
import searcher.common.action.Action;
import searcher.common.validator.FullValidator;
import searcher.full_checkmate.FullCheckmate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentFullCheckmateNoHoldInvoker {
    private final ExecutorService executorService;
    private final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private final ThreadLocal<FullCheckmate<Action>> checkmateThreadLocal;

    public ConcurrentFullCheckmateNoHoldInvoker(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, FullCheckmateNoHoldThreadLocal<Action> checkmateThreadLocal) {
        this.executorService = executorService;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkmateThreadLocal = checkmateThreadLocal;
    }

    public List<Pair<List<Operation>, List<Result>>> search(Field field, List<Pair<List<Operation>, FullValidator>> searching, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        Obj obj = new Obj(field, maxClearLine, maxDepth, candidateThreadLocal, checkmateThreadLocal);
        ArrayList<Task> tasks = new ArrayList<>();
        for (Pair<List<Operation>, FullValidator> target : searching)
            tasks.add(new Task(obj, target));

        List<Future<Pair<List<Operation>, List<Result>>>> futureResults = executorService.invokeAll(tasks);

        // 結果をリストに追加する
        ArrayList<Pair<List<Operation>, List<Result>>> pairs = new ArrayList<>();
        for (Future<Pair<List<Operation>, List<Result>>> future : futureResults)
            pairs.add(future.get());

        return pairs;
    }
}

