package concurrent.checker.invoker.using_hold;

import common.order.ReverseOrderLookUp;
import common.tree.ConcurrentVisitedTree;
import core.field.Field;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ConcurrentVisitedTree visitedTree;
    final ReverseOrderLookUp lookUp;

    Obj(Field field, int maxClearLine, int maxDepth, ConcurrentVisitedTree visitedTree, ReverseOrderLookUp lookUp) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.visitedTree = visitedTree;
        this.lookUp = lookUp;
    }
}
