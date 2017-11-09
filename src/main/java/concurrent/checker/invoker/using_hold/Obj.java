package concurrent.checker.invoker.using_hold;

import common.datastore.action.Action;
import common.tree.ConcurrentVisitedTree;
import core.action.candidate.Candidate;
import core.field.Field;
import searcher.checker.Checker;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ConcurrentVisitedTree visitedTree;
    final ThreadLocal<Candidate<? extends Action>> candidateThreadLocal;
    final ThreadLocal<Checker> checkerThreadLocal;

    Obj(Field field, int maxClearLine, int maxDepth, ConcurrentVisitedTree visitedTree, ThreadLocal<Candidate<? extends Action>> candidateThreadLocal, ThreadLocal<Checker> checkerThreadLocal) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.visitedTree = visitedTree;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }
}
