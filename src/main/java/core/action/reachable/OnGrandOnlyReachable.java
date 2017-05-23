package core.action.reachable;

import core.field.Field;
import core.mino.Mino;

public class OnGrandOnlyReachable implements Reachable {
    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int appearY) {
        return field.isOnGround(mino, x, y);
    }
}
