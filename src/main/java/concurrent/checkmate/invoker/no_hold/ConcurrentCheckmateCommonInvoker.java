package concurrent.checkmate.invoker.no_hold;

import core.action.candidate.Candidate;
import concurrent.checker.invoker.Pair;
import core.field.Field;
import core.mino.Block;
import misc.pieces.ComparablePieces;
import searcher.checkmate.Checkmate;
import searcher.common.Result;
import searcher.common.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ConcurrentCheckmateCommonInvoker {
    private final ExecutorService executorService;
    private final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private final ThreadLocal<Checkmate<Action>> checkmateThreadLocal;
    private final int taskSplitCount;

    public ConcurrentCheckmateCommonInvoker(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, ThreadLocal<Checkmate<Action>> checkmateThreadLocal, int taskSplitCount) {
        this.executorService = executorService;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkmateThreadLocal = checkmateThreadLocal;
        this.taskSplitCount = taskSplitCount;
    }

    public List<Pair<List<Block>, List<Result>>> search(Field field, List<List<Block>> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        // ミノごとにソートする
        List<ComparablePieces> sortedPieces = searchingPieces.stream()
                .map(ComparablePieces::new)
                .sorted()
                .collect(Collectors.toList());

        int size = sortedPieces.size();

        Obj obj = new Obj(field, maxClearLine, maxDepth, candidateThreadLocal, checkmateThreadLocal);

        ArrayList<Task> tasks = new ArrayList<>();
        int lastIndex = 0;
        for (int count = 0; count < taskSplitCount; count++) {
            int toIndex = (int) (size * ((double) (count + 1) / taskSplitCount));
            List<ComparablePieces> subPieces = sortedPieces.subList(lastIndex, toIndex);
            tasks.add(new Task(obj, subPieces));
            lastIndex = toIndex;
        }
        List<Future<List<Pair<List<Block>, List<Result>>>>> futures = executorService.invokeAll(tasks);

        List<Pair<List<Block>, List<Result>>> results = new ArrayList<>();
        for (Future<List<Pair<List<Block>, List<Result>>>> future : futures) {
            List<Pair<List<Block>, List<Result>>> pairs = future.get();
            results.addAll(pairs);
        }

        return results;
    }
}

