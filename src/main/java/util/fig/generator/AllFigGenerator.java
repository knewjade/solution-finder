package util.fig.generator;

import core.mino.Piece;
import util.fig.FigColor;
import util.fig.FigSetting;
import util.fig.Rectangle;
import util.fig.position.PositionDecider;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class AllFigGenerator implements FigGenerator {
    private final FigSetting setting;
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final PositionDecider positionDecider;
    private final BufferedImage image;
    private final Graphics2D graphics;

    public AllFigGenerator(FigSetting setting, PositionDecider positionDecider, MinoFactory minoFactory, ColorConverter colorConverter) {
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
        graphics.setColor(FigColor.Line.getNormalColor());
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
                FigColor figColor = FigColor.parse(type);
                Color color = getColor(figColor, isFilledLine);
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
        Piece piece = colorConverter.parseToBlock(colorType);
        Mino mino = minoFactory.create(piece, rotate);
        FigColor figColor = FigColor.parse(colorType);
        Color color = figColor.getStrongColor();
        graphics.setColor(color);
        for (int[] positions : mino.getPositions()) {
            Rectangle rectangle = positionDecider.getInField(xIndex + positions[0], yIndex + positions[1]);
            fillRect(rectangle);
        }
    }

    @Override
    public void updateNext(List<Piece> pieces) {
        Color color = new Color(0x999999);
        int nextBoxCount = setting.geNextBoxCount() < pieces.size() ? setting.geNextBoxCount() : pieces.size();

        assert nextBoxCount <= pieces.size();

        for (int index = 0; index < nextBoxCount; index++) {
            Rectangle rectangle = positionDecider.getNext(index);
            graphics.setColor(FigColor.Background.getNormalColor());
            fillRect(rectangle);

            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2.0f));
            drawRoundRect(rectangle, 2);

            drawMino(pieces.get(index), rectangle);
        }
    }

    private void drawMino(Piece piece, Rectangle rectangle) {
        if (piece == null)
            return;

        ColorType colorType = colorConverter.parseToColorType(piece);
        FigColor figColor = FigColor.parse(colorType);
        Color color = getColor(figColor, true);
        graphics.setColor(color);

        Mino mino = minoFactory.create(piece, Rotate.Spawn);
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
    public void updateHold(Piece piece) {
        Color color = new Color(0xdddddd);
        Rectangle rectangle = positionDecider.getHold();
        graphics.setColor(FigColor.Background.getNormalColor());
        fillRect(rectangle);

        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(2.0f));
        drawRoundRect(rectangle, 0);

        drawMino(piece, rectangle);
    }

    private Color getColor(FigColor figColor, boolean isFilledLine) {
        return isFilledLine ? figColor.getStrongColor() : figColor.getNormalColor();
    }

    @Override
    public BufferedImage fix() {
        return image;
    }
}
