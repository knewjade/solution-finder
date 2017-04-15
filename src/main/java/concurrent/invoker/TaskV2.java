package concurrent.invoker;

import action.candidate.Candidate;
import core.mino.Block;
import searcher.checker.Checker;
import searcher.common.Operation;
import searcher.common.action.Action;

import java.util.ArrayList;
import java.util.List;

class TaskV2 implements Runnable {
    private final ObjV2 obj;
    private final List<Block> target;

    TaskV2(ObjV2 obj, List<Block> target) {
        this.obj = obj;
        this.target = target;
    }

    @Override
    public void run() {
        // 探索  // すでに成功確定済みでも、別のholdパターンを列挙するため探索が必要
        Checker<Action> checker = obj.checkerThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();
        boolean checkResult = checker.check(obj.field, target, candidate, obj.maxClearLine, obj.maxDepth);

        // 成功を記録する
        // もし探索に成功した場合
        // パフェが見つかったツモ順(≠探索時のツモ順)へと、ホールドを使ってできるパターンを逆算
        if (checkResult) {
            obj.visitedTree.set(true, target);
            int reverseMaxDepth = target.size();
            ArrayList<Pieces> reversePieces = OrderLookup.reverse(target, reverseMaxDepth);
            for (Pieces piece : reversePieces) {
                obj.visitedTree.set(true, piece.getBlocks());
            }
        }

        obj.countDownLatch.countDown();
    }

    private ArrayList<Block> parseOperationsToBlockList(List<Operation> operations) {
        ArrayList<Block> operationBlocks = new ArrayList<>();
        for (Operation operation : operations)
            operationBlocks.add(operation.getBlock());
        return operationBlocks;
    }
}
