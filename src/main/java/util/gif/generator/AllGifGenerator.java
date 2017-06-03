package util.gif.generator;

import util.gif.GifColor;
import util.gif.GifSetting;
import util.gif.Rectangle;
import util.gif.position.PositionDecider;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class AllGifGenerator implements GifGenerator {
    private final GifSetting setting;
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final PositionDecider positionDecider;
    private final BufferedImage image;
    private final Graphics2D graphics;

    public AllGifGenerator(GifSetting setting, PositionDecider positionDecider, MinoFactory minoFactory, ColorConverter colorConverter) {
        this.setting = setting;
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
        this.positionDecider = positionDecider;

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
    public void updateField(ColoredField field) {
        int heightBlock = setting.getFieldHeightBlock();
        int widthBlock = setting.getFieldWidthBlock();

        for (int yIndex = 0; yIndex < heightBlock; yIndex++) {
            boolean isFilledLine = field.isFilledLine(yIndex);
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

    private void drawRoundRect(Rectangle rectangle, int arc) {
        graphics.drawRoundRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), arc, arc);
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
        Color color = new Color(0x999999);
        int nextBoxCount = setting.geNextBoxCount() < blocks.size() ? setting.geNextBoxCount() : blocks.size();

        assert nextBoxCount <= blocks.size();

        for (int index = 0; index < nextBoxCount; index++) {
            Rectangle rectangle = positionDecider.getNext(index);
            graphics.setColor(GifColor.Background.getNormalColor());
            fillRect(rectangle);

            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2.0f));
            drawRoundRect(rectangle, 2);

            drawMino(blocks.get(index), rectangle);
        }
    }

    private void drawMino(Block block, Rectangle rectangle) {
        if (block == null)
            return;

        ColorType colorType = colorConverter.parseToColorType(block);
        GifColor gifColor = GifColor.parse(colorType);
        Color color = getColor(gifColor, true);
        graphics.setColor(color);

        Mino mino = minoFactory.create(block, Rotate.Spawn);
        int maxX = mino.getMaxX();
        int minX = mino.getMinX();
        double mx = (maxX - minX + 1) / 2.0 + minX;

        int minY = -mino.getMaxY();
        int maxY = -mino.getMinY();
        double my = (maxY - minY + 1) / 2.0 + minY;

        int nextBlockSize = setting.getNextBlockSize();
        int nextBlockMargin = setting.getNextBlockMargin();
        int size = nextBlockSize + nextBlockMargin;
        double centerX = rectangle.getX() + rectangle.getWidth() / 2.0;
        double centerY = rectangle.getY() + rectangle.getHeight() / 2.0;
        for (int[] position : mino.getPositions()) {
            int x = (int) (centerX + (position[0] - mx) * size + nextBlockMargin / 2.0);
            int y = (int) (centerY - (position[1] + my) * size + nextBlockMargin / 2.0);
            graphics.fillRect(x, y, nextBlockSize, nextBlockSize);
        }
    }

    @Override
    public void updateHold(Block block) {
        Color color = new Color(0xdddddd);
        Rectangle rectangle = positionDecider.getHold();
        graphics.setColor(GifColor.Background.getNormalColor());
        fillRect(rectangle);

        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(2.0f));
        drawRoundRect(rectangle, 0);

        drawMino(block, rectangle);
    }

    private Color getColor(GifColor gifColor, boolean isFilledLine) {
        return isFilledLine ? gifColor.getStrongColor() : gifColor.getNormalColor();
    }

    @Override
    public BufferedImage fix() {
        return image;
    }
}
