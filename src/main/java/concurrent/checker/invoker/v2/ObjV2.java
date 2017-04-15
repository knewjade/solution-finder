package concurrent.checker.invoker.v2;

import action.candidate.Candidate;
import core.field.Field;
import searcher.checker.Checker;
import searcher.common.action.Action;

class ObjV2 {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    final ThreadLocal<Checker<Action>> checkerThreadLocal;

    ObjV2(Field field, int maxClearLine, int maxDepth, ThreadLocal<Candidate<Action>> candidateThreadLocal, ThreadLocal<Checker<Action>> checkerThreadLocal) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }
}
