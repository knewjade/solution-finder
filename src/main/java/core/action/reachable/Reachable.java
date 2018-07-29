package core.action.reachable;

import core.field.Field;
import core.mino.Mino;

public interface Reachable {
    // checksを呼び出す前に、Field.cansPutの確認を必ずしていること
    boolean checks(Field field, Mino mino, int x, int y, int validHeight);
}
