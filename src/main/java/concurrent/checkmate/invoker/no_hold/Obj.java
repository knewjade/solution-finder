package concurrent.checkmate.invoker.no_hold;

import core.action.candidate.Candidate;
import core.field.Field;
import searcher.checkmate.Checkmate;
import searcher.common.action.Action;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    final ThreadLocal<Checkmate<Action>> checkmateThreadLocal;

    Obj(Field field, int maxClearLine, int maxDepth, ThreadLocal<Candidate<Action>> candidateThreadLocal, ThreadLocal<Checkmate<Action>> checkmateThreadLocal) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkmateThreadLocal = checkmateThreadLocal;
    }
}
