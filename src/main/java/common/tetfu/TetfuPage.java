package common.tetfu;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.srs.Rotate;

import java.util.List;

public interface TetfuPage {
    ColorType getColorType();

    int getX();

    int getY();

    Rotate getRotate();

    String getComment();

    ColoredField getField();

    boolean isPutMino();

    boolean isLock();

    boolean isMirror();

    boolean isBlockUp();

    List<Integer> getBlockUpList();
}
