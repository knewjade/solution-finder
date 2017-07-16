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

import static org.assertj.core.api.Assertions.assertThat;

class DeepdropCandidateTest {
    @Test
    void testSearch1() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        DeepdropCandidate candidate = new DeepdropCandidate(minoFactory, minoShifter);

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

        assertThat(actions)
                .doesNotContain(MinimalAction.create(0, 0, Rotate.Spawn))
                .contains(MinimalAction.create(1, 0, Rotate.Spawn))
                .contains(MinimalAction.create(2, 0, Rotate.Spawn))
                .doesNotContain(MinimalAction.create(3, 0, Rotate.Spawn))
                .doesNotContain(MinimalAction.create(4, 0, Rotate.Spawn))
                .doesNotContain(MinimalAction.create(5, 0, Rotate.Spawn))
                .contains(MinimalAction.create(6, 0, Rotate.Spawn))
                .doesNotContain(MinimalAction.create(0, 1, Rotate.Spawn))
                .doesNotContain(MinimalAction.create(1, 1, Rotate.Spawn))
                .contains(MinimalAction.create(3, 1, Rotate.Spawn))
                .contains(MinimalAction.create(4, 1, Rotate.Spawn))
                .contains(MinimalAction.create(5, 1, Rotate.Spawn))
                .contains(MinimalAction.create(0, 1, Rotate.Right))
                .doesNotContain(MinimalAction.create(0, 1, Rotate.Left))
                .contains(MinimalAction.create(9, 1, Rotate.Left))
                .doesNotContain(MinimalAction.create(9, 1, Rotate.Right))
                .contains(MinimalAction.create(3, 1, Rotate.Reverse))
                .doesNotContain(MinimalAction.create(4, 1, Rotate.Reverse));
    }

    @Test
    void testSearch2() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        DeepdropCandidate candidate = new DeepdropCandidate(minoFactory, minoShifter);

        String marks = "" +
                "XXXX______" +
                "XXXXX_____" +
                "X___X_____" +
                "XX_XX_____";
        Field field = FieldFactory.createField(marks);

        Set<Action> actions = candidate.search(field, Block.T, 4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Spawn)).hasSize(3);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Right)).hasSize(4);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Reverse)).hasSize(5);
        assertThat(actions.stream().filter((e) -> e.getRotate() == Rotate.Left)).hasSize(4);

        assertThat(actions)
                .contains(MinimalAction.create(2, 1, Rotate.Reverse))
                .doesNotContain(MinimalAction.create(2, 1, Rotate.Spawn))
                .doesNotContain(MinimalAction.create(2, 1, Rotate.Left))
                .doesNotContain(MinimalAction.create(2, 1, Rotate.Right))
                .doesNotContain(MinimalAction.create(2, 0, Rotate.Reverse))
                .doesNotContain(MinimalAction.create(2, 2, Rotate.Reverse));
    }

    @Test
    void testRandom() throws Exception {
        Randoms randoms = new Randoms();

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new PassedMinoShifter();
        DeepdropCandidate candidate = new DeepdropCandidate(minoFactory, minoShifter);

        for (int count = 0; count < 10000; count++) {
            int height = randoms.nextIntClosed(2, 12);
            int numOfMinos = randoms.nextIntClosed(4, height * 10 / 4 - 1);
            Field field = randoms.field(height, numOfMinos);
            height -= field.clearLine();
            Block block = randoms.block();

            Set<Action> actions = candidate.search(field, block, height);

            for (Rotate rotate : Rotate.values()) {
                Coordinates.walk(minoFactory.create(block, rotate), height)
                        .map(coordinate -> MinimalAction.create(coordinate.x, coordinate.y, rotate))
                        .forEach(action -> {
                            int x = action.getX();
                            int y = action.getY();
                            Mino mino = minoFactory.create(block, action.getRotate());

                            if (actions.contains(action)) {
                                // おける
                                assertThat(field.canPutMino(mino, x, y)).isTrue();
                                assertThat(field.isOnGround(mino, x, y)).isTrue();
                            } else {
                                // おけない
                                assertThat(field.canPutMino(mino, x, y) && field.isOnGround(mino, x, y)).isFalse();
                            }
                        });
            }
        }
    }
}