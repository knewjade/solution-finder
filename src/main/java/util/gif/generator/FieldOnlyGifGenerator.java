package util.gif.generator;

import util.gif.GifColor;
import util.gif.GifSetting;
import util.gif.Rectangle;
import util.gif.position.FieldOnlyPositionDecider;
import util.gif.position.PositionDecider;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FieldOnlyGifGenerator implements GifGenerator {
    private final GifSetting setting;
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final PositionDecider positionDecider;
    private final BufferedImage image;
    private final Graphics2D graphics;

    public FieldOnlyGifGenerator(GifSetting setting, MinoFactory minoFactory, ColorConverter colorConverter) {
        this.setting = setting;
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
        this.positionDecider = new FieldOnlyPositionDecider(setting);

        int screenWidth = setting.getScreenWidth();
        int screenHeight = setting.getScreenHeight();
        this.image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        this.graphics = image.createGraphics();
    }

    @Override
    public void reset() {
        graphics.setColor(GifColor.Line.getNormalColor());
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    @Override
    public void updateField(ColoredField field, Mino mino, int x, int y) {
        ColoredField freeze = field.freeze(field.getMaxHeight());
        if (mino != null)
            freeze.putMino(mino, x, y);

        int heightBlock = setting.getFieldHeightBlock();
        int widthBlock = setting.getFieldWidthBlock();

        for (int yIndex = 0; yIndex < heightBlock; yIndex++) {
            boolean isFilledLine = freeze.isFilledLine(yIndex);
            for (int xIndex = 0; xIndex < widthBlock; xIndex++) {
                ColorType type = field.getColorType(xIndex, yIndex);
                GifColor gifColor = GifColor.parse(type);
                Color color = getColor(gifColor, isFilledLine);
                graphics.setColor(color);

                Rectangle rectangle = positionDecider.getInField(xIndex, yIndex);
                fillRect(rectangle);
            }
        }
    }

    private void fillRect(Rectangle rectangle) {
        graphics.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public void updateMino(ColorType colorType, Rotate rotate, int xIndex, int yIndex) {
        Block block = colorConverter.parseToBlock(colorType);
        Mino mino = minoFactory.create(block, rotate);
        GifColor gifColor = GifColor.parse(colorType);
        Color color = gifColor.getStrongColor();
        graphics.setColor(color);
        for (int[] positions : mino.getPositions()) {
            Rectangle rectangle = positionDecider.getInField(xIndex + positions[0], yIndex + positions[1]);
            fillRect(rectangle);
        }
    }

    @Override
    public void updateNext(List<Block> blocks) {
        // do nothing
    }

    @Override
    public void updateHold(Block block) {
        // do nothing
    }

    private Color getColor(GifColor gifColor, boolean isFilledLine) {
        return isFilledLine ? gifColor.getStrongColor() : gifColor.getNormalColor();
    }

    @Override
    public BufferedImage fix() {
        return image;
    }
}
