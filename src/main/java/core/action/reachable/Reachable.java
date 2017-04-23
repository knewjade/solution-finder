package core.action.reachable;

import core.field.Field;
import core.mino.Mino;

public interface Reachable {
    boolean checks(Field field, Mino mino, int x, int y, int appearY);
}
