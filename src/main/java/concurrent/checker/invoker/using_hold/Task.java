package concurrent.checker.invoker.using_hold;

import common.ResultHelper;
import common.datastore.Operation;
import common.datastore.Pair;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.blocks.Pieces;
import common.order.OrderLookup;
import common.tree.VisitedTree;
import concurrent.checker.invoker.CheckerCommonObj;
import core.action.candidate.Candidate;
import core.mino.Piece;
import searcher.checker.Checker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

class Task implements Callable<Pair<Pieces, Boolean>> {
    private final Obj obj;
    private final CheckerCommonObj commonObj;
    private final Pieces target;

    Task(Obj obj, CheckerCommonObj commonObj, Pieces target) {
        this.obj = obj;
        this.commonObj = commonObj;
        this.target = target;
    }

    @Override
    public Pair<Pieces, Boolean> call() {
        List<Piece> pieceList = target.getPieces();

        // すでに探索済みならそのまま結果を追加
        int succeed = obj.visitedTree.isSucceed(pieceList);
        if (succeed != VisitedTree.NO_RESULT)
            return new Pair<>(target, succeed == VisitedTree.SUCCEED);

        // すでに条件を満たしている場合は成功として扱う
        if (obj.field.isEmpty() && obj.maxClearLine == 0) {
            boolean result = true;
            obj.visitedTree.set(result, pieceList);
            return new Pair<>(target, result);
        }
        assert 0 < obj.maxClearLine;

        // 探索準備
        Checker<Action> checker = commonObj.checkerThreadLocal.get();
        Candidate<Action> candidate = commonObj.candidateThreadLocal.get();

        // 探索
        boolean checkResult = checker.check(obj.field, pieceList, candidate, obj.maxClearLine, obj.maxDepth);
        obj.visitedTree.set(checkResult, pieceList);

        // もし探索に成功した場合
        // パフェが見つかったツモ順(≠探索時のツモ順)へと、ホールドを使ってできるパターンを逆算
        if (checkResult) {
            Result result = checker.getResult();
            List<Piece> resultPieceList = ResultHelper.createOperationStream(result)
                    .map(Operation::getPiece)
                    .collect(Collectors.toList());

            int reverseMaxDepth = result.getLastHold() != null ? resultPieceList.size() + 1 : resultPieceList.size();
            OrderLookup.reverseBlocks(resultPieceList, reverseMaxDepth)
                    .forEach(piece -> obj.visitedTree.set(true, piece.toList()));
        }

        return new Pair<>(target, checkResult);
    }
}
