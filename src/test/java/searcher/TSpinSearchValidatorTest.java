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
    @Test
    void check1() {
        int maxHeight = 6;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();

        {
            int maxPieceNum = 4;
            TSpinSearchValidator validator = new TSpinSearchValidator(minoFactory, minoShifter, minoRotation, maxHeight, maxPieceNum);

            Field field = FieldFactory.createField(
                    "__________"
            );

            NormalOrder initOrder = new NormalOrder(field, null, maxHeight, maxPieceNum);

            Mino mino = minoFactory.create(Piece.I, Rotate.Spawn);
            FullOperationWithKey operation = OperationTransform.toFullOperationWithKey(mino, 4, 1, 0L);

            // 残り3ミノ+Tミノ でTスピンできる可能性がある
            {
                ValidationParameter parameter = toValidationParameter(initOrder, operation, false);
                assertThat(validator.check(parameter)).isEqualTo(ValidationResultState.Valid);
            }

            {
                ValidationParameter parameter = toValidationParameter(initOrder, operation, true);
                assertThat(validator.check(parameter)).isEqualTo(ValidationResultState.Valid);
            }
        }

        {
            int maxPieceNum = 1;
            TSpinSearchValidator validator = new TSpinSearchValidator(minoFactory, minoShifter, minoRotation, maxHeight, maxPieceNum);

            Field field = FieldFactory.createField(
                    "__________"
            );

            NormalOrder initOrder = new NormalOrder(field, null, maxHeight, maxPieceNum);

            Mino mino = minoFactory.create(Piece.I, Rotate.Spawn);
            FullOperationWithKey operation = OperationTransform.toFullOperationWithKey(mino, 4, 1, 0L);

            // Tスピンできない
            {
                ValidationParameter parameter = toValidationParameter(initOrder, operation, false);
                assertThat(validator.check(parameter)).isEqualTo(ValidationResultState.Prune);
            }

            {
                ValidationParameter parameter = toValidationParameter(initOrder, operation, true);
                assertThat(validator.check(parameter)).isEqualTo(ValidationResultState.Prune);
            }
        }
    }

    private ValidationParameter toValidationParameter(NormalOrder initOrder, FullOperationWithKey operation, boolean isLast) {
        Field field = initOrder.getField();
        Field freeze = putMino(field, operation);

        int clearedLine = freeze.clearLine();
        int nextMaxCleardLine = initOrder.getMaxClearLine() - clearedLine;

        return new ValidationParameter(
                initOrder,
                field, operation.getMino(), operation.getX(), operation.getY(), operation.getNeedDeletedKey(),
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