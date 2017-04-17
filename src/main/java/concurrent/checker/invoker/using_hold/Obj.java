package concurrent.checker.invoker.using_hold;

import action.candidate.Candidate;
import core.field.Field;
import searcher.checker.Checker;
import searcher.common.action.Action;
import tree.ConcurrentVisitedTree;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ConcurrentVisitedTree visitedTree;
    final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    final ThreadLocal<Checker<Action>> checkerThreadLocal;

    Obj(Field field, int maxClearLine, int maxDepth, ConcurrentVisitedTree visitedTree, ThreadLocal<Candidate<Action>> candidateThreadLocal, ThreadLocal<Checker<Action>> checkerThreadLocal) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.visitedTree = visitedTree;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }
}