package concurrent.checker.invoker.no_hold;

import common.datastore.Pair;
import common.datastore.blocks.Pieces;
import common.tree.ConcurrentVisitedTree;
import concurrent.checker.invoker.CheckerCommonObj;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import core.field.Field;
import exceptions.FinderExecuteException;

import java.util.ArrayList;
import java.util.List;

public class SingleCheckerNoHoldInvoker implements ConcurrentCheckerInvoker {
    private final CheckerCommonObj commonObj;

    public SingleCheckerNoHoldInvoker(CheckerCommonObj commonObj) {
        this.commonObj = commonObj;
    }

    @Override
    public List<Pair<Pieces, Boolean>> search(Field field, List<Pieces> searchingPieces, int maxClearLine, int maxDepth) throws FinderExecuteException {
        ConcurrentVisitedTree visitedTree = new ConcurrentVisitedTree();

        Obj obj = new Obj(field, maxClearLine, maxDepth, visitedTree);

        try {
            ArrayList<Pair<Pieces, Boolean>> results = new ArrayList<>();
            for (Pieces target : searchingPieces) {
                Task task = new Task(obj, commonObj, target);
                results.add(task.call());
            }
            return results;
        } catch (Exception e) {
            throw new FinderExecuteException(e);
        }
    }
}

