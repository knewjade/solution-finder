package tetfu.common;

import core.mino.Block;
import tetfu.common.ColorType;

import java.util.ArrayList;
import java.util.EnumMap;

public class ColorConverter {
    private final EnumMap<Block, ColorType> blockToColor;
    private final EnumMap<ColorType, Block> colorToBlock;
    private final ArrayList<ColorType> numberToColor;

    public ColorConverter() {
        blockToColor = new EnumMap<>(Block.class);
        colorToBlock = new EnumMap<>(ColorType.class);

        for (Block block : Block.values()) {
            String blockName = block.name();
            try {
                ColorType type = ColorType.valueOf(blockName);
                blockToColor.put(block, type);
                colorToBlock.put(type, block);
            } catch (IllegalArgumentException ignored) {
            }
        }

        assert blockToColor.size() == 7;
        assert colorToBlock.size() == 7;

        numberToColor = new ArrayList<>();
        for (int count = 0; count < ColorType.values().length; count++)
            numberToColor.add(null);
        for (ColorType type : ColorType.values())
            numberToColor.set(type.getNumber(), type);
    }

    public ColorType parseToColorType(Block block) {
        return blockToColor.get(block);
    }

    public Block parseToBlock(ColorType type) {
        assert ColorType.isBlock(type);
        return colorToBlock.get(type);
    }

    public ColorType parseToColorType(int number) {
        return numberToColor.get(number);
    }
}
