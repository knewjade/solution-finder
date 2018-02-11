package util.fig.output;

import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.fig.Bag;
import util.fig.generator.FigGenerator;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class GifWriter implements FigWriter {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final FigGenerator figGenerator;
    private final Bag bag;
    private final int nextBoxCount;
    private final int delay;
    private final File outputFile;
    private final boolean isInfiniteLoop;

    public GifWriter(MinoFactory minoFactory, ColorConverter colorConverter, FigGenerator figGenerator, Bag bag, int nextBoxCount, int delay, File outputFile, boolean isInfiniteLoop) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
        this.figGenerator = figGenerator;
        this.bag = bag;
        this.nextBoxCount = nextBoxCount;
        this.delay = delay;
        this.outputFile = outputFile;
        this.isInfiniteLoop = isInfiniteLoop;
    }

    @Override
    public void write(List<TetfuPage> tetfuPages) throws IOException {
        boolean isInfiniteLoop2 = isInfiniteLoop;
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputFile)) {
            // imageWriterの準備
            ImageWriter imageWriter = getGifImageWriter();
            imageWriter.setOutput(imageOutputStream);
            imageWriter.prepareWriteSequence(null);

            for (TetfuPage tetfuPage : tetfuPages) {
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

                // メタデータの作成
                IIOMetadata metadata = createMetadata(imageWriter, image, delay, isInfiniteLoop2);
                IIOImage iioImage = new IIOImage(image, null, metadata);
                imageWriter.writeToSequence(iioImage, null);

                // 無限ループの設定は最大1度までで十分
                isInfiniteLoop2 = false;
            }

            // imageWriterの終了処理
            imageWriter.endWriteSequence();
        }
    }

    private static ImageWriter getGifImageWriter() {
        Iterator<ImageWriter> writerIterator = ImageIO.getImageWritersByFormatName("gif");
        if (writerIterator.hasNext())
            return writerIterator.next();
        throw new IllegalStateException("No reachable");
    }

    private static IIOMetadata createMetadata(ImageWriter imageWriter, BufferedImage image, int delayTime, boolean isInfiniteLoop) throws IIOInvalidTreeException {
        // メタデータの作成
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageType = new ImageTypeSpecifier(image);
        IIOMetadata metadata = imageWriter.getDefaultImageMetadata(imageType, imageWriteParam);
        String metaFormat = metadata.getNativeMetadataFormatName();
        Node root = metadata.getAsTree(metaFormat);

        // delay timeの設定
        NodeList childNodes = root.getChildNodes();
        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);
            if (node.getNodeName().equalsIgnoreCase("GraphicControlExtension")) {
                // Get delay value
                NamedNodeMap namedNodeMap = node.getAttributes();
                Node delayTimeNode = namedNodeMap.getNamedItem("delayTime");
                if (delayTimeNode != null)
                    delayTimeNode.setNodeValue(String.valueOf(delayTime)); // 10ms単位
            }
        }

        // 無限ループの設定
        if (isInfiniteLoop)
            addInfiniteLoopMetaData(root);

        // メタデータのセット
        metadata.setFromTree(metadata.getNativeMetadataFormatName(), root);

        return metadata;
    }

    private static void addInfiniteLoopMetaData(Node root) {
        IIOMetadataNode aes = new IIOMetadataNode("ApplicationExtensions");
        IIOMetadataNode ae = new IIOMetadataNode("ApplicationExtension");
        ae.setAttribute("applicationID", "NETSCAPE");
        ae.setAttribute("authenticationCode", "2.0");
        byte[] uo = {
                // last two bytes is an unsigned short (little endian) that
                // indicates the the number of times to loop.
                // 0 means loop forever.
                0x1, 0x0, 0x0
        };
        ae.setUserObject(uo);
        aes.appendChild(ae);
        root.appendChild(aes);
    }
}
