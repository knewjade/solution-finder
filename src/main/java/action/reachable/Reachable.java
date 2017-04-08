package action.reachable;

import core.mino.Block;
import core.field.Field;
import core.srs.Rotate;

public interface Reachable {
    boolean checksReachable(Field field, Block block, int x, int y, Rotate rotate, int appearY);
}
