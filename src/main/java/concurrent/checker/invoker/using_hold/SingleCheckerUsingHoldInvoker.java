package concurrent.checker.invoker.using_hold;

import common.datastore.Pair;
import common.datastore.blocks.Pieces;
import common.tree.ConcurrentVisitedTree;
import concurrent.checker.invoker.CheckerCommonObj;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import core.field.Field;
import exceptions.FinderExecuteException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SingleCheckerUsingHoldInvoker implements ConcurrentCheckerInvoker {
    private final CheckerCommonObj commonObj;

    public SingleCheckerUsingHoldInvoker(CheckerCommonObj commonObj) {
        this.commonObj = commonObj;
    }

    @Override
    public List<Pair<Pieces, Boolean>> search(Field field, List<Pieces> searchingPieces, int maxClearLine, int maxDepth) throws FinderExecuteException {
        ConcurrentVisitedTree visitedTree = new ConcurrentVisitedTree();

        Obj obj = new Obj(field, maxClearLine, maxDepth, visitedTree);
        ArrayList<Pair<Pieces, Boolean>> results = new ArrayList<>();
        try {
            for (Pieces target : searchingPieces) {
                Task task = new Task(obj, commonObj, target);
                Pair<Pieces, Boolean> call = task.call();
                results.add(call);
            }
        } catch (Exception e) {
            throw new FinderExecuteException(e);
        }

        // 結果をリストに追加する
        return results;
    }
}

