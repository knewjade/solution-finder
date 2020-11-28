package util.fig.generator;

import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;
import util.fig.FigColor;
import util.fig.FigColors;
import util.fig.FigSetting;
import util.fig.Rectangle;
import util.fig.position.FieldOnlyPositionDecider;
import util.fig.position.PositionDecider;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class FieldOnlyFigGenerator implements FigGenerator {
    private final FigSetting setting;
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final PositionDecider positionDecider;
    private final BufferedImage image;
    private final Graphics2D graphics;
    private final FigColors figColors;

    public FieldOnlyFigGenerator(FigSetting setting, FigColors figColors, MinoFactory minoFactory, ColorConverter colorConverter) {
        this.setting = setting;
        this.figColors = figColors;
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
        // Border
        graphics.setColor(figColors.line());
        Rectangle rectangle = positionDecider.getField();
        fillRect(rectangle);
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
                FigColor figColor = figColors.parse(type);
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

    @Override
    public void updateMino(ColorType colorType, Rotate rotate, int xIndex, int yIndex) {
        Piece piece = colorConverter.parseToBlock(colorType);
        Mino mino = minoFactory.create(piece, rotate);
        FigColor figColor = figColors.parse(colorType);
        Color color = figColor.getStrong2Color();
        graphics.setColor(color);
        for (int[] positions : mino.getPositions()) {
            Rectangle rectangle = positionDecider.getInField(xIndex + positions[0], yIndex + positions[1]);
            fillRect(rectangle);
        }
    }

    @Override
    public void updateNext(List<Piece> pieces) {
        // do nothing
    }

    @Override
    public void updateHold(Piece piece) {
        // do nothing
    }

    private Color getColor(FigColor figColor, boolean isFilledLine) {
        return isFilledLine ? figColor.getStrongColor() : figColor.getNormalColor();
    }

    @Override
    public BufferedImage fix() {
        return image;
    }
}
