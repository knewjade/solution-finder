package util.fig.position;

import util.fig.FigSetting;
import util.fig.Rectangle;

public class FieldOnlyPositionDecider implements PositionDecider {
    private final FigSetting setting;

    public FieldOnlyPositionDecider(FigSetting setting) {
        this.setting = setting;
    }

    @Override
    public Rectangle getInField(int xIndex, int yIndex) {
        int x = getX(xIndex);
        int y = getY(yIndex);
        int fieldBlockSize = setting.getFieldBlockSize();
        return new Rectangle(x, y, fieldBlockSize, fieldBlockSize);
    }

    private int getX(int xIndex) {
        int fieldBlockSize = setting.getFieldBlockSize();
        int fieldBlockMargin = setting.getFieldBlockMargin();
        int size = fieldBlockSize + fieldBlockMargin;
        return xIndex * size + fieldBlockMargin;
    }

    private int getY(int yIndex) {
        int fieldHeightBlock = setting.getFieldHeightBlock();
        int fieldBlockSize = setting.getFieldBlockSize();
        int fieldBlockMargin = setting.getFieldBlockMargin();
        int y = fieldHeightBlock - yIndex - 1;
        int size = fieldBlockSize + fieldBlockMargin;
        return y * size + fieldBlockMargin;
    }

    @Override
    public Rectangle getHold() {
        return DUMMY;
    }

    @Override
    public Rectangle getNext(int index) {
        return DUMMY;
    }
}
