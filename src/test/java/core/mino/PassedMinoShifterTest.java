package core.mino;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PassedMinoShifterTest {
    @Test
    void createTransformedAction() {
        Randoms randoms = new Randoms();
        PassedMinoShifter minoShifter = new PassedMinoShifter();

        for (int count = 0; count < 10000; count++) {
            Piece piece = randoms.block();
            Rotate rotate = randoms.rotate();
            int x = randoms.nextIntOpen(10);
            int y = randoms.nextIntOpen(0, 12);
            MinimalAction action = MinimalAction.create(x, y, rotate);

            Action actualAction = minoShifter.createTransformedAction(piece, action);

            assertThat(actualAction).isEqualTo(action);
        }
    }

    @Test
    void createTransformedRotate() {
        Randoms randoms = new Randoms();
        PassedMinoShifter minoShifter = new PassedMinoShifter();

        for (int count = 0; count < 10000; count++) {
            Piece piece = randoms.block();
            Rotate rotate = randoms.rotate();

            Rotate actualRotate = minoShifter.createTransformedRotate(piece, rotate);

            assertThat(actualRotate).isEqualTo(rotate);
        }
    }

    @Test
    void enumerateSameOtherActions() {
        Randoms randoms = new Randoms();
        PassedMinoShifter minoShifter = new PassedMinoShifter();

        for (int count = 0; count < 10000; count++) {
            Piece piece = randoms.block();
            Rotate rotate = randoms.rotate();
            int x = randoms.nextIntOpen(10);
            int y = randoms.nextIntOpen(0, 12);

            List<Action> actions = minoShifter.enumerateSameOtherActions(piece, rotate, x, y);
            assertThat(actions).isEmpty();
        }
    }
}