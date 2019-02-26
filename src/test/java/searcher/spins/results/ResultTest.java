package searcher.spins.results;

import common.datastore.FullOperationWithKey;
import common.datastore.PieceCounter;
import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTest {
    @Test
    void create() {
        int maxHeight = 4;
        Field initField = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "____XXXXXX" +
                        "____XXXXXX"
                , maxHeight);

        PieceCounter reminderPieceCounter = new PieceCounter(Piece.valueList());
        Result emptyResult = new EmptyResult(initField, reminderPieceCounter, maxHeight);

        // Empty
        assertThat(emptyResult)
                .returns(initField, Result::freezeInitField)
                .returns(emptyResult.freezeInitField(), Result::getInitField)
                .returns(FieldFactory.createField(maxHeight), Result::freezeUsingField)
                .returns(emptyResult.freezeUsingField(), Result::getUsingField)
                .returns(initField, Result::freezeAllMergedField)
                .returns(emptyResult.freezeAllMergedField(), Result::getAllMergedField)
                .returns(new PieceCounter(Piece.valueList()), Result::getRemainderPieceCounter)
                .returns(0, Result::getNumOfUsingPiece)
                .returns(Collections.emptyList(), (it) -> it.operationStream().collect(Collectors.toList()))
        ;

        // Add result
        AddLastResult result1 = AddLastResult.create(emptyResult, toOperation(Piece.L, Rotate.Reverse, 1, 1, maxHeight));
        AddLastResult result2 = AddLastResult.create(result1, toOperation(Piece.I, Rotate.Left, 3, 1, maxHeight));

        assertThat(result2)
                .returns(initField, Result::freezeInitField)
                .returns(result2.freezeInitField(), Result::getInitField)
                .returns(FieldFactory.createField("" +
                        "___X______" +
                        "___X______" +
                        "XXXX______" +
                        "X__X______"
                ), Result::freezeUsingField)
                .returns(result2.freezeUsingField(), Result::getUsingField)
                .returns(FieldFactory.createField("" +
                        "___X______" +
                        "___X______" +
                        "XXXXXXXXXX" +
                        "X__XXXXXXX"
                ), Result::freezeAllMergedField)
                .returns(result2.freezeAllMergedField(), Result::getAllMergedField)
                .returns(new PieceCounter(Arrays.asList(Piece.T, Piece.S, Piece.Z, Piece.O, Piece.J)), Result::getRemainderPieceCounter)
                .returns(2, Result::getNumOfUsingPiece)
                .returns(Arrays.asList(result1.getCurrentOperation(), result2.getCurrentOperation()), (it) -> it.operationStream().collect(Collectors.toList()))
        ;

        // ライン消去後におく
        AddLastResult result3 = AddLastResult.create(result2, toOperation(Piece.S, Rotate.Right, 0, 1, KeyOperators.getBitKey(1), maxHeight));

        assertThat(result3)
                .returns(initField, Result::freezeInitField)
                .returns(result3.freezeInitField(), Result::getInitField)
                .returns(FieldFactory.createField("" +
                        "X__X______" +
                        "XX_X______" +
                        "XXXX______" +
                        "XX_X______"
                ), Result::freezeUsingField)
                .returns(result3.freezeUsingField(), Result::getUsingField)
                .returns(FieldFactory.createField("" +
                        "X__X______" +
                        "XX_X______" +
                        "XXXXXXXXXX" +
                        "XX_XXXXXXX"
                ), Result::freezeAllMergedField)
                .returns(result3.freezeAllMergedField(), Result::getAllMergedField)
                .returns(new PieceCounter(Arrays.asList(Piece.T, Piece.Z, Piece.O, Piece.J)), Result::getRemainderPieceCounter)
                .returns(3, Result::getNumOfUsingPiece)
                .returns(Arrays.asList(
                        result1.getCurrentOperation(), result2.getCurrentOperation(), result3.getCurrentOperation()
                ), (it) -> it.operationStream().collect(Collectors.toList()))
        ;
    }

    private SimpleOriginalPiece toOperation(Piece piece, Rotate rotate, int x, int y, int maxHeight) {
        return toOperation(piece, rotate, x, y, 0L, maxHeight);
    }

    private SimpleOriginalPiece toOperation(Piece piece, Rotate rotate, int x, int y, long needDeletedKey, int maxHeight) {
        FullOperationWithKey operation = FullOperationWithKey.create(new Mino(piece, rotate), x, y, needDeletedKey, maxHeight);
        return new SimpleOriginalPiece(operation, maxHeight);
    }
}