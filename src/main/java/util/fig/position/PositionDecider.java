package util.fig.position;

import util.fig.Rectangle;

public interface PositionDecider {
    Rectangle DUMMY = new Rectangle();

    Rectangle getField();

    Rectangle getInField(int xIndex, int yIndex);

    Rectangle getHold();

    Rectangle getNext(int index);
}
