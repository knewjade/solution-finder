package common.tetfu;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.srs.Rotate;

import java.util.List;
import java.util.Optional;

import static common.tetfu.Tetfu.TETFU_MAX_HEIGHT;

public class TetfuElement {
    public static TetfuElement createFieldOnly(ColoredField coloredField) {
        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0);
    }

    private final String comment;
    private final ColoredField field;
    private final ColorType colorType;
    private final Rotate rotate;
    private final int x;
    private final int y;
    private final boolean isLock;
    private final boolean isMirror;
    private final boolean isBlockUp;
    private final List<Integer> blockUp;

    public TetfuElement(String comment) {
        this(ColorType.Empty, Rotate.Reverse, 0, 0, comment);
    }

    public TetfuElement(ColorType colorType, Rotate rotate, int x, int y) {
        this(colorType, rotate, x, y, "");
    }

    public TetfuElement(ColorType colorType, Rotate rotate, int x, int y, String comment) {
        this(null, colorType, rotate, x, y, comment);
    }

    public TetfuElement(ColoredField field) {
        this(field, "");
    }

    public TetfuElement(ColoredField field, String quiz) {
        this(field, ColorType.Empty, Rotate.Reverse, 0, 0, quiz);
    }

    public TetfuElement(ColoredField field, ColorType colorType, Rotate rotate, int x, int y) {
        this(field, colorType, rotate, x, y, "");
    }

    public TetfuElement(ColoredField field, ColorType colorType, Rotate rotate, int x, int y, String comment) {
        this(field, colorType, rotate, x, y, comment, true, false, false, null);
    }

    public TetfuElement(ColoredField field, ColorType colorType, Rotate rotate, int x, int y, String comment, boolean isLock) {
        this(field, colorType, rotate, x, y, comment, isLock, false, false, null);
    }

    public TetfuElement(
            ColoredField field, ColorType colorType, Rotate rotate, int x, int y, String comment,
            boolean isLock, boolean isMirror, boolean isBlockUp, List<Integer> blockUp
    ) {
        assert field == null || field.getMaxHeight() == TETFU_MAX_HEIGHT;
        this.field = field;
        this.colorType = colorType;
        this.rotate = rotate;
        this.x = x;
        this.y = y;
        this.comment = comment;
        this.isLock = isLock;
        this.isMirror = isMirror;
        this.isBlockUp = isBlockUp;
        this.blockUp = blockUp;
    }

    public Optional<ColoredField> getField() {
        return Optional.ofNullable(field);
    }

    String getEscapedComment() {
        return TetfuTable.escape(comment);
    }

    public ColorType getColorType() {
        return colorType;
    }

    public Rotate getRotate() {
        return rotate;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getComment() {
        return comment;
    }

    public boolean isLock() {
        return isLock;
    }

    public int getBlockUp(int x) {
        return blockUp != null ? blockUp.get(x) : 0;
    }
    public Optional<List<Integer>> getBlockUpList() {
        return Optional.ofNullable(blockUp);
    }

    public boolean isMirror() {
        return isMirror;
    }

    public boolean isBlockUp() {
        return isBlockUp;
    }
}
