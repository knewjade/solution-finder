package concurrent.checker.invoker.using_hold;

import common.ResultHelper;
import common.datastore.Operation;
import common.datastore.Pair;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.blocks.Blocks;
import common.order.OrderLookup;
import common.tree.VisitedTree;
import core.action.candidate.Candidate;
import core.mino.Block;
import searcher.checker.Checker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

class Task implements Callable<Pair<Blocks, Boolean>> {
    private final Obj obj;
    private final Blocks target;

    Task(Obj obj, Blocks target) {
        this.obj = obj;
        this.target = target;
    }

    @Override
    public Pair<Blocks, Boolean> call() throws Exception {
        List<Block> blockList = target.getBlocks();

        // すでに探索済みならそのまま結果を追加
        int succeed = obj.visitedTree.isSucceed(blockList);
        if (succeed != VisitedTree.NO_RESULT)
            return new Pair<>(target, succeed == VisitedTree.SUCCEED);

        // 探索準備
        Checker<Action> checker = obj.checkerThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();

        // 探索
        boolean checkResult = checker.check(obj.field, blockList, candidate, obj.maxClearLine, obj.maxDepth);
        obj.visitedTree.set(checkResult, blockList);

        // もし探索に成功した場合
        // パフェが見つかったツモ順(≠探索時のツモ順)へと、ホールドを使ってできるパターンを逆算
        if (checkResult) {
            Result result = checker.getResult();
            List<Block> resultBlockList = ResultHelper.createOperationStream(result)
                    .map(Operation::getBlock)
                    .collect(Collectors.toList());

            int reverseMaxDepth = result.getLastHold() != null ? resultBlockList.size() + 1 : resultBlockList.size();
            OrderLookup.reverseBlocks(resultBlockList, reverseMaxDepth)
                    .forEach(piece -> obj.visitedTree.set(true, piece.toList()));
        }

        return new Pair<>(target, checkResult);
    }
}
