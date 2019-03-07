package searcher.spins.wall.results;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;
import searcher.spins.scaffold.results.ScaffoldResultWithT;

import java.util.stream.Stream;

public class AddLastWallResult extends WallResult {
    private final WallResult prevWallResult;
    private final ScaffoldResultWithT scaffoldResult;
    private final Field remain;

    public AddLastWallResult(WallResult prevWallResult, ScaffoldResultWithT scaffoldResult) {
        super();
        this.prevWallResult = prevWallResult;
        this.scaffoldResult = scaffoldResult;

        Result result = scaffoldResult.getLastResult();

        // 残りのミノから、すでに使われているブロックを除く
        Field remain = prevWallResult.getRemain().freeze();
        remain.reduce(result.getAllMergedField());
        this.remain = remain;
    }

    @Override
    public Result getLastResult() {
        return scaffoldResult.getLastResult();
    }

    @Override
    public boolean isVisitedAll() {
        return remain.isPerfect();
    }

    @Override
    public Stream<Long> toKeyStream() {
        Stream<SimpleOriginalPiece> targetOperationStream = scaffoldResult.targetOperationStream();
        return Stream.concat(prevWallResult.toKeyStream(), targetOperationStream.map(it -> it.toUniqueKey()));
    }

    @Override
    public Field getRemain() {
        return remain;
    }

    @Override
    public Field getNotAllowed() {
        return prevWallResult.getNotAllowed();
    }
}
