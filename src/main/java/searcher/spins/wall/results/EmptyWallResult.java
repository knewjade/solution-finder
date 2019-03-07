package searcher.spins.wall.results;

import core.field.Field;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;
import searcher.spins.wall.MaskField;

import java.util.stream.Stream;

public class EmptyWallResult extends WallResult {
    private final Candidate candidate;
    private final MaskField maskField;
    private final Field remain;

    public EmptyWallResult(Candidate candidate, MaskField maskField) {
        super();
        this.candidate = candidate;
        this.maskField = maskField;

        Result result = candidate.getResult();

        // 残りのミノから、すでに使われているブロックを除く
        Field remain = maskField.getRemain().freeze();
        remain.reduce(result.getAllMergedField());
        this.remain = remain;
    }

    @Override
    public Result getLastResult() {
        return candidate.getResult();
    }

    @Override
    public boolean isVisitedAll() {
        return remain.isPerfect();
    }

    @Override
    public Stream<Long> toKeyStream() {
        return Stream.empty();
    }

    @Override
    public Field getRemain() {
        return remain;
    }

    @Override
    public Field getNotAllowed() {
        return maskField.getNotAllowed();
    }
}
