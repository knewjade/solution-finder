package common.tetfu;

import common.tetfu.common.ColorType;
import common.tetfu.common.Coordinate;
import common.tetfu.decorder.ActionDecoder;
import common.tetfu.field.ColoredField;
import core.srs.Rotate;

import static common.tetfu.Tetfu.TETFU_MAX_HEIGHT;

public class DecodedTetfuPage implements TetfuPage {
    private final ColorType colorType;
    private final Coordinate coordinate;
    private final Rotate rotate;
    private final String escapedComment;
    private final ColoredField field;
    private final ActionDecoder actionDecoder;

    DecodedTetfuPage(ActionDecoder decoder, String escapedComment, ColoredField field) {
        this.colorType = decoder.colorType;
        this.coordinate = decoder.coordinate;
        this.rotate = decoder.rotate;
        this.escapedComment = escapedComment;
        this.field = field.freeze(TETFU_MAX_HEIGHT);
        this.actionDecoder = decoder;
    }

    @Override
    public ColorType getColorType() {
        return colorType;
    }

    @Override
    public int getX() {
        return coordinate.x;
    }

    @Override
    public int getY() {
        return coordinate.y;
    }

    @Override
    public Rotate getRotate() {
        return rotate;
    }

    @Override
    public String getComment() {
        return TetfuTable.unescape(escapedComment);
    }

    @Override
    public ColoredField getField() {
        return field;
    }

    @Override
    public boolean isPutMino() {
        return ColorType.isMinoBlock(colorType) && actionDecoder.isLock;
    }

    @Override
    public boolean isLock() {
        return actionDecoder.isLock;
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
