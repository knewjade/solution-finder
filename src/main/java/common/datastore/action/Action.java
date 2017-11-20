package common.datastore.action;

import core.srs.Rotate;

public interface Action {
    int getX();

    int getY();

    Rotate getRotate();
}
