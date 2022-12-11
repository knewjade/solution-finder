package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.FileMinoRotationFactory;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class Locked180CandidateTest {
    @Test
    void testSearch1() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = FileMinoRotationFactory.load(Paths.get("kicks/nullpomino180.properties")).create();
        Candidate<Action> candidate = new Locked180Candidate(minoFactory, minoShifter, minoRotation, 6);

        String marks = "" +
                "_XXXXXXXXX" +
                "_X_XXXXXX_" +
                "_X_XXXXXX_" +
                "_X_XXXXXX_" +
                "_X_XXXXXX_";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Piece.I, 6);
        assertThat(actions)
                .hasSize(9)
                .satisfies((actions1) -> {
                    assertThat(actions1).filteredOn(action -> action.getRotate() == Rotate.Spawn)
                            .hasSize(7)
                            .anyMatch((e) -> e.getX() == 1 && e.getY() == 5)
                            .anyMatch((e) -> e.getX() == 2 && e.getY() == 5)
                            .anyMatch((e) -> e.getX() == 3 && e.getY() == 5)
                            .anyMatch((e) -> e.getX() == 4 && e.getY() == 5)
                            .anyMatch((e) -> e.getX() == 5 && e.getY() == 5)
                            .anyMatch((e) -> e.getX() == 6 && e.getY() == 5)
                            .anyMatch((e) -> e.getX() == 7 && e.getY() == 5);
                    assertThat(actions1).filteredOn(action -> action.getRotate() == Rotate.Left)
                            .hasSize(2)
                            .anyMatch((e) -> e.getX() == 0 && e.getY() == 1)
                            .anyMatch((e) -> e.getX() == 2 && e.getY() == 1);
                });
    }

    @Test
    void testSearch2() {
        Set<Action> actionsSRS;
        {
            MinoFactory minoFactory = new MinoFactory();
            MinoShifter minoShifter = new MinoShifter();
            MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
            Candidate<Action> candidate = CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, 24);

            String marks = "" +
                    "XXXXXX____" +
                    "XXXXXX__X_" +
                    "XXXXXXX__X";
            Field field = FieldFactory.createField(marks);

            actionsSRS = candidate.search(field, Piece.L, 24);
            assertThat(actionsSRS).hasSize(34);
        }
        {
            MinoFactory minoFactory = new MinoFactory();
            MinoShifter minoShifter = new MinoShifter();
            MinoRotation minoRotation = FileMinoRotationFactory.load(Paths.get("kicks/nokicks.properties")).create();
            Candidate<Action> candidate = new Locked180Candidate(minoFactory, minoShifter, minoRotation, 24);

            String marks = "" +
                    "XXXXXX____" +
                    "XXXXXX__X_" +
                    "XXXXXXX__X";
            Field field = FieldFactory.createField(marks);

            Set<Action> actions = candidate.search(field, Piece.L, 24);
            assertThat(actions).hasSize(35);

            // SRSでは到達できないActionを加えると同じになる
            actionsSRS.add(MinimalAction.create(7, 1, Rotate.Right));
            assertThat(actions).isEqualTo(actionsSRS);
        }
    }
}