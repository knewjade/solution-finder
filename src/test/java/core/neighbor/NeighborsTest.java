package core.neighbor;

import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class NeighborsTest {
    private Neighbors createNeighbors(int maxClearLine) {
        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(maxClearLine + 3);
        return new Neighbors(minoFactory, minoRotation, pieceFactory);
    }

    @Test
    void getT() {
        Neighbors neighbors = createNeighbors(4);

        Neighbor neighbor = neighbors.get(Piece.O, Rotate.Spawn, 1, 0);
        assertThat(neighbor.getPiece())
                .returns(Piece.O, OriginalPiece::getPiece)
                .returns(Rotate.Spawn, OriginalPiece::getRotate)
                .returns(1, OriginalPiece::getX)
                .returns(0, OriginalPiece::getY);
    }

    @Test
    void getJ() {
        Neighbors neighbors = createNeighbors(4);

        Neighbor neighbor = neighbors.get(Piece.J, Rotate.Left, 3, 2);
        assertThat(neighbor.getPiece())
                .returns(Piece.J, OriginalPiece::getPiece)
                .returns(Rotate.Left, OriginalPiece::getRotate)
                .returns(3, OriginalPiece::getX)
                .returns(2, OriginalPiece::getY);
    }

    @Test
    void getS() {
        Neighbors neighbors = createNeighbors(4);

        Neighbor neighbor = neighbors.get(Piece.S, Rotate.Reverse, 8, 3);
        assertThat(neighbor.getPiece())
                .returns(Piece.S, OriginalPiece::getPiece)
                .returns(Rotate.Reverse, OriginalPiece::getRotate)
                .returns(8, OriginalPiece::getX)
                .returns(3, OriginalPiece::getY);
    }

    @Test
    void nextMovesSources() {
        Neighbors neighbors = createNeighbors(4);

        Neighbor neighbor = neighbors.get(Piece.T, Rotate.Spawn, 4, 1);
        assertThat(neighbor.getNextMovesSources())
                .containsExactlyInAnyOrder(
                        neighbors.get(Piece.T, Rotate.Spawn, 3, 1),
                        neighbors.get(Piece.T, Rotate.Spawn, 5, 1),
                        neighbors.get(Piece.T, Rotate.Spawn, 4, 2)
                );
    }

    @Test
    void nextRightRotateDestinations() {
        Neighbors neighbors = createNeighbors(4);

        Neighbor neighbor = neighbors.get(Piece.T, Rotate.Spawn, 4, 3);
        assertThat(neighbor.getNextRightRotateDestinations())
                .containsExactlyInAnyOrder(
                        neighbors.get(Piece.T, Rotate.Right, 4, 3),
                        neighbors.get(Piece.T, Rotate.Right, 3, 3),
                        neighbors.get(Piece.T, Rotate.Right, 3, 4),
                        neighbors.get(Piece.T, Rotate.Right, 4, 1),
                        neighbors.get(Piece.T, Rotate.Right, 3, 1)
                );
    }

    @Test
    void nextRightRotateSources() {
        Neighbors neighbors = createNeighbors(4);

        Neighbor neighbor = neighbors.get(Piece.T, Rotate.Spawn, 4, 3);
        assertThat(neighbor.getNextRightRotateSources())
                .containsExactlyInAnyOrder(
                        neighbors.get(Piece.T, Rotate.Left, 4, 3),
                        neighbors.get(Piece.T, Rotate.Left, 5, 3),
                        neighbors.get(Piece.T, Rotate.Left, 5, 4),
                        neighbors.get(Piece.T, Rotate.Left, 4, 1),
                        neighbors.get(Piece.T, Rotate.Left, 5, 1)
                );
    }

    @Test
    void nextLeftRotateDestinations() {
        Neighbors neighbors = createNeighbors(4);

        Neighbor neighbor = neighbors.get(Piece.T, Rotate.Spawn, 4, 3);
        assertThat(neighbor.getNextLeftRotateDestinations())
                .containsExactlyInAnyOrder(
                        neighbors.get(Piece.T, Rotate.Left, 4, 3),
                        neighbors.get(Piece.T, Rotate.Left, 5, 3),
                        neighbors.get(Piece.T, Rotate.Left, 5, 4),
                        neighbors.get(Piece.T, Rotate.Left, 4, 1),
                        neighbors.get(Piece.T, Rotate.Left, 5, 1)
                );
    }

    @Test
    void nextLeftRotateSources() {
        Neighbors neighbors = createNeighbors(4);

        Neighbor neighbor = neighbors.get(Piece.T, Rotate.Spawn, 4, 3);
        assertThat(neighbor.getNextLeftRotateSources())
                .containsExactlyInAnyOrder(
                        neighbors.get(Piece.T, Rotate.Right, 4, 3),
                        neighbors.get(Piece.T, Rotate.Right, 3, 3),
                        neighbors.get(Piece.T, Rotate.Right, 3, 4),
                        neighbors.get(Piece.T, Rotate.Right, 4, 1),
                        neighbors.get(Piece.T, Rotate.Right, 3, 1)
                );
    }
}