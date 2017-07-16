package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.Rotate;
import lib.Coordinates;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LockedCandidateTest {
    @Test
    void testSearch1() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "__________" +
                "__________" +
                "____X_____";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn)).hasSize(8);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right)).hasSize(9);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse)).hasSize(8);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left)).hasSize(9);
    }

    @Test
    void testSearch2() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXXX______" +
                "XXXXX_____" +
                "X___X_____" +
                "XX_XX_____";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn)).hasSize(3);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right)).hasSize(4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse)).hasSize(4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left)).hasSize(4);
    }

    @Test
    void testSearch3() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXXX______" +
                "XX_XXXXX__" +
                "X___X_____" +
                "XX_X______";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions)
                .hasSize(11)
                .contains(MinimalAction.create(8, 0, Rotate.Spawn))
                .contains(MinimalAction.create(7, 0, Rotate.Spawn))
                .contains(MinimalAction.create(6, 0, Rotate.Spawn))
                .contains(MinimalAction.create(5, 0, Rotate.Spawn))
                .contains(MinimalAction.create(8, 1, Rotate.Reverse))
                .contains(MinimalAction.create(7, 1, Rotate.Reverse))
                .contains(MinimalAction.create(6, 1, Rotate.Reverse))
                .contains(MinimalAction.create(8, 3, Rotate.Reverse))
                .contains(MinimalAction.create(9, 1, Rotate.Left))
                .contains(MinimalAction.create(8, 1, Rotate.Left))
                .contains(MinimalAction.create(8, 1, Rotate.Right));
    }

    @Test
    void testSearch4() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "X_________" +
                "XX__XXXXXX" +
                "X__XXXXXXX" +
                "X_XXXXXXXX";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.Z, 4);
        assertThat(actions)
                .hasSize(5)
                .contains(MinimalAction.create(2, 2, Rotate.Spawn))
                .contains(MinimalAction.create(2, 3, Rotate.Reverse))
                .contains(MinimalAction.create(3, 2, Rotate.Left))
                .contains(MinimalAction.create(2, 2, Rotate.Right))
                .contains(MinimalAction.create(1, 1, Rotate.Right));
    }

    @Test
    void testRandom() throws Exception {
        Randoms randoms = new Randoms();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();

        for (int count = 0; count < 10000; count++) {
            int randomHeight = randoms.nextIntClosed(2, 12);
            int numOfMinos = randoms.nextIntClosed(4, randomHeight * 10 / 4 - 1);
            Field field = randoms.field(randomHeight, numOfMinos);
            int height = randomHeight - field.clearLine();
            Block block = randoms.block();

            LockedCandidate candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);
            Set<Action> actions = candidate.search(field, block, height);

            LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);

            for (Rotate rotate : Rotate.values()) {
                Coordinates.walk(minoFactory.create(block, rotate), height)
                        .map(coordinate -> MinimalAction.create(coordinate.x, coordinate.y, rotate))
                        .forEach(action -> {
                            int x = action.getX();
                            int y = action.getY();
                            Mino mino = minoFactory.create(block, action.getRotate());

                            Action transformedAction = minoShifter.createTransformedAction(block, action);
                            if (actions.contains(transformedAction)) {
                                // おける
                                assertThat(field.canPutMino(mino, x, y)).isTrue();
                                assertThat(field.isOnGround(mino, x, y)).isTrue();
                                assertThat(reachable.checks(field, mino, x, y, height)).isTrue();
                            } else {
                                // おけない
                                boolean canPut = field.canPutMino(mino, x, y) &&
                                        field.isOnGround(mino, x, y) &&
                                        reachable.checks(field, mino, x, y, height);
                                assertThat(canPut).isFalse();
                            }
                        });
            }
        }
    }
}