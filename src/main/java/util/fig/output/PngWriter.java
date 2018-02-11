package util.fig.output;

import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;
import util.fig.Bag;
import util.fig.generator.FigGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PngWriter implements FigWriter {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final FigGenerator figGenerator;
    private final Bag bag;
    private final int nextBoxCount;
    private final String prefix;
    private final int startPageIndex;

    public PngWriter(MinoFactory minoFactory, ColorConverter colorConverter, FigGenerator figGenerator, Bag bag, int nextBoxCount, String prefix, int startPageIndex) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
        this.figGenerator = figGenerator;
        this.bag = bag;
        this.nextBoxCount = nextBoxCount;
        this.prefix = prefix;
        this.startPageIndex = startPageIndex;
    }

    @Override
    public void write(List<TetfuPage> tetfuPages) throws IOException {
        int page = startPageIndex + 1;

        for (TetfuPage tetfuPage : tetfuPages) {
            String path = String.format("%s_%03d.png", prefix, page);

            // リセット
            figGenerator.reset();

            // 現在のミノを取得
            ColorType colorType = tetfuPage.getColorType();
            Rotate rotate = tetfuPage.getRotate();
            Mino mino = ColorType.isMinoBlock(colorType) ? minoFactory.create(colorConverter.parseToBlock(colorType), rotate) : null;

            int x = tetfuPage.getX();
            int y = tetfuPage.getY();

            // フィールドの更新
            ColoredField field = tetfuPage.getField();
            figGenerator.updateField(field, mino, x, y);

            // ミノを置くかチェック
            if (ColorType.isMinoBlock(colorType)) {
                // 現在のミノの更新
                figGenerator.updateMino(colorType, rotate, x, y);

                // bagの更新
                Piece piece = colorConverter.parseToBlock(colorType);
                bag.use(piece);
            }

            // ネクストの更新
            figGenerator.updateNext(bag.getNext(nextBoxCount));

            // ホールドの更新
            figGenerator.updateHold(bag.getHold());

            // 画像の生成
            BufferedImage image = figGenerator.fix();

            // 画像の出力
            ImageIO.write(image, "png", new File(path));

            page++;
        }
    }
}
