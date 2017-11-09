package concurrent.checker.invoker.no_hold;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.field.Field;
import searcher.checker.Checker;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ThreadLocal<Candidate<? extends Action>> candidateThreadLocal;
    final ThreadLocal<Checker> checkerThreadLocal;

    Obj(Field field, int maxClearLine, int maxDepth, ThreadLocal<Candidate<? extends Action>> candidateThreadLocal, ThreadLocal<Checker> checkerThreadLocal) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }
}
