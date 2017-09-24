package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.*;
import core.srs.Rotate;
import lib.Coordinates;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class HarddropCandidateTest {
    @Test
    void testSearch1() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        Candidate<Action> candidate = new HarddropCandidate(minoFactory, minoShifter);

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
        Candidate<Action> candidate = new HarddropCandidate(minoFactory, minoShifter);

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
        Candidate<Action> candidate = new HarddropCandidate(minoFactory, minoShifter);

        String marks = "" +
                "XXXX______" +
                "XX_XXXXX__" +
                "X___X_____" +
                "XX_XX_____";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions)
                .hasSize(3)
                .contains(MinimalAction.create(9, 1, Rotate.Left))
                .contains(MinimalAction.create(8, 3, Rotate.Reverse))
                .contains(MinimalAction.create(8, 1, Rotate.Right));
    }

    @Test
    void testSearch4() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        Candidate<Action> candidate = new HarddropCandidate(minoFactory, minoShifter);

        String marks = "" +
                "_______XXX" +
                "_________X";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 2);
        assertThat(actions)
                .contains(MinimalAction.create(5, 0, Rotate.Spawn))
                .doesNotContain(MinimalAction.create(6, 0, Rotate.Spawn));
    }

    @Test
    void testSearch5() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        Candidate<Action> candidate = new HarddropCandidate(minoFactory, minoShifter);

        String marks = "" +
                "X________X" +
                "________XX" +
                "_________X" +
                "_________X" +
                "X________X" +
                "_________X" +
                "XXX_____XX";

        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.J, 7);
        assertThat(actions)
                .doesNotContain(MinimalAction.create(1, 4, Rotate.Left));
    }

    @Test
    void testRandom() throws Exception {
        Randoms randoms = new Randoms();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        HarddropCandidate candidate = new HarddropCandidate(minoFactory, minoShifter);

        for (int count = 0; count < 10000; count++) {
            int randomHeight = randoms.nextIntClosed(2, 12);
            int numOfMinos = randoms.nextIntClosed(4, randomHeight * 10 / 4 - 1);
            Field field = randoms.field(randomHeight, numOfMinos);
            int clearLine = field.clearLine();
            int height = randomHeight - clearLine;
            Block block = randoms.block();

            Set<Action> actions = candidate.search(field, block, height);

            for (Rotate rotate : minoShifter.getUniqueRotates(block)) {
                Coordinates.walk(minoFactory.create(block, rotate), height)
                        .map(coordinate -> MinimalAction.create(coordinate.x, coordinate.y, rotate))
                        .forEach(action -> {
                            int x = action.getX();
                            int y = action.getY();
                            Mino mino = minoFactory.create(block, action.getRotate());

                            boolean canPut = field.isOnGround(mino, x, y) &&
                                    IntStream.range(y, height - mino.getMinY())
                                            .allMatch(up -> field.canPut(mino, x, up));
                            assertThat(actions.contains(action)).isEqualTo(canPut);
                        });
            }
        }
    }
}