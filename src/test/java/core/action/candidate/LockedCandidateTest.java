package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
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

class LockedCandidateTest {
    @Test
    void testSearch1() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "__________" +
                "__________" +
                "____X_____";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Piece.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn)).hasSize(8);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right)).hasSize(9);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse)).hasSize(8);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left)).hasSize(9);
    }

    @Test
    void testSearch2() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXXX______" +
                "XXXXX_____" +
                "X___X_____" +
                "XX_XX_____";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Piece.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn)).hasSize(3);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right)).hasSize(4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse)).hasSize(4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left)).hasSize(4);
    }

    @Test
    void testSearch3() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXXX______" +
                "XX_XXXXX__" +
                "X___X_____" +
                "XX_X______";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Piece.T, 4);
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
    void testSearch4() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "X_________" +
                "XX__XXXXXX" +
                "X__XXXXXXX" +
                "X_XXXXXXXX";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Piece.Z, 4);
        assertThat(actions)
                .hasSize(5)
                .contains(MinimalAction.create(2, 2, Rotate.Spawn))
                .contains(MinimalAction.create(2, 3, Rotate.Reverse))
                .contains(MinimalAction.create(3, 2, Rotate.Left))
                .contains(MinimalAction.create(2, 2, Rotate.Right))
                .contains(MinimalAction.create(1, 1, Rotate.Right));
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

            ILockedCandidate candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);
            Set<Action> actions = candidate.search(field, piece, height);

            ILockedReachable reachable = ReachableFacade.create90Locked(minoFactory, minoShifter, minoRotation, height);

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