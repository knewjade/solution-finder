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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScaffoldResultWithoutTTest {
    @Test
    void extractAirOperations1() {
        int fieldHeight = 4;
        Field field = FieldFactory.createField("" +
                        "X_________"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Collections.singletonList(
                to(Piece.O, Rotate.Spawn, 0, 1, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(0);
    }

    @Test
    void extractAirOperations2() {
        int fieldHeight = 4;
        Field field = FieldFactory.createField("" +
                        "XXXXXXX___" +
                        "X_________"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.J, Rotate.Reverse, 8, 1, fieldHeight),
                to(Piece.O, Rotate.Spawn, 8, 2, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(0);
    }

    @Test
    void extractAirOperations3() {
        int fieldHeight = 4;
        Field field = FieldFactory.createField("" +
                        "XXXXXXX___" +
                        "X_________"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.J, Rotate.Reverse, 8, 1, fieldHeight),
                to(Piece.O, Rotate.Spawn, 7, 2, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(1);
    }

    @Test
    void extractAirOperations2_2() {
        int fieldHeight = 4;
        Field field = FieldFactory.createField("" +
                        "XXXXXXX___" +
                        "X_________"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.J, Rotate.Reverse, 8, 1, fieldHeight),
                to(Piece.O, Rotate.Spawn, 0, 2, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(0);
    }

    @Test
    void extractAirOperations3_2() {
        int fieldHeight = 4;
        Field field = FieldFactory.createField("" +
                        "XXXXXXX___" +
                        "X_________"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.J, Rotate.Reverse, 8, 1, fieldHeight),
                to(Piece.O, Rotate.Spawn, 1, 2, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(0);
    }

    @Test
    void extractAirOperations4() {
        int fieldHeight = 4;
        Field field = FieldFactory.createField("" +
                        "_XXXXXX___" +
                        "__________"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.I, Rotate.Left, 0, 1, fieldHeight),
                to(Piece.J, Rotate.Reverse, 8, 1, fieldHeight),
                to(Piece.O, Rotate.Spawn, 7, 2, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(0);
    }

    @Test
    void extractAirOperations5() {
        int fieldHeight = 5;
        Field field = FieldFactory.createField("" +
                        "XXXXXXX___" +
                        "XXXXXX____" +
                        "_________X"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.I, Rotate.Spawn, 7, 1, fieldHeight),
                to(Piece.L, Rotate.Spawn, 8, 2, fieldHeight),
                to(Piece.O, Rotate.Spawn, 7, 3, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(1);
    }

    @Test
    void extractAirOperations6() {
        int fieldHeight = 5;
        Field field = FieldFactory.createField("" +
                        "_XXXXXX___" +
                        "___XXX____" +
                        "X________X"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.J, Rotate.Spawn, 1, 1, fieldHeight),
                to(Piece.I, Rotate.Spawn, 7, 1, fieldHeight),
                to(Piece.L, Rotate.Spawn, 8, 2, fieldHeight),
                to(Piece.O, Rotate.Spawn, 7, 3, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(0);
    }

    @Test
    void extractAirOperations7() {
        int fieldHeight = 5;
        Field field = FieldFactory.createField("" +
                        "XXXXXXX___" +
                        "XXXX______" +
                        "_________X"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.O, Rotate.Spawn, 4, 0, fieldHeight),
                to(Piece.I, Rotate.Spawn, 7, 1, fieldHeight),
                to(Piece.L, Rotate.Spawn, 8, 2, fieldHeight),
                to(Piece.O, Rotate.Spawn, 7, 3, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(0);
    }

    @Test
    void extractAirOperations8() {
        int fieldHeight = 5;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "XXXXXXX___" +
                        "XXXXXXXX__"
                , fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.J, Rotate.Reverse, 8, 1, fieldHeight),
                to(Piece.Z, Rotate.Right, 8, 3, fieldHeight)
        );
        EmptyResult emptyResult = new EmptyResult(field, new PieceCounter(operations.stream().map(SimpleOriginalPiece::getPiece)), fieldHeight);
        AddLastsResult result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        List<SimpleOriginalPiece> pieces = ScaffoldResultWithoutT.extractAirOperations(result, operations.stream());
        assertThat(pieces).hasSize(1);
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