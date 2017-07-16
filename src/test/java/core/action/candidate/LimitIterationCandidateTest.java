package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LimitIterationCandidateTest {
    @Test
    void testSearch1() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LimitIterationCandidate(minoFactory, minoShifter, minoRotation, 3);

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
        Candidate<Action> candidate = new LimitIterationCandidate(minoFactory, minoShifter, minoRotation, 3);

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
    void testSearch3When3Iteration() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LimitIterationCandidate(minoFactory, minoShifter, minoRotation, 3);

        String marks = "" +
                "XXXX______" +
                "XX_XXXXX__" +
                "X___X_____" +
                "XX_X______";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions)
                .hasSize(9)
                .contains(MinimalAction.create(8, 0, Rotate.Spawn))
                .contains(MinimalAction.create(7, 0, Rotate.Spawn))
                .contains(MinimalAction.create(8, 1, Rotate.Reverse))
                .contains(MinimalAction.create(7, 1, Rotate.Reverse))
                .contains(MinimalAction.create(6, 1, Rotate.Reverse))
                .contains(MinimalAction.create(8, 3, Rotate.Reverse))
                .contains(MinimalAction.create(9, 1, Rotate.Left))
                .contains(MinimalAction.create(8, 1, Rotate.Left))
                .contains(MinimalAction.create(8, 1, Rotate.Right));
    }

    @Test
    void testSearch3When4Iteration() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LimitIterationCandidate(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXXX______" +
                "XX_XXXXX__" +
                "X___X_____" +
                "XX_X______";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn))
                .hasSize(3)
                .contains(MinimalAction.create(8, 0, Rotate.Spawn))
                .contains(MinimalAction.create(7, 0, Rotate.Spawn))
                .contains(MinimalAction.create(6, 0, Rotate.Spawn))
                .doesNotContain(MinimalAction.create(5, 0, Rotate.Spawn));
    }

    @Test
    void testSearch4() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Candidate<Action> candidate = new LimitIterationCandidate(minoFactory, minoShifter, minoRotation, 4);

        String marks = "" +
                "XXXXX__XXX" +
                "XXXXX___XX" +
                "XXXX___XXX" +
                "";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 3);
        assertThat(actions)
                .hasSize(5)
                .contains(MinimalAction.create(6, 1, Rotate.Spawn))
                .contains(MinimalAction.create(5, 1, Rotate.Right))
                .contains(MinimalAction.create(6, 1, Rotate.Right))
                .contains(MinimalAction.create(6, 1, Rotate.Left))
                .contains(MinimalAction.create(6, 1, Rotate.Reverse));
    }

    @Test
    void testRandomHarddrop() {
        Randoms randoms = new Randoms();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        HarddropCandidate harddropCandidate = new HarddropCandidate(minoFactory, minoShifter);
        LimitIterationCandidate limitIterationCandidate = new LimitIterationCandidate(minoFactory, minoShifter, minoRotation, 0);

        for (int count = 0; count < 10000; count++) {
            int randomHeight = randoms.nextIntClosed(2, 12);
            int numOfMinos = randoms.nextIntClosed(4, randomHeight * 10 / 4 - 1);
            Field field = randoms.field(randomHeight, numOfMinos);
            int clearLine = field.clearLine();
            int height = randomHeight - clearLine;
            Block block = randoms.block();

            Set<Action> actions1 = harddropCandidate.search(field, block, height);
            Set<Action> actions2 = limitIterationCandidate.search(field, block, height);
            assertThat(actions2).isEqualTo(actions1);
        }
    }

    @Test
    void testRandomLocked() {
        Randoms randoms = new Randoms();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LimitIterationCandidate limitIterationCandidate = new LimitIterationCandidate(minoFactory, minoShifter, minoRotation, 12);

        for (int count = 0; count < 20; count++) {
            int randomHeight = randoms.nextIntClosed(10, 12);
            int numOfMinos = randoms.nextIntClosed(4, randomHeight * 10 / 4 - 1);
            Field field = randoms.field(randomHeight, numOfMinos);
            int clearLine = field.clearLine();
            int height = randomHeight - clearLine;
            Block block = randoms.block();

            String description = FieldView.toString(field, height) + block;
            Set<Action> actions1 = limitIterationCandidate.search(field, block, height);

            LockedCandidate lockedCandidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);
            Set<Action> actions2 = lockedCandidate.search(field, block, height);
            assertThat(actions2)
                    .as(description)
                    .isEqualTo(actions1);
        }
    }
}