package searcher.spins.fill.line;

import common.datastore.SimpleOperation;
import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleOriginalPiecesTest {
    @Test
    void create() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        SimpleOriginalPieces simpleOriginalPieces = SimpleOriginalPieces.create(minoFactory, minoShifter, 7);

        // keyToPiece

        {
            SimpleOperation operation = new SimpleOperation(Piece.T, Rotate.Spawn, 4, 2);
            SimpleOriginalPiece originalPiece = simpleOriginalPieces.get(
                    operation.getPiece(), operation.getRotate(), operation.getX(), operation.getY()
            );
            assertThat(originalPiece)
                    .returns(operation.getPiece(), SimpleOriginalPiece::getPiece)
                    .returns(operation.getRotate(), SimpleOriginalPiece::getRotate)
                    .returns(operation.getX(), SimpleOriginalPiece::getX)
                    .returns(operation.getY(), SimpleOriginalPiece::getY)
                    .returns(0L, SimpleOriginalPiece::getNeedDeletedKey)
            ;
        }

        // fieldToPiece

        {
            Field field = FieldFactory.createField("" +
                    "____X_____" +
                    "__________" +
                    "____X_____" +
                    "____XX____" +
                    "__________"
            );
            SimpleOriginalPiece originalPiece = simpleOriginalPieces.get(field);
            assertThat(originalPiece)
                    .returns(Piece.L, SimpleOriginalPiece::getPiece)
                    .returns(Rotate.Right, SimpleOriginalPiece::getRotate)
                    .returns(4, SimpleOriginalPiece::getX)
                    .returns(2, SimpleOriginalPiece::getY)
                    .returns(KeyOperators.getBitKey(3), SimpleOriginalPiece::getNeedDeletedKey)
            ;
        }
    }
}