package core.mino;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinoTransformTest {
    private final MinoTransform transform = new MinoTransform();

    void assertOperation(Operation operation, Operation expected) {
        Operation mirror = transform.mirror(operation.getPiece(), operation.getRotate(), operation.getX(), operation.getY());
        assertThat(mirror).isEqualTo(expected);

        Operation mirror2 = transform.mirror(mirror.getPiece(), mirror.getRotate(), mirror.getX(), mirror.getY());
        assertThat(mirror2).isEqualTo(operation);
    }

    @Test
    void mirrorI() {
        Piece piece = Piece.I;
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Reverse, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Reverse, 2, 0);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Spawn, 7, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Right, 0, 2);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Left, 0, 1);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Right, 9, 2);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorT() {
        Piece piece = Piece.T;
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Spawn, 1, 1);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Spawn, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Reverse, 1, 0);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Reverse, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Left, 1, 1);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorO() {
        Piece piece = Piece.O;
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Spawn, 0, 0);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Reverse, 9, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Left, 1,0);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Left, 9, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleOperation expected = new SimpleOperation(piece, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorS() {
        Piece piece = Piece.S;
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleOperation expected = new SimpleOperation(Piece.Z, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleOperation expected = new SimpleOperation(Piece.Z, Rotate.Reverse, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Left, 1,1);
            SimpleOperation expected = new SimpleOperation(Piece.Z, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleOperation expected = new SimpleOperation(Piece.Z, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorZ() {
        Piece piece = Piece.Z;
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleOperation expected = new SimpleOperation(Piece.S, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleOperation expected = new SimpleOperation(Piece.S, Rotate.Reverse, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Left, 1,1);
            SimpleOperation expected = new SimpleOperation(Piece.S, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleOperation expected = new SimpleOperation(Piece.S, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorL() {
        Piece piece = Piece.L;
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleOperation expected = new SimpleOperation(Piece.J, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleOperation expected = new SimpleOperation(Piece.J, Rotate.Reverse, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Left, 1,1);
            SimpleOperation expected = new SimpleOperation(Piece.J, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleOperation expected = new SimpleOperation(Piece.J, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
    }

    @Test
    void mirrorJ() {
        Piece piece = Piece.J;
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Spawn, 1, 0);
            SimpleOperation expected = new SimpleOperation(Piece.L, Rotate.Spawn, 8, 0);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Reverse, 1, 1);
            SimpleOperation expected = new SimpleOperation(Piece.L, Rotate.Reverse, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Left, 1,1);
            SimpleOperation expected = new SimpleOperation(Piece.L, Rotate.Right, 8, 1);
            assertOperation(operation, expected);
        }
        {
            SimpleOperation operation = new SimpleOperation(piece, Rotate.Right, 0, 1);
            SimpleOperation expected = new SimpleOperation(Piece.L, Rotate.Left, 9, 1);
            assertOperation(operation, expected);
        }
    }
}