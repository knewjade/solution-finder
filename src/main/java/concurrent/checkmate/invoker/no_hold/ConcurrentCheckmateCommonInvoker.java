package concurrent.checkmate.invoker.no_hold;

import common.datastore.blocks.ReadOnlyListPieces;
import core.action.candidate.Candidate;
import common.datastore.Pair;
import core.field.Field;
import core.mino.Piece;
import searcher.checkmate.Checkmate;
import common.datastore.Result;
import common.datastore.action.Action;

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

    public List<Pair<List<Piece>, List<Result>>> search(Field field, List<List<Piece>> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        // ミノごとにソートする
        List<ReadOnlyListPieces> sortedPieces = searchingPieces.stream()
                .map(ReadOnlyListPieces::new)
                .sorted()
                .collect(Collectors.toList());

        int size = sortedPieces.size();

        Obj obj = new Obj(field, maxClearLine, maxDepth, candidateThreadLocal, checkmateThreadLocal);

        ArrayList<Task> tasks = new ArrayList<>();
        int lastIndex = 0;
        for (int count = 0; count < taskSplitCount; count++) {
            int toIndex = (int) (size * ((double) (count + 1) / taskSplitCount));
            List<ReadOnlyListPieces> subPieces = sortedPieces.subList(lastIndex, toIndex);
            tasks.add(new Task(obj, subPieces));
            lastIndex = toIndex;
        }
        List<Future<List<Pair<List<Piece>, List<Result>>>>> futures = executorService.invokeAll(tasks);

        List<Pair<List<Piece>, List<Result>>> results = new ArrayList<>();
        for (Future<List<Pair<List<Piece>, List<Result>>>> future : futures) {
            List<Pair<List<Piece>, List<Result>>> pairs = future.get();
            results.addAll(pairs);
        }

        return results;
    }
}

