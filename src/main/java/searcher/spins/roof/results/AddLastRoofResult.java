package searcher.spins.roof.results;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.AddLastResult;
import searcher.spins.results.Result;

import java.util.stream.Stream;

public class AddLastRoofResult extends RoofResult {
    private final RoofResult prevRoofResult;
    private final Result result;
    private final SimpleOriginalPiece originalPiece;
    private final Field notAllowedWithT;
    private final Field allMergedFieldWithoutT;
    private final long onePieceFilledKeyWithoutT;
    private final int numOfRoofPieces;

    public AddLastRoofResult(RoofResult prevRoofResult, SimpleOriginalPiece originalPiece) {
        this.prevRoofResult = prevRoofResult;
        this.result = AddLastResult.create(prevRoofResult.getLastResult(), originalPiece);
        this.originalPiece = originalPiece;

        Field freezeNotAllowedWithT = prevRoofResult.getNotAllowedWithT().freeze();
        freezeNotAllowedWithT.merge(originalPiece.getMinoField());
        this.notAllowedWithT = freezeNotAllowedWithT;

        Field freezeAllMergedFieldWithoutT = prevRoofResult.getAllMergedFieldWithoutT().freeze();
        freezeAllMergedFieldWithoutT.merge(originalPiece.getMinoField());
        this.allMergedFieldWithoutT = freezeAllMergedFieldWithoutT;

        this.onePieceFilledKeyWithoutT = result.getOnePieceFilledKey() & ~getOperationT().getUsingKey();
        this.numOfRoofPieces = result.getNumOfUsingPiece() + 1;
    }

    @Override
    public Result getLastResult() {
        return result;
    }

    @Override
    public int getNumOfUsingPiece() {
        return this.getLastResult().getNumOfUsingPiece();
    }

    @Override
    public Stream<SimpleOriginalPiece> targetOperationStream() {
        return Stream.concat(prevRoofResult.targetOperationStream(), Stream.of(originalPiece));
    }

    @Override
    public SimpleOriginalPiece getOperationT() {
        return prevRoofResult.getOperationT();
    }

    @Override
    public Field getAllMergedFieldWithoutT() {
        return allMergedFieldWithoutT;
    }

    @Override
    public Field getNotAllowedWithT() {
        return notAllowedWithT;
    }

    @Override
    public Stream<Long> toKeyStream() {
        return Stream.concat(prevRoofResult.toKeyStream(), Stream.of(originalPiece.toUniqueKey()));
    }

    @Override
    public long getOnePieceFilledKeyWithoutT() {
        return onePieceFilledKeyWithoutT;
    }

    @Override
    public int getNumOfRoofPieces() {
        return numOfRoofPieces;
    }
}
