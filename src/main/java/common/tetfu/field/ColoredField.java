package common.tetfu.field;

import core.mino.Mino;
import common.tetfu.common.ColorType;

// Blockの番号とColorの番号
public interface ColoredField {
    ColoredField freeze(int maxHeight);

    // Empty = 0, I = 1, L = 2, O = 3, Z = 4, T = 5, J = 6, S = 7, Gray = 8,
    int getBlockNumber(int x, int y);

    void putMino(Mino mino, int x, int y);

    void setBlockNumber(int x, int y, int number);

    void setColorType(ColorType colorType, int x, int y);

    void clearLine();

    void blockUp();

    void mirror();

    int getMaxHeight();
}
