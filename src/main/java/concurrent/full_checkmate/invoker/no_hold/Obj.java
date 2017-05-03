package concurrent.full_checkmate.invoker.no_hold;

import core.action.candidate.Candidate;
import core.field.Field;
import common.datastore.action.Action;
import searcher.full_checkmate.FullCheckmate;

class Obj {
    final Field field;
    final int maxClearLine;
    final int maxDepth;
    final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    final ThreadLocal<FullCheckmate<Action>> checkmateThreadLocal;

    Obj(Field field, int maxClearLine, int maxDepth, ThreadLocal<Candidate<Action>> candidateThreadLocal, ThreadLocal<FullCheckmate<Action>> checkmateThreadLocal) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.maxDepth = maxDepth;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkmateThreadLocal = checkmateThreadLocal;
    }
}
