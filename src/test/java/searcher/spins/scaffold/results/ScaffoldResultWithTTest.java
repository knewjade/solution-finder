package searcher.spins.scaffold.results;

import common.datastore.PieceCounter;
import common.parser.OperationTransform;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;
import searcher.spins.results.AddLastsResult;
import searcher.spins.results.EmptyResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScaffoldResultWithTTest {
    @Test
    void extractAirOperations1() {
        int fieldHeight = 6;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "XXXXXXX___" +
                        "XXXXXXXX__"
                , fieldHeight);
        SimpleOriginalPiece operationT = to(Piece.T, Rotate.Reverse, 6, 3, fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.Z, Rotate.Spawn, 8, 0, fieldHeight),
                to(Piece.J, Rotate.Reverse, 8, 2, fieldHeight),
                to(Piece.O, Rotate.Spawn, 8, 3, fieldHeight),
                to(Piece.L, Rotate.Reverse, 8, 5, fieldHeight),
                operationT
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithT.extractAirOperations(result, operationT, operations.stream());
        assertThat(pieces).hasSize(0);
    }

    private SimpleOriginalPiece to(Piece piece, Rotate rotate, int x, int y, int fieldHeight) {
        return to(piece, rotate, x, y, 0L, fieldHeight);
    }

    private SimpleOriginalPiece to(Piece piece, Rotate rotate, int x, int y, long deletedKey, int fieldHeight) {
        return new SimpleOriginalPiece(
                OperationTransform.toFullOperationWithKey(new Mino(piece, rotate), x, y, deletedKey), fieldHeight
        );
    }
}