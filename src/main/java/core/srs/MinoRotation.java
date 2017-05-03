package core.srs;

import core.mino.Block;
import core.field.Field;
import core.mino.Mino;

import java.util.EnumMap;

// TODO: unittest
public class MinoRotation {
    private static final int FIELD_WIDTH = 10;

    private final EnumMap<Block, EnumMap<Rotate, Pattern>> rightMap;
    private final EnumMap<Block, EnumMap<Rotate, Pattern>> leftMap;

    public MinoRotation() {
        this.rightMap = createRightMap();
        this.leftMap = createLeftMap();
    }

    private EnumMap<Block, EnumMap<Rotate, Pattern>> createRightMap() {
        EnumMap<Block, EnumMap<Rotate, Pattern>> blockMap = new EnumMap<>(Block.class);
        for (Block block : Block.values()) {
            EnumMap<Rotate, Pattern> rotateMap = new EnumMap<>(Rotate.class);
            for (Rotate rotate : Rotate.values()) {
                Pattern pattern = getPattern(block, rotate, rotate.getRightRotate());
                rotateMap.put(rotate, pattern);
            }
            blockMap.put(block, rotateMap);
        }
        return blockMap;
    }

    private Pattern getPattern(Block block, Rotate current, Rotate next) {
        switch (block) {
            case I:
                return OffsetDefine.I.getPattern(current, next);
            case O:
                return OffsetDefine.O.getPattern(current, next);
            default:
                return OffsetDefine.Other.getPattern(current, next);
        }
    }

    private EnumMap<Block, EnumMap<Rotate, Pattern>> createLeftMap() {
        EnumMap<Block, EnumMap<Rotate, Pattern>> blockMap = new EnumMap<>(Block.class);
        for (Block block : Block.values()) {
            EnumMap<Rotate, Pattern> rotateMap = new EnumMap<>(Rotate.class);
            for (Rotate rotate : Rotate.values()) {
                Pattern pattern = getPattern(block, rotate, rotate.getLeftRotate());
                rotateMap.put(rotate, pattern);
            }
            blockMap.put(block, rotateMap);
        }
        return blockMap;
    }

    public int[] getKicksWithRightRotation(Field field, Mino before, Mino after, int x, int y) {
        Pattern pattern = rightMap.get(before.getBlock()).get(before.getRotate());
        return getKicks(field, x, y, after, pattern);
    }

    private int[] getKicks(Field field, int x, int y, Mino after, Pattern pattern) {
        int[][] offsets = pattern.getOffsets();
        int minX = -after.getMinX();
        int maxX = FIELD_WIDTH - after.getMaxX();
        int minY = -after.getMinY();
        for (int[] offset : offsets) {
            int toX = x + offset[0];
            int toY = y + offset[1];
            if (minX <= toX && toX < maxX && minY <= toY && field.canPutMino(after, toX, toY))
                return offset;
        }
        return null;
    }

    public int[] getKicksWithLeftRotation(Field field,Mino before, Mino after, int x, int y) {
        Pattern pattern = leftMap.get(before.getBlock()).get(before.getRotate());
        return getKicks(field, x, y, after, pattern);
    }

    public int[][] getRightPatternsFrom(Mino current) {
        return rightMap.get(current.getBlock()).get(current.getRotate()).getOffsets();
    }

    public int[][] getLeftPatternsFrom(Mino current) {
        return leftMap.get(current.getBlock()).get(current.getRotate()).getOffsets();
    }
}
