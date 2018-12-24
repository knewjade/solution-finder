package searcher;

import common.datastore.FullOperationWithKey;
import common.datastore.order.NormalOrder;
import common.parser.OperationTransform;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;
import searcher.core.ValidationParameter;
import searcher.core.ValidationResultState;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TSpinSearchValidatorTest {
    private final MinoFactory minoFactory = new MinoFactory();

    @Test
    void check1() {
        // 残り3ミノ+Tミノ でTスピンできる可能性がある
        int maxHeight = 6;
        int maxPieceNum = 4;
        TSpinSearchValidator validator = getTSpinSearchValidator(maxHeight, maxPieceNum);

        Field field = FieldFactory.createField(
                "__________"
        );

        NormalOrder initOrder = new NormalOrder(field, null, maxHeight, maxPieceNum);

        Mino mino = minoFactory.create(Piece.I, Rotate.Spawn);
        FullOperationWithKey operation = OperationTransform.toFullOperationWithKey(mino, 4, 0, 0L);

        ValidationParameter parameter = toValidationParameter(initOrder, operation, false);
        assertThat(validator.check(parameter)).isEqualTo(ValidationResultState.Valid);
    }

    @Test
    void check2() {
        // 最後にTスピンできる可能性がある
        int maxHeight = 6;
        int maxPieceNum = 1;
        TSpinSearchValidator validator = getTSpinSearchValidator(maxHeight, maxPieceNum);

        Field field = FieldFactory.createField("" +
                "X_____X___" +
                "X_____XXXX" +
                "XX_XXXXXXX"
        );

        NormalOrder initOrder = new NormalOrder(field, null, maxHeight, maxPieceNum);

        Mino mino = minoFactory.create(Piece.Z, Rotate.Spawn);
        FullOperationWithKey operation = OperationTransform.toFullOperationWithKey(mino, 4, 1, 0L);

        ValidationParameter parameter = toValidationParameter(initOrder, operation, false);
        assertThat(validator.check(parameter)).isEqualTo(ValidationResultState.Result);
    }

    @Test
    void check3() {
        // 最後までTスピンできなかった
        int maxHeight = 6;
        int maxPieceNum = 1;
        TSpinSearchValidator validator = getTSpinSearchValidator(maxHeight, maxPieceNum);

        Field field = FieldFactory.createField("" +
                "______X___" +
                "______XXXX" +
                "___XXXXXXX"
        );

        NormalOrder initOrder = new NormalOrder(field, null, maxHeight, maxPieceNum);

        Mino mino = minoFactory.create(Piece.Z, Rotate.Spawn);
        FullOperationWithKey operation = OperationTransform.toFullOperationWithKey(mino, 4, 1, 0L);

        ValidationParameter parameter = toValidationParameter(initOrder, operation, false);
        assertThat(validator.check(parameter)).isEqualTo(ValidationResultState.Prune);
    }

    @Test
    void check4() {
        // 残りのミノTスピンできないことが確定する
        int maxHeight = 6;
        int maxPieceNum = 2;
        TSpinSearchValidator validator = getTSpinSearchValidator(maxHeight, maxPieceNum);

        Field field = FieldFactory.createField("" +
                "XXXXX_XXXX" +
                "_XXXXXXXXX"
        );

        NormalOrder initOrder = new NormalOrder(field, null, maxHeight, maxPieceNum);

        Mino mino = minoFactory.create(Piece.J, Rotate.Left);
        FullOperationWithKey operation = OperationTransform.toFullOperationWithKey(mino, 6, 3, 0L);

        ValidationParameter parameter = toValidationParameter(initOrder, operation, false);
        assertThat(validator.check(parameter)).isEqualTo(ValidationResultState.Prune);
    }

    private TSpinSearchValidator getTSpinSearchValidator(int maxHeight, int maxPieceNum) {
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        return new TSpinSearchValidator(minoFactory, minoShifter, minoRotation, maxHeight, maxPieceNum);
    }

    private ValidationParameter toValidationParameter(NormalOrder initOrder, FullOperationWithKey operation, boolean isLast) {
        Field field = initOrder.getField();
        Field freeze = putMino(field, operation);

        int clearedLine = freeze.clearLine();
        int nextMaxCleardLine = initOrder.getMaxClearLine() - clearedLine;

        return new ValidationParameter(
                initOrder,
                freeze, operation.getMino(), operation.getX(), operation.getY(), operation.getNeedDeletedKey(),
                nextMaxCleardLine, isLast
        );
    }

    private Field putMino(Field field, FullOperationWithKey operation) {
        int maxHeight = field.getMaxFieldHeight();
        Field operationField = FieldFactory.createField(maxHeight);
        operationField.put(operation.getMino(), operation.getX(), operation.getY());
        operationField.insertWhiteLineWithKey(operation.getNeedDeletedKey());

        Field freeze = field.freeze();
        freeze.merge(operationField);
        return freeze;
    }
}