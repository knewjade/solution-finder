package concurrent.checker.invoker.no_hold;

import common.ResultHelper;
import common.buildup.BuildUpStream;
import common.datastore.*;
import common.datastore.action.Action;
import common.datastore.blocks.Pieces;
import common.parser.OperationTransform;
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
    public Pair<Pieces, Boolean> call() throws Exception {
        List<Piece> pieceList = target.getPieces();

        // すでに探索済みならそのまま結果を追加
        int succeed = obj.visitedTree.isSucceed(pieceList);
        if (succeed != VisitedTree.NO_RESULT)
            return new Pair<>(target, succeed == VisitedTree.SUCCEED);

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

            Operations operations = new Operations(ResultHelper.createOperationStream(result));
            List<MinoOperationWithKey> operationWithKeys = OperationTransform.parseToOperationWithKeys(obj.field, operations, commonObj.minoFactory, obj.maxClearLine);

            BuildUpStream buildUpStream = new BuildUpStream(commonObj.reachableThreadLocal.get(), obj.maxClearLine);
            buildUpStream.existsValidBuildPattern(obj.field, operationWithKeys)
                    .forEach(minoOperationWithKeys -> {
                        List<Piece> pieces = minoOperationWithKeys.stream().map(OperationWithKey::getPiece).collect(Collectors.toList());
                        obj.visitedTree.set(true, pieces);
                    });
        }

        return new Pair<>(target, checkResult);
    }
}
