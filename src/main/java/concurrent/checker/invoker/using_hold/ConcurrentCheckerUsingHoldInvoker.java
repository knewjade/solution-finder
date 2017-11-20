package concurrent.checker.invoker.using_hold;

import common.datastore.Pair;
import common.datastore.blocks.Pieces;
import common.order.ReverseOrderLookUp;
import common.tree.ConcurrentVisitedTree;
import concurrent.checker.invoker.CheckerCommonObj;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import core.field.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentCheckerUsingHoldInvoker implements ConcurrentCheckerInvoker {
    private final ExecutorService executorService;
    private final CheckerCommonObj commonObj;
    private final int fromDepth;

    public ConcurrentCheckerUsingHoldInvoker(ExecutorService executorService, CheckerCommonObj commonObj, int fromDepth) {
        this.executorService = executorService;
        this.commonObj = commonObj;
        this.fromDepth = fromDepth;
    }

    @Override
    public List<Pair<Pieces, Boolean>> search(Field field, List<Pieces> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        ConcurrentVisitedTree visitedTree = new ConcurrentVisitedTree();
        ReverseOrderLookUp lookUp = new ReverseOrderLookUp(maxDepth, fromDepth);

        Obj obj = new Obj(field, maxClearLine, maxDepth, visitedTree, lookUp);
        ArrayList<Task> tasks = new ArrayList<>();
        for (Pieces target : searchingPieces)
            tasks.add(new Task(obj, commonObj, target));

        List<Future<Pair<Pieces, Boolean>>> futureResults = executorService.invokeAll(tasks);

        // 結果をリストに追加する
        ArrayList<Pair<Pieces, Boolean>> pairs = new ArrayList<>();
        for (Future<Pair<Pieces, Boolean>> future : futureResults)
            pairs.add(future.get());

        return pairs;
    }
}

