package _experimental.allcomb;

import core.action.reachable.Reachable;
import core.field.Field;
import core.mino.Mino;

public class GrandOnlyReachable implements Reachable {
    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int appearY) {
        return field.isOnGround(mino, x, y);
    }
}
