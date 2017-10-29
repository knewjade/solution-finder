package common.tetfu.common;

import core.mino.Piece;

import java.util.ArrayList;
import java.util.EnumMap;

public class ColorConverter {
    private final EnumMap<Piece, ColorType> blockToColor;
    private final EnumMap<ColorType, Piece> colorToBlock;
    private final ArrayList<ColorType> numberToColor;

    public ColorConverter() {
        blockToColor = new EnumMap<>(Piece.class);
        colorToBlock = new EnumMap<>(ColorType.class);

        for (Piece piece : Piece.values()) {
            String blockName = piece.name();
            try {
                ColorType type = ColorType.valueOf(blockName);
                blockToColor.put(piece, type);
                colorToBlock.put(type, piece);
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

    public ColorType parseToColorType(Piece piece) {
        return blockToColor.get(piece);
    }

    public Piece parseToBlock(ColorType type) {
        assert ColorType.isMinoBlock(type) : type;
        return colorToBlock.get(type);
    }

    public ColorType parseToColorType(int number) {
        return numberToColor.get(number);
    }
}
