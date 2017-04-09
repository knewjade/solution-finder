package searcher.common;

import core.mino.Block;
import core.srs.Rotate;
import searcher.common.action.Action;

class ActionParser {
    private static final Block[] blockMap = new Block[Block.values().length];
    private static final Rotate[] rotateMap = new Rotate[Rotate.values().length];

    static {
        for (Block block : Block.values())
            blockMap[block.getNumber()] = block;

        for (Rotate rotate : Rotate.values())
            rotateMap[rotate.getNumber()] = rotate;
    }

    static int parseToInt(Block block, Action action) {
        return action.getX() + action.getY() * 10 + action.getRotate().getNumber() * 240 + block.getNumber() * 240 * 4;
    }

    static Operation parseToOperation(int value) {
        int x = value % 10;
        value /= 10;
        int y = value % 24;
        value /= 24;
        Rotate rotate = rotateMap[value % 4];
        value /= 4;
        Block block = blockMap[value];
        return new Operation(block, rotate, x, y);
    }
}
