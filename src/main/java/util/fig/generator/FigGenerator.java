package util.fig.generator;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;

import java.awt.image.BufferedImage;
import java.util.List;

public interface FigGenerator {
    void reset();

    void updateField(ColoredField field, Mino mino, int x, int y);

    void updateMino(ColorType colorType, Rotate rotate, int xIndex, int yIndex);

    void updateNext(List<Block> blocks);

    void updateHold(Block block);

    BufferedImage fix();
}
