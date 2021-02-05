package common.tetfu;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.srs.Rotate;

import java.util.List;

public class DirectTetfuPage implements TetfuPage {
    private final ColorType colorType;
    private final int x;
    private final int y;
    private final Rotate rotate;
    private final String comment;
    private final ColoredField field;
    private final boolean isPutMino;
    private final boolean isLock;
    private final boolean isMirror;
    private final boolean isBlockUp;
    private final List<Integer> blockUp;

    public DirectTetfuPage(
            ColorType colorType, int x, int y, Rotate rotate, String comment, ColoredField field,
            boolean isPutMino, boolean isLock, boolean isMirror, boolean isBlockUp, List<Integer> blockUp
    ) {
        this.colorType = colorType;
        this.x = x;
        this.y = y;
        this.rotate = rotate;
        this.comment = comment;
        this.field = field;
        this.isPutMino = isPutMino;
        this.isLock = isLock;
        this.isMirror = isMirror;
        this.isBlockUp = isBlockUp;
        this.blockUp = blockUp;
    }

    @Override
    public ColorType getColorType() {
        return colorType;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Rotate getRotate() {
        return rotate;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public ColoredField getField() {
        return field;
    }

    @Override
    public boolean isPutMino() {
        return isPutMino;
    }

    @Override
    public boolean isLock() {
        return isLock;
    }

    @Override
    public boolean isMirror() {
        return isMirror;
    }

    @Override
    public boolean isBlockUp() {
        return isBlockUp;
    }

    @Override
    public List<Integer> getBlockUpList() {
        return blockUp;
    }
}
