package concurrent.checker.invoker.no_hold;

import action.candidate.Candidate;
import concurrent.checker.invoker.Pair;
import core.mino.Block;
import searcher.checker.Checker;
import searcher.common.action.Action;

import java.util.List;
import java.util.concurrent.Callable;

class Task implements Callable<Pair<List<Block>, Boolean>> {
    private final Obj obj;
    private final List<Block> target;

    Task(Obj obj, List<Block> target) {
        this.obj = obj;
        this.target = target;
    }

    @Override
    public Pair<List<Block>, Boolean> call() throws Exception {
        // 探索
        Checker<Action> checker = obj.checkerThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();
        boolean checkResult = checker.check(obj.field, target, candidate, obj.maxClearLine, obj.maxDepth);
        return new Pair<>(target, checkResult);
    }
}
