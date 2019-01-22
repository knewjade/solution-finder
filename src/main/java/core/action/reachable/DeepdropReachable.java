package core.action.reachable;

import core.field.Field;
import core.mino.Mino;

/**
 * マルチスレッド非対応
 */
public class DeepdropReachable implements Reachable {
    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int validHeight) {
        return field.canPut(mino, x, y) && y + mino.getMaxY() < validHeight;
    }
}