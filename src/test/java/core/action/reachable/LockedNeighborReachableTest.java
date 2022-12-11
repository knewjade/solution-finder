package core.action.reachable;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.Neighbors;
import core.neighbor.OriginalPiece;
import core.neighbor.OriginalPieceFactory;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LockedNeighborReachableTest {
    private ILockedReachable createLockedReachable(int maxClearLine) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        return ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
    }

    private LockedNeighborReachable createLockedNeighborReachable(int maxClearLine) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(maxClearLine + 3);
        Neighbors neighbors = new Neighbors(minoFactory, minoRotation, pieceFactory);
        return new LockedNeighborReachable(minoShifter, neighbors, maxClearLine);
    }

    @Test
    void checks1() {
        int maxClearLine = 4;
        LockedNeighborReachable reachable = createLockedNeighborReachable(maxClearLine);

        Field field = FieldFactory.createField("" +
                "XXXX______" +
                "___X______" +
                "___X______" +
                "__________"
        );
        assertThat(reachable.checks(field, new Mino(Piece.T, Rotate.Spawn), 8, 0, maxClearLine)).isTrue();
        assertThat(reachable.checks(field, new Mino(Piece.T, Rotate.Spawn), 1, 0, maxClearLine)).isFalse();
    }

    @Test
    void checks2() {
        int maxClearLine = 4;
        LockedNeighborReachable reachable = createLockedNeighborReachable(maxClearLine);

        Field field = FieldFactory.createField("" +
                "X______XXX" +
                "XXXXXX___X" +
                "XXX_____XX" +
                "XXX______X"
        );
        boolean checks = reachable.checks(field, new Mino(Piece.J, Rotate.Spawn), 4, 0, maxClearLine);
        assertThat(checks).isFalse();

    }

    @Test
    void randoms() {
        int maxClearLine = 4;
        ILockedReachable reachable1 = createLockedReachable(maxClearLine);
        LockedNeighborReachable reachable2 = createLockedNeighborReachable(maxClearLine);

        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(maxClearLine + 3);
        Set<OriginalPiece> pieces = pieceFactory.createPieces();

        Randoms randoms = new Randoms();
        for (int count = 0; count < 5000; count++) {
            Field field = randoms.field(4, 5);
            for (OriginalPiece piece : pieces) {
                // フィールドにそもそも置けないときは前提条件が合わないためスキップ
                if (!field.canPut(piece))
                    continue;

                Mino mino = piece.getMino();
                int x = piece.getX();
                int y = piece.getY();
                boolean checks1 = reachable1.checks(field, mino, x, y, maxClearLine);
                boolean checks2 = reachable2.checks(field, mino, x, y, maxClearLine);
                assertThat(checks2).as(piece.toString())
                        .isEqualTo(checks1);
            }
        }
    }
}