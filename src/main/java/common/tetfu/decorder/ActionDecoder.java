package common.tetfu.decorder;

import core.srs.Rotate;
import common.tetfu.common.ColorType;
import common.tetfu.common.Coordinate;
import common.tetfu.TetfuTable;

import static common.tetfu.Tetfu.*;

public class ActionDecoder {
    public final ColorType colorType;
    public final Rotate rotate;
    public final Coordinate coordinate;
    public final boolean isBlockUp;
    public final boolean isMirror;
    public final boolean isColor;
    public final boolean isComment;
    public final boolean isLock;

    public ActionDecoder(int value) {
        colorType = parseBlock(value % 8);
        value /= 8;
        rotate = parseRotate(value % 4);
        value /= 4;
        coordinate = parseCoordinate(value % TETFU_FIELD_BLOCKS, colorType, rotate);
        value /= TETFU_FIELD_BLOCKS;
        isBlockUp = parseBool(value % 2);
        value /= 2;
        isMirror = parseBool(value % 2);
        value /= 2;
        isColor = parseBool(value % 2);
        value /= 2;
        isComment = parseBool(value % 2);
        value /= 2;
        isLock = !parseBool(value % 2);
        assert value / 2 == 0;
    }

    private boolean parseBool(int value) {
        return value != 0;
    }

    private ColorType parseBlock(int value) {
        return TetfuTable.parseColorType(value);
    }

    private Rotate parseRotate(int value) {
        switch (value) {
            case 0:
                return Rotate.Reverse;
            case 1:
                return Rotate.Right;
            case 2:
                return Rotate.Spawn;
            case 3:
                return Rotate.Left;
        }
        throw new IllegalStateException("No reachable");
    }

    private Coordinate parseCoordinate(int value, ColorType type, Rotate rotate) {
        int x = value % TETFU_FIELD_WIDTH;
        int originY = value / 10;
        int y = TETFU_FIELD_TOP - originY - 1;

        if (type == ColorType.O && rotate == Rotate.Left) {
            x += 1;
            y -= 1;
        } else if (type == ColorType.O && rotate == Rotate.Reverse)
            x += 1;
        else if (type == ColorType.O && rotate == Rotate.Spawn)
            y -= 1;
        else if (type == ColorType.I && rotate == Rotate.Reverse)
            x += 1;
        else if (type == ColorType.I && rotate == Rotate.Left)
            y -= 1;
        else if (type == ColorType.S && rotate == Rotate.Spawn)
            y -= 1;
        else if (type == ColorType.S && rotate == Rotate.Right)
            x -= 1;
        else if (type == ColorType.Z && rotate == Rotate.Spawn)
            y -= 1;
        else if (type == ColorType.Z && rotate == Rotate.Left)
            x += 1;

        return new Coordinate(x, y);
    }
}
