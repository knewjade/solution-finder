package util.gif.position;

import util.gif.Rectangle;

public interface PositionDecider {
    Rectangle DUMMY = new Rectangle();

    Rectangle getInField(int xIndex, int yIndex);

    Rectangle getHold();

    Rectangle getNext(int index);
}
