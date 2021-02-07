package core.mino;

import common.datastore.Operation;
import common.datastore.SimpleMinoOperation;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinoTransformTest {
    private final MinoTransform transform = new MinoTransform();
    private final MinoFactory minoFactory = new MinoFactory();

    void assertOperation(Operation operation, Operation expected) {
        Operation mirror = transform.mirror(minoFactory, operation.getPiece(), operation.getRotate(), operation.getX(), operation.getY());
        assertThat(mirror).isEqualTo(expected);

        Operation mirror2 = transform.mirror(minoFactory, mirror.getPiece(), mirror.getRotate(), mirror.getX(), mirror.getY());
        assertThat(mirror2).isEqualTo(operation);
    }

    private SimpleMinoOperation createSimpleOperation(
            Piece piece, Rotate rotate, int x, int y
    ) {
        Mino mino = minoFactory.create(piece, rotate);
        return new SimpleMinoOperation(mino, x, y);
    }

    @Test
    void mirrorI() {
        Piece piece = Piece.I;
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Spawn, 7, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Reverse, 2, 0);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Reverse, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Right, 0, 2);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Left, 0, 1);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Right, 9, 2);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorT() {
        Piece piece = Piece.T;
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Spawn, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Spawn, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Reverse, 1, 0);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Reverse, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Left, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorO() {
        Piece piece = Piece.O;
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Spawn, 0, 0);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Reverse, 9, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Left, 1, 0);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleMinoOperation expected = createSimpleOperation(piece, Rotate.Left, 9, 0);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorS() {
        Piece piece = Piece.S;
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleMinoOperation expected = createSimpleOperation(Piece.Z, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.Z, Rotate.Reverse, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Left, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.Z, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.Z, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorZ() {
        Piece piece = Piece.Z;
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleMinoOperation expected = createSimpleOperation(Piece.S, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.S, Rotate.Reverse, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Left, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.S, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.S, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorL() {
        Piece piece = Piece.L;
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleMinoOperation expected = createSimpleOperation(Piece.J, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.J, Rotate.Reverse, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Left, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.J, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.J, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorJ() {
        Piece piece = Piece.J;
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleMinoOperation expected = createSimpleOperation(Piece.L, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.L, Rotate.Reverse, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Left, 1, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.L, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleMinoOperation operation = createSimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleMinoOperation expected = createSimpleOperation(Piece.L, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
    }
}