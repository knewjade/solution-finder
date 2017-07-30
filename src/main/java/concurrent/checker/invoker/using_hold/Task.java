package concurrent.checker.invoker.using_hold;

import common.ResultHelper;
import common.datastore.Operation;
import common.datastore.Pair;
import common.datastore.Result;
import common.datastore.action.Action;
import common.order.OrderLookup;
import common.order.StackOrder;
import common.tree.VisitedTree;
import core.action.candidate.Candidate;
import core.mino.Block;
import searcher.checker.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

class Task implements Callable<Pair<List<Block>, Boolean>> {
    private final Obj obj;
    private final List<Block> target;

    Task(Obj obj, List<Block> target) {
        this.obj = obj;
        this.target = target;
    }

    @Override
    public Pair<List<Block>, Boolean> call() throws Exception {
        // すでに探索済みならそのまま結果を追加
        int succeed = obj.visitedTree.isSucceed(target);
        if (succeed != VisitedTree.NO_RESULT)
            return new Pair<>(target, succeed == VisitedTree.SUCCEED);

        // 探索準備
        Checker<Action> checker = obj.checkerThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();

        // 探索
        boolean checkResult = checker.check(obj.field, target, candidate, obj.maxClearLine, obj.maxDepth);
        obj.visitedTree.set(checkResult, target);

        // もし探索に成功した場合
        // パフェが見つかったツモ順(≠探索時のツモ順)へと、ホールドを使ってできるパターンを逆算
        if (checkResult) {
            Result result = checker.getResult();
            List<Block> blocks = ResultHelper.createOperationStream(result)
                    .map(Operation::getBlock)
                    .collect(Collectors.toList());

            int reverseMaxDepth = result.getLastHold() != null ? blocks.size() + 1 : blocks.size();
            ArrayList<StackOrder<Block>> reversePieces = OrderLookup.reverseBlocks(blocks, reverseMaxDepth);

            for (StackOrder<Block> piece : reversePieces)
                obj.visitedTree.set(true, piece.toList());
        }

        return new Pair<>(target, checkResult);
    }
}
