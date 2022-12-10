package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.action.reachable.RotateReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import lib.Coordinates;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RotateCandidateTest {
    @Test
    void testSearch1() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        Candidate<Action> candidate = new RotateCandidate(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "________XX" +
                "_________X" +
                "___XXXXX_X" +
                "____XXX__X" +
                "_X_XXXXX_X";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Piece.T, 4);
        assertThat(actions)
                .hasSize(11)
                .contains(MinimalAction.create(1, 1, Rotate.Spawn))
                .contains(MinimalAction.create(2, 1, Rotate.Spawn))
                .contains(MinimalAction.create(0, 1, Rotate.Right))
                .contains(MinimalAction.create(1, 2, Rotate.Right))
                .contains(MinimalAction.create(2, 1, Rotate.Right))
                .contains(MinimalAction.create(1, 2, Rotate.Reverse))
                .contains(MinimalAction.create(2, 1, Rotate.Reverse))
                .contains(MinimalAction.create(2, 3, Rotate.Reverse))
                .contains(MinimalAction.create(1, 2, Rotate.Left))
                .contains(MinimalAction.create(2, 1, Rotate.Left))
                .contains(MinimalAction.create(8, 1, Rotate.Left));
    }

    @Test
    void testSearch2() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        Candidate<Action> candidate = new RotateCandidate(minoFactory, minoShifter, minoRotation, 3);

        String marks = "" +
                "_________X" +
                "X____XX_XX" +
                "XX__XXX__X" +
                "X__XXXXX_X";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Piece.S, 3);

        assertThat(actions)
                .hasSize(3)
                .contains(MinimalAction.create(2, 0, Rotate.Spawn))
                .contains(MinimalAction.create(3, 1, Rotate.Spawn))
                .contains(MinimalAction.create(8, 1, Rotate.Left));
    }

    @Test
    void testRandom() {
        Randoms randoms = new Randoms();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();

        for (int count = 0; count < 10000; count++) {
            int randomHeight = randoms.nextIntClosed(2, 12);
            int numOfMinos = randoms.nextIntClosed(4, randomHeight * 10 / 4 - 1);
            Field field = randoms.field(randomHeight, numOfMinos);
            int height = randomHeight - field.clearLine();
            Piece piece = randoms.block();

            RotateCandidate candidate = new RotateCandidate(minoFactory, minoShifter, minoRotation, height);
            Set<Action> actions = candidate.search(field, piece, height);

            RotateReachable reachable = new RotateReachable(minoFactory, minoShifter, minoRotation, height);

            for (Rotate rotate : Rotate.values()) {
                Coordinates.walk(minoFactory.create(piece, rotate), height)
                        .map(coordinate -> MinimalAction.create(coordinate.x, coordinate.y, rotate))
                        .forEach(action -> {
                            int x = action.getX();
                            int y = action.getY();
                            Mino mino = minoFactory.create(piece, action.getRotate());

                            if (actions.contains(action)) {
                                // おける
                                assertThat(field.canPut(mino, x, y)).isTrue();
                                assertThat(field.isOnGround(mino, x, y)).isTrue();
                                assertThat(reachable.checks(field, mino, x, y, height)).isTrue();
                            } else {
                                // おけない
                                boolean canPut = field.canPut(mino, x, y) &&
                                        field.isOnGround(mino, x, y) &&
                                        reachable.checks(field, mino, x, y, height);
                                assertThat(canPut).isFalse();
                            }
                        });
            }
        }
    }
}