package concurrent.checker.invoker;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.action.reachable.Reachable;
import core.mino.MinoFactory;
import searcher.checker.Checker;

public class CheckerCommonObj {
    public final MinoFactory minoFactory;
    public final ThreadLocal<? extends Candidate<Action>> candidateThreadLocal;
    public final ThreadLocal<Checker<Action>> checkerThreadLocal;
    public final ThreadLocal<? extends Reachable> reachableThreadLocal;

    public CheckerCommonObj(
            MinoFactory minoFactory,
            ThreadLocal<? extends Candidate<Action>> candidateThreadLocal,
            ThreadLocal<Checker<Action>> checkerThreadLocal,
            ThreadLocal<? extends Reachable> reachableThreadLocal
    ) {
        this.minoFactory = minoFactory;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
        this.reachableThreadLocal = reachableThreadLocal;
    }
}
