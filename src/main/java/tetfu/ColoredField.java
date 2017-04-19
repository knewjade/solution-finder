package tetfu;

import core.mino.Block;
import core.mino.Mino;

// Blockの番号とColorの番号
public interface ColoredField {
    ColoredField freeze(int maxHeight);

    // Empty = 0, I = 1, L = 2, O = 3, Z = 4, T = 5, J = 6, S = 7, Gray = 8,
    int getBlockNumber(int x, int y);

    void setBlock(Block block, int x, int y);

    void putMino(Mino mino, int x, int y);

    void setBlockNumber(int x, int y, int number);
}
