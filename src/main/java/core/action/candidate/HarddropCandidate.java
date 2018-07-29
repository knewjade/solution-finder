package core.action.candidate;

import common.datastore.action.Action;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.Rotate;

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
    // ハードドロップで置くことができ、appearY以下にミノがすべて収まる場所を列挙する
    public Set<Action> search(Field field, Piece piece, int validHeight) {
        HashSet<Action> actions = new HashSet<>();

        for (Rotate rotate : minoShifter.getUniqueRotates(piece)) {
            Mino mino = minoFactory.create(piece, rotate);
            int y = validHeight - mino.getMinY();
            int maxY = validHeight - mino.getMaxY();
            for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                int harddropY = field.getYOnHarddrop(mino, x, y);
                if (harddropY < maxY) {
                    Action action = minoShifter.createTransformedAction(piece, rotate, x, harddropY);
                    actions.add(action);
                }
            }
        }

        return actions;
    }
}
