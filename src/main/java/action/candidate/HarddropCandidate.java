package action.candidate;

import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;
import searcher.common.action.Action;

import java.util.HashSet;
import java.util.Set;

public class HarddropCandidate implements Candidate<Action> {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;

    public HarddropCandidate(MinoFactory minoFactory, MinoShifter minoShifter) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
    }

    @Override
    public Set<Action> search(Field field, Block block, int appearY) {
        HashSet<Action> actions = new HashSet<>();

        for (Rotate rotate : Rotate.values()) {
            Mino mino = minoFactory.create(block, rotate);
            int y = appearY - mino.getMaxY() - 1;
            for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                if (field.canPutMino(mino, x, y)) {
                    int harddropY = field.getYOnHarddrop(mino, x, y);
                    Action action = minoShifter.createTransformedAction(block, x, harddropY, rotate);
                    actions.add(action);
                }
            }
        }

        return actions;
    }
}
