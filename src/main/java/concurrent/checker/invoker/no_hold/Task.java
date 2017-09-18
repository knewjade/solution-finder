package concurrent.checker.invoker.no_hold;

import common.datastore.pieces.Blocks;
import core.action.candidate.Candidate;
import common.datastore.Pair;
import core.mino.Block;
import searcher.checker.Checker;
import common.datastore.action.Action;

import java.util.List;
import java.util.concurrent.Callable;

class Task implements Callable<Pair<Blocks, Boolean>> {
    private final Obj obj;
    private final Blocks target;

    Task(Obj obj, Blocks target) {
        this.obj = obj;
        this.target = target;
    }

    @Override
    public Pair<Blocks, Boolean> call() throws Exception {
        // 探索
        Checker<Action> checker = obj.checkerThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();
        boolean checkResult = checker.check(obj.field, target.getBlocks(), candidate, obj.maxClearLine, obj.maxDepth);
        return new Pair<>(target, checkResult);
    }
}
