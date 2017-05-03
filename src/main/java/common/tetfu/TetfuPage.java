package common.tetfu;

import core.srs.Rotate;
import common.tetfu.common.ColorType;
import common.tetfu.common.Coordinate;
import common.tetfu.decorder.ActionDecoder;
import common.tetfu.field.ColoredField;

import static common.tetfu.Tetfu.TETFU_MAX_HEIGHT;

public class TetfuPage {
    private final ColorType colorType;
    private final Coordinate coordinate;
    private final Rotate rotate;
    private final String escapedComment;
    private final ColoredField field;
    private final ActionDecoder actionDecoder;

    TetfuPage(ActionDecoder decoder, String escapedComment, ColoredField field) {
        this.colorType = decoder.colorType;
        this.coordinate = decoder.coordinate;
        this.rotate = decoder.rotate;
        this.escapedComment = escapedComment;
        this.field = field.freeze(TETFU_MAX_HEIGHT);
        this.actionDecoder = decoder;
    }

    public ColorType getColorType() {
        return colorType;
    }

    public int getX() {
        return coordinate.x;
    }

    public int getY() {
        return coordinate.y;
    }

    public Rotate getRotate() {
        return rotate;
    }

    public String getComment() {
        return TetfuTable.unescape(escapedComment);
    }

    public ColoredField getField() {
        return field;
    }

    public boolean isPutMino() {
        return ColorType.isMinoBlock(colorType) && actionDecoder.isLock;
    }

    @Override
    public String toString() {
        return "TetfuPage{" +
                "colorType=" + colorType +
                ", coordinate=" + coordinate +
                ", rotate=" + rotate +
                ", escapedComment='" + escapedComment + '\'' +
                ", field=" + field +
                '}';
    }
}
