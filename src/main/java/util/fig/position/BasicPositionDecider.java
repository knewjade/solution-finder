package util.fig.position;

import util.fig.FigSetting;
import util.fig.Rectangle;

public class BasicPositionDecider implements PositionDecider {
    private final FigSetting setting;
    private final FieldOnlyPositionDecider fieldPositionDecider;

    public BasicPositionDecider(FigSetting setting) {
        this.setting = setting;
        this.fieldPositionDecider = new FieldOnlyPositionDecider(setting);
    }

    @Override
    public Rectangle getField() {
        int left = getHoldLeftMargin();
        return new Rectangle(left, 0, setting.getFieldWidthPx(), setting.getScreenHeight());
    }

    @Override
    public Rectangle getInField(int xIndex, int yIndex) {
        Rectangle rectangle = fieldPositionDecider.getInField(xIndex, yIndex);
        if (rectangle == DUMMY)
            return DUMMY;

        int leftMargin = getHoldLeftMargin();
        return new Rectangle(rectangle.getX() + leftMargin, rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    private int getHoldLeftMargin() {
        int nextBoxSize = setting.getNextBoxSize();
        int nextBoxMargin = setting.getNextBoxMargin();
        int fieldBlockMargin = setting.getFieldBlockMargin();
        return nextBoxSize + 2 * nextBoxMargin - fieldBlockMargin;
    }

    @Override
    public Rectangle getHold() {
        int nextBoxSize = setting.getNextBoxSize();
        int nextBoxMargin = setting.getNextBoxMargin();
        return new Rectangle(nextBoxMargin, nextBoxMargin, nextBoxSize, nextBoxSize);
    }

    @Override
    public Rectangle getNext(int index) {
        int leftMargin = getHoldLeftMargin();

        int nextBoxSize = setting.getNextBoxSize();
        int nextBoxMargin = setting.getNextBoxMargin();
        int upperMargin = nextBoxSize + nextBoxMargin;
        int fieldBlockMargin = setting.getFieldBlockMargin();

        int x = leftMargin + setting.getFieldWidthPx() - fieldBlockMargin + nextBoxMargin;
        int y = upperMargin * index + nextBoxMargin;
        return new Rectangle(x, y, nextBoxSize, nextBoxSize);
    }
}
