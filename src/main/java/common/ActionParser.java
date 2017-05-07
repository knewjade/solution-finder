package common;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import core.mino.Block;
import core.srs.Rotate;
import common.datastore.action.Action;

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
        return parseToInt(block, action.getRotate(), action.getX(), action.getY());
    }

    static int parseToInt(Block block, Rotate rotate, int x, int y) {
        return x + y * 10 + rotate.getNumber() * 240 + block.getNumber() * 240 * 4;
    }

    static Operation parseToOperation(int value) {
        int x = value % 10;
        value /= 10;
        int y = value % 24;
        value /= 24;
        Rotate rotate = rotateMap[value % 4];
        value /= 4;
        Block block = blockMap[value];
        return new SimpleOperation(block, rotate, x, y);
    }
}
