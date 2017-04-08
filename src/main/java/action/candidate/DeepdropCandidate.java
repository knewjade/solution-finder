package action.candidate;

import core.mino.Block;
import core.srs.Rotate;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.common.action.Action;

import java.util.Set;
import java.util.TreeSet;

public class DeepdropCandidate implements Candidate<Action> {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;

    public DeepdropCandidate(MinoFactory minoFactory, MinoShifter minoShifter) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
    }

    @Override
    public Set<Action> search(Field field, Block block, int appearY) {
        TreeSet<Action> actions = new TreeSet<>();

        for (Rotate rotate : Rotate.values()) {
            Mino mino = minoFactory.create(block, rotate);
            for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                for (int y = appearY - mino.getMaxY() - 1; -mino.getMinY() <= y; y--) {
                    if (field.canPutMino(mino, x, y) && field.isOnGround(mino, x, y)) {
                        Action action = minoShifter.createTransformedAction(block, x, y, rotate);
                        actions.add(action);
                    }
                }
            }
        }

        return actions;
    }
}
