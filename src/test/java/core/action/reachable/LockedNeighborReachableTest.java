package core.action.reachable;

import com.google.inject.Guice;
import com.google.inject.Injector;
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
import lib.Randoms;
import module.BasicModule;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LockedNeighborReachableTest {
    private LockedReachable createLockedReachable(Injector injector, int maxClearLine) {
        MinoFactory minoFactory = injector.getInstance(MinoFactory.class);
        MinoShifter minoShifter = injector.getInstance(MinoShifter.class);
        MinoRotation minoRotation = injector.getInstance(MinoRotation.class);
        return new LockedReachable(minoFactory, minoShifter, minoRotation, maxClearLine);
    }

    private LockedNeighborReachable createLockedNeighborReachable(Injector injector, int maxClearLine) {
        MinoShifter minoShifter = injector.getInstance(MinoShifter.class);
        Neighbors neighbors = injector.getInstance(Neighbors.class);
        return new LockedNeighborReachable(minoShifter, neighbors, maxClearLine);
    }

    @Test
    void checks1() {
        int maxClearLine = 4;
        Injector injector = Guice.createInjector(new BasicModule(maxClearLine));
        LockedNeighborReachable reachable = createLockedNeighborReachable(injector, maxClearLine);

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
        Injector injector = Guice.createInjector(new BasicModule(maxClearLine));
        LockedNeighborReachable reachable = createLockedNeighborReachable(injector, maxClearLine);

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
        Injector injector = Guice.createInjector(new BasicModule(maxClearLine));
        LockedReachable reachable1 = createLockedReachable(injector, maxClearLine);
        LockedNeighborReachable reachable2 = createLockedNeighborReachable(injector, maxClearLine);

        OriginalPieceFactory pieceFactory = injector.getInstance(OriginalPieceFactory.class);
        Set<OriginalPiece> pieces = pieceFactory.create();

        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
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