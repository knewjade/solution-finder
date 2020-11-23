package util.fig.position;

import util.fig.FigSetting;
import util.fig.Rectangle;

public class RightPositionDecider implements PositionDecider {
    private final FigSetting setting;
    private final FieldOnlyPositionDecider fieldPositionDecider;

    public RightPositionDecider(FigSetting setting) {
        this.setting = setting;
        this.fieldPositionDecider = new FieldOnlyPositionDecider(setting);
    }

    @Override
    public Rectangle getField() {
        int left = 0;
        return new Rectangle(left, 0, setting.getFieldWidthPx(), setting.getScreenHeight());
    }

    @Override
    public Rectangle getInField(int xIndex, int yIndex) {
        return fieldPositionDecider.getInField(xIndex, yIndex);
    }

    @Override
    public Rectangle getHold() {
        int nextBoxSize = setting.getNextBoxSize();
        int nextBoxMargin = setting.getNextBoxMargin();
        int bottomMargin = nextBoxSize + nextBoxMargin;
        int fieldBlockMargin = setting.getFieldBlockMargin();

        int x = setting.getFieldWidthPx() - fieldBlockMargin + nextBoxMargin;
        int y = setting.getScreenHeight() - bottomMargin;
        return new Rectangle(x, y, nextBoxSize, nextBoxSize);
    }

    @Override
    public Rectangle getNext(int index) {
        int nextBoxSize = setting.getNextBoxSize();
        int nextBoxMargin = setting.getNextBoxMargin();
        int upperMargin = nextBoxSize + nextBoxMargin;
        int fieldBlockMargin = setting.getFieldBlockMargin();

        int x = setting.getFieldWidthPx() - fieldBlockMargin + nextBoxMargin;
        int y = upperMargin * index + nextBoxMargin;
        return new Rectangle(x, y, nextBoxSize, nextBoxSize);
    }
}
