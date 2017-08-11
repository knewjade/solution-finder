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
            Block block = randoms.block();
            Rotate rotate = randoms.rotate();
            int x = randoms.nextInt(10);
            int y = randoms.nextInt(0, 12);
            MinimalAction action = MinimalAction.create(x, y, rotate);

            Action actualAction = minoShifter.createTransformedAction(block, action);

            assertThat(actualAction).isEqualTo(action);
        }
    }

    @Test
    void createTransformedRotate() {
        Randoms randoms = new Randoms();
        PassedMinoShifter minoShifter = new PassedMinoShifter();

        for (int count = 0; count < 10000; count++) {
            Block block = randoms.block();
            Rotate rotate = randoms.rotate();

            Rotate actualRotate = minoShifter.createTransformedRotate(block, rotate);

            assertThat(actualRotate).isEqualTo(rotate);
        }
    }

    @Test
    void enumerateSameOtherActions() {
        Randoms randoms = new Randoms();
        PassedMinoShifter minoShifter = new PassedMinoShifter();

        for (int count = 0; count < 10000; count++) {
            Block block = randoms.block();
            Rotate rotate = randoms.rotate();
            int x = randoms.nextInt(10);
            int y = randoms.nextInt(0, 12);

            List<Action> actions = minoShifter.enumerateSameOtherActions(block, rotate, x, y);
            assertThat(actions).isEmpty();
        }
    }
}