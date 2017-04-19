package tetfu.encorder;

import core.srs.Rotate;
import tetfu.ActionFlags;
import tetfu.ColorType;
import tetfu.TetfuElement;

import static tetfu.Tetfu.*;

public class ActionEncoder extends Encoder {
    private final TetfuElement element;
    private final ActionFlags flags;

    public ActionEncoder(TetfuElement element, ActionFlags flags) {
        this.element = element;
        this.flags = flags;
    }

    // ミノの置く場所をエンコードする
    public boolean encode() {
        boolean isComment = flags.isCommented && !flags.escapedComment.equals(flags.prevEscapedComment);

        int value = parseBool(!flags.isLock);
        value *= 2;
        value += parseBool(isComment);
        value *= 2;
        value += (parseBool(flags.isColor));
        value *= 2;
        value += parseBool(flags.isMirror);
        value *= 2;
        value += parseBool(flags.isBlockUp);
        value *= TETFU_FIELD_BLOCKS;
        value += coordinate_to_int(element);
        value *= 4;
        value += parseRotate(element);
        value *= 8;
        value += parseColorType(element);

        pushValues(value, 3);

        return isComment;
    }

    private int parseBool(boolean flag) {
        return flag ? 1 : 0;
    }

    private int coordinate_to_int(TetfuElement element) {
        int x = element.getX();
        int y = element.getY();
        ColorType type = element.getColorType();
        Rotate rotate = element.getRotate();

        if (!ColorType.isBlock(type)) {
            x = 0;
            y = 22;
        } else if (type == ColorType.O && rotate == Rotate.Left) {
            x -= 1;
            y -= 1;
        } else if (type == ColorType.O && rotate == Rotate.Reverse)
            x -= 1;
        else if (type == ColorType.O && rotate == Rotate.Spawn)
            y += 1;
        else if (type == ColorType.I && rotate == Rotate.Reverse)
            x -= 1;
        else if (type == ColorType.I && rotate == Rotate.Left)
            y += 1;
        else if (type == ColorType.S && rotate == Rotate.Spawn)
            y += 1;
        else if (type == ColorType.S && rotate == Rotate.Right)
            x += 1;
        else if (type == ColorType.Z && rotate == Rotate.Spawn)
            y += 1;
        else if (type == ColorType.Z && rotate == Rotate.Left)
            x -= 1;

        return (TETFU_FIELD_TOP - y - 1) * TETFU_FIELD_WIDTH + x;
    }

    private int parseRotate(TetfuElement element) {
        ColorType type = element.getColorType();
        Rotate rotate = element.getRotate();

        if (!ColorType.isBlock(type))
            return 0;

        switch (rotate) {
            case Reverse:
                return 0;
            case Right:
                return type != ColorType.I ? 1 : 3;
            case Spawn:
                return 2;
            case Left:
                return type != ColorType.I ? 3 : 1;
        }

        throw new IllegalStateException("No reachable");
    }

    private int parseColorType(TetfuElement element) {
        return element.getColorType().getNumber();
    }
}
