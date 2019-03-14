package searcher.spins;

import common.parser.OperationTransform;
import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpinCommonsTest {
    @Test
    void existsOnGround1() {
        int fieldHeight = 4;
        Field field = FieldFactory.createField("" +
                        "X_________"
                , fieldHeight);

        assertThat(
                SpinCommons.existsOnGround(field, field, 0L, 0L, to(Piece.O, Rotate.Spawn, 3, 0, fieldHeight))
        ).isTrue();

        assertThat(
                SpinCommons.existsOnGround(field, field, 0L, 0L, to(Piece.O, Rotate.Spawn, 0, 1, fieldHeight))
        ).isTrue();

        assertThat(
                SpinCommons.existsOnGround(field, field, 0L, 0L, to(Piece.O, Rotate.Spawn, 3, 1, fieldHeight))
        ).isFalse();
    }

    @Test
    void existsOnGround2() {
        // 揃っているラインがすぐに消えないケース
        int fieldHeight = 6;
        Field field = FieldFactory.createField("" +
                        "_____XXXXX" +
                        "XXXXXXXXXX" +
                        "_____XXXXX" +
                        "X____XXXXX" +
                        "XX___XXXXX"
                , fieldHeight);

        long filledLine = KeyOperators.getBitKey(3);

        assertThat(
                SpinCommons.existsOnGround(field, field, filledLine, 0L, to(Piece.O, Rotate.Spawn, 2, 0, 0L, fieldHeight))
        ).isTrue();

        assertThat(
                SpinCommons.existsOnGround(field, field, filledLine, 0L, to(Piece.O, Rotate.Spawn, 2, 1, 0L, fieldHeight))
        ).isFalse();

        assertThat(
                SpinCommons.existsOnGround(field, field, filledLine, 0L, to(Piece.O, Rotate.Spawn, 1, 1, 0L, fieldHeight))
        ).isTrue();

        assertThat(
                SpinCommons.existsOnGround(field, field, filledLine, 0L, to(Piece.O, Rotate.Spawn, 1, 2, filledLine, fieldHeight))
        ).isFalse();

        assertThat(
                SpinCommons.existsOnGround(field, field, filledLine, 0L, to(Piece.O, Rotate.Spawn, 0, 2, filledLine, fieldHeight))
        ).isTrue();

        assertThat(
                SpinCommons.existsOnGround(field, field, filledLine, 0L, to(Piece.O, Rotate.Spawn, 0, 4, 0L, fieldHeight))
        ).isTrue();
    }

    @Test
    void existsOnGround3() {
        // 揃っているラインがすぐに消えるケース
        int fieldHeight = 6;
        Field initField = FieldFactory.createField("" +
                        "_____XXXXX" +
                        "____XXXXXX" +
                        "_____XXXXX" +
                        "X____XXXXX" +
                        "XX___XXXXX"
                , fieldHeight);
        Field field = FieldFactory.createField("" +
                        "_____XXXXX" +
                        "XXXXXXXXXX" +
                        "_____XXXXX" +
                        "X____XXXXX" +
                        "XX___XXXXX"
                , fieldHeight);

        long filledLine = KeyOperators.getBitKey(3);

        assertThat(
                SpinCommons.existsOnGround(initField, field, filledLine, filledLine, to(Piece.O, Rotate.Spawn, 2, 0, 0L, fieldHeight))
        ).isTrue();

        assertThat(
                SpinCommons.existsOnGround(initField, field, filledLine, filledLine, to(Piece.O, Rotate.Spawn, 2, 1, 0L, fieldHeight))
        ).isFalse();

        assertThat(
                SpinCommons.existsOnGround(initField, field, filledLine, filledLine, to(Piece.O, Rotate.Spawn, 1, 1, 0L, fieldHeight))
        ).isTrue();

        assertThat(
                SpinCommons.existsOnGround(initField, field, filledLine, filledLine, to(Piece.O, Rotate.Spawn, 1, 2, filledLine, fieldHeight))
        ).isFalse();

        assertThat(
                SpinCommons.existsOnGround(initField, field, filledLine, filledLine, to(Piece.O, Rotate.Spawn, 0, 2, filledLine, fieldHeight))
        ).isTrue();

        assertThat(
                SpinCommons.existsOnGround(initField, field, filledLine, filledLine, to(Piece.O, Rotate.Spawn, 0, 4, 0L, fieldHeight))
        ).isFalse();
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