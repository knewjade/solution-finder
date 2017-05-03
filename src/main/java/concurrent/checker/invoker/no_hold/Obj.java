package concurrent.checker.invoker.no_hold;

import core.action.candidate.Candidate;
import core.field.Field;
import searcher.checker.Checker;
import common.datastore.action.Action;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    final ThreadLocal<Checker<Action>> checkerThreadLocal;

    Obj(Field field, int maxClearLine, int maxDepth, ThreadLocal<Candidate<Action>> candidateThreadLocal, ThreadLocal<Checker<Action>> checkerThreadLocal) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }
}
