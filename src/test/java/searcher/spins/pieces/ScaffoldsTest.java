package searcher.spins.pieces;

import common.parser.OperationTransform;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.KeyOperators;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ScaffoldsTest {
    @Test
    void scaffolds1() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        int fieldHeight = 7;
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        Scaffolds scaffolds = Scaffolds.create(factory.createMinimalPieces(FieldFactory.createField(fieldHeight)));

        SimpleOriginalPiece operation = new SimpleOriginalPiece(OperationTransform.toFullOperationWithKey(
                minoFactory.create(Piece.O, Rotate.Spawn), 3, 3, KeyOperators.getBitKey(4)
        ), fieldHeight);
        List<SimpleOriginalPiece> results = scaffolds.get(operation).collect(Collectors.toList());

        isValidScaffold(results, operation, fieldHeight);
    }

    @Test
    void scaffolds2() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        int fieldHeight = 7;
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        Scaffolds scaffolds = Scaffolds.create(factory.createMinimalPieces(FieldFactory.createField(fieldHeight)));

        SimpleOriginalPiece operation = new SimpleOriginalPiece(OperationTransform.toFullOperationWithKey(
                minoFactory.create(Piece.I, Rotate.Spawn), 4, 2, 0L
        ), fieldHeight);
        List<SimpleOriginalPiece> results = scaffolds.get(operation).collect(Collectors.toList());

        isValidScaffold(results, operation, fieldHeight);
    }

    @Test
    void scaffolds3() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        int fieldHeight = 7;
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        Scaffolds scaffolds = Scaffolds.create(factory.createMinimalPieces(FieldFactory.createField(fieldHeight)));

        SimpleOriginalPiece operation = new SimpleOriginalPiece(OperationTransform.toFullOperationWithKey(
                minoFactory.create(Piece.S, Rotate.Spawn), 2, 0, 0L
        ), fieldHeight);
        List<SimpleOriginalPiece> results = scaffolds.get(operation).collect(Collectors.toList());

        isValidScaffold(results, operation, fieldHeight);
    }

    private void isValidScaffold(List<SimpleOriginalPiece> scaffolds, SimpleOriginalPiece operation, int fieldHeight) {
        Field field = FieldFactory.createField(fieldHeight);

        Set<SimpleOriginalPiece> expected;
        if (field.isOnGround(operation.getMino(), operation.getX(), operation.getY())) {
            expected = Collections.emptySet();
        } else {
            List<SimpleOriginalPiece> originalPieces = getAllSimpleOriginalPieces(fieldHeight);
            expected = originalPieces.stream()
                    .filter(scaffold -> isValidScaffold(operation, scaffold))
                    .collect(Collectors.toSet());
        }

        HashSet<SimpleOriginalPiece> actual = new HashSet<>(scaffolds);

        try {
            assertThat(actual).isEqualTo(expected);
        } catch (Error e) {
            {
                System.out.println("over");

                HashSet<SimpleOriginalPiece> copy = new HashSet<>(actual);
                copy.removeAll(expected);

                for (SimpleOriginalPiece originalPiece : copy) {
                    Field minoField = originalPiece.getMinoField().freeze();
                    minoField.merge(operation.getMinoField());
                    System.out.println(FieldView.toString(minoField));
                    System.out.println();
                }
            }

            {
                System.out.println("less");

                HashSet<SimpleOriginalPiece> copy = new HashSet<>(expected);
                copy.removeAll(actual);

                for (SimpleOriginalPiece originalPiece : copy) {
                    Field minoField = originalPiece.getMinoField().freeze();
                    minoField.merge(operation.getMinoField());
                    System.out.println(FieldView.toString(minoField));
                    System.out.println();
                }
            }

            throw e;
        }
    }

    private List<SimpleOriginalPiece> getAllSimpleOriginalPieces(int fieldHeight) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        return factory.createAllPieces().getOriginalPieces();
    }

    private boolean isValidScaffold(SimpleOriginalPiece operation, SimpleOriginalPiece scaffold) {
        // 置きたいミノ
        Field operationField = operation.getMinoField().freeze();

        // 足場ミノの needDeletedKey が削除されても、置くミノに影響を与えない
        operationField.deleteLineWithKey(scaffold.getNeedDeletedKey());
        if (operationField.getNumOfAllBlocks() != 4) {
            return false;
        }

        // 足場ミノ
        Field scaffoldField = scaffold.getMinoField().freeze();

        // 置きたいミノの needDeletedKey が削除されている
        scaffoldField.deleteLineWithKey(operation.getNeedDeletedKey());

        // 置きたいミノを置くスペースがある
        if (!scaffoldField.canPut(operation.getMino(), operation.getX(), operation.getY())) {
            return false;
        }

        // 置きたいミノが地面の上にある
        return scaffoldField.isOnGround(operation.getMino(), operation.getX(), operation.getY());
    }
}