package concurrent.checker.invoker.using_hold;

import common.tree.ConcurrentVisitedTree;
import core.field.Field;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ConcurrentVisitedTree visitedTree;

    Obj(Field field, int maxClearLine, int maxDepth, ConcurrentVisitedTree visitedTree) {
        Field freeze = field.freeze();
        int lines_cleared = freeze.clearLine();
        this.field = freeze;
        this.maxClearLine = maxClearLine - lines_cleared;
        this.maxDepth = maxDepth;
        this.visitedTree = visitedTree;
    }
}
