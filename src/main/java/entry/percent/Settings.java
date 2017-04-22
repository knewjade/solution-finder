package entry.percent;

import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import tetfu.common.ColorType;
import tetfu.field.ColoredField;

public class Settings {
    public static final int EMPTY_BLOCK_NUMBER = ColorType.Empty.getNumber();
    private boolean isUsingHold = true;
    private Field field = null;
    private int maxClearLine = -1;

    public boolean isUsingHold() {
        return isUsingHold;
    }

    public void setUsingHold(boolean flag) {
        this.isUsingHold = flag;
    }

    public void setField(ColoredField coloredField, int height) {
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getBlockNumber(x, y) != EMPTY_BLOCK_NUMBER)
                    field.setBlock(x, y);
        this.field = field;
    }

    public void setMaxClearLine(int value) {
        this.maxClearLine = value;
    }
}
