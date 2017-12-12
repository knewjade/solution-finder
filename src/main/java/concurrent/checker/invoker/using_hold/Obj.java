package concurrent.checker.invoker.using_hold;

import common.order.ReverseOrderLookUp;
import common.tree.ConcurrentVisitedTree;
import core.field.Field;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ConcurrentVisitedTree visitedTree;

    Obj(Field field, int maxClearLine, int maxDepth, ConcurrentVisitedTree visitedTree) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.visitedTree = visitedTree;
    }
}
