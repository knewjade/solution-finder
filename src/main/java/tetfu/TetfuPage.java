package tetfu;

import core.srs.Rotate;
import tetfu.common.ColorType;
import tetfu.common.Coordinate;
import tetfu.decorder.ActionDecoder;
import tetfu.field.ColoredField;

import static tetfu.Tetfu.TETFU_MAX_HEIGHT;

public class TetfuPage {
    private final ColorType colorType;
    private final Coordinate coordinate;
    private final Rotate rotate;
    private final String escapedComment;
    private final ColoredField field;
    private final ActionDecoder actionDecoder;

    public TetfuPage(ActionDecoder decoder, String escapedComment, ColoredField field) {
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
