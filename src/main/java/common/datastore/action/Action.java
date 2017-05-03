package common.datastore.action;

import core.srs.Rotate;

public interface Action extends Comparable<Action> {
    int getX();

    int getY();

    Rotate getRotate();
}
