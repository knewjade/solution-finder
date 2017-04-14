package concurrent.invoker;

import action.candidate.Candidate;
import concurrent.CheckerNoHoldThreadLocal;
import core.field.Field;
import core.mino.Block;
import searcher.checker.Checker;
import searcher.common.Operation;
import searcher.common.action.Action;
import tree.ConcurrentVisitedTree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ConcurrentCheckerInvokerV2 {
    private final ExecutorService executorService;
    private final ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private final ThreadLocal<Checker<Action>> checkerThreadLocal;

    public ConcurrentCheckerInvokerV2(ExecutorService executorService, ThreadLocal<Candidate<Action>> candidateThreadLocal, CheckerNoHoldThreadLocal<Action> checkerThreadLocal) {
        this.executorService = executorService;
        this.candidateThreadLocal = candidateThreadLocal;
        this.checkerThreadLocal = checkerThreadLocal;
    }

    public List<Pair<List<Block>, Boolean>> search(Field field, List<List<Block>> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException {
        ConcurrentVisitedTree visitedTree = new ConcurrentVisitedTree();

        // タスクの実行
        ObjV2 obj = new ObjV2(field, maxClearLine, maxDepth, visitedTree, candidateThreadLocal, checkerThreadLocal, searchingPieces.size());
        for (List<Block> target : searchingPieces)
            executorService.submit(new TaskV2(obj, target));

        while (0 < obj.countDownLatch.getCount()) {
            System.out.println(obj.countDownLatch.getCount());
            obj.countDownLatch.await(1000L, TimeUnit.MILLISECONDS);
        }
        System.out.println("done");

        // No hold から Using hold へ確率を計算し直す
        // 結果をリストに追加する
        ArrayList<Pair<List<Block>, Boolean>> pairs = new ArrayList<>();
        ConcurrentVisitedTree tree = obj.visitedTree;
        for (List<Block> target : searchingPieces) {
            int succeed = tree.isSucceed(target);
            assert succeed != ConcurrentVisitedTree.NO_RESULT;
            pairs.add(new Pair<>(target, succeed == ConcurrentVisitedTree.SUCCEED));
        }

        return pairs;
    }
}

