package common.datastore;

import core.mino.Block;
import core.srs.Rotate;

public interface Operation {
    Block getBlock();

    Rotate getRotate();

    int getX();

    int getY();
}
