package action.reachable;

import core.mino.Block;
import core.field.Field;
import core.mino.Mino;
import core.srs.Rotate;

public interface Reachable {
    boolean checks(Field field, Mino mino, int x, int y, int appearY);
}
