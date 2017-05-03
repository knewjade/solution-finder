package concurrent.full_checkmate.invoker.no_hold;

import core.action.candidate.Candidate;
import common.datastore.Pair;
import core.mino.Block;
import common.datastore.Operation;
import searcher.common.Result;
import common.datastore.action.Action;
import searcher.common.validator.FullValidator;
import searcher.full_checkmate.FullCheckmate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class Task implements Callable<Pair<List<Operation>, List<Result>>> {
    private final Obj obj;
    private final Pair<List<Operation>, FullValidator> target;

    Task(Obj obj, Pair<List<Operation>, FullValidator> target) {
        this.obj = obj;
        this.target = target;
    }

    @Override
    public Pair<List<Operation>, List<Result>> call() throws Exception {
        // 探索
        FullCheckmate<Action> checkmate = obj.checkmateThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();
        List<Operation> operations = target.getKey();

        // blocksの作成
        ArrayList<Block> blocks = new ArrayList<>();
        for (Operation operation : operations)
            blocks.add(operation.getBlock());

        FullValidator validator = target.getValue();
        List<Result> result = checkmate.search(obj.field, blocks, candidate, validator, obj.maxClearLine, obj.maxDepth);
        return new Pair<>(operations, result);
    }
}

