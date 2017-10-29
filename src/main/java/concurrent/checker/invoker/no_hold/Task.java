package concurrent.checker.invoker.no_hold;

import common.datastore.blocks.Pieces;
import core.action.candidate.Candidate;
import common.datastore.Pair;
import searcher.checker.Checker;
import common.datastore.action.Action;

import java.util.concurrent.Callable;

class Task implements Callable<Pair<Pieces, Boolean>> {
    private final Obj obj;
    private final Pieces target;

    Task(Obj obj, Pieces target) {
        this.obj = obj;
        this.target = target;
    }

    @Override
    public Pair<Pieces, Boolean> call() throws Exception {
        // 探索
        Checker<Action> checker = obj.checkerThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();
        boolean checkResult = checker.check(obj.field, target.getPieces(), candidate, obj.maxClearLine, obj.maxDepth);
        return new Pair<>(target, checkResult);
    }
}
