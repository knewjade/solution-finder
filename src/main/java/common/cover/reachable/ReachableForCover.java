package common.cover.reachable;

import core.field.Field;
import core.mino.Mino;

public interface ReachableForCover {
    // checksを呼び出す前に、Field.cansPutの確認を必ずしていること
    // @param remainingDepth 最後までの残りのdepth。最後のミノを置くとき、remainingDepth=1となる
    boolean checks(Field field, Mino mino, int x, int y, int validHeight, int remainingDepth);
}