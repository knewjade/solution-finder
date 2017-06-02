import common.tetfu.Tetfu;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

public class Main6 {
    public static final int FIELD_WIDTH = 128;
    private static int BLOCK_SIZE = 32;

    public static void main(String[] args) throws IOException {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
//        String code = "vhK2OJzkBifB3rBtqBUhB5oBdtBasBusBAAA";
        String code = "vhdSSY4AFLDmClcJSAVDEHBEooRBMoAVBvfjxCTuTW?CKejxCPXNFD0/rtC6COMCzvKWC0HkPCPONPCznBGjBUrBds?B3hBJkBXlBWvBRvBKqBUrBTpBTfB9tB0gBJnBusB6mBFtBX?sBSyBMoBJmB3iBumBNpBTqBSqBlpB";
        List<TetfuPage> decode = tetfu.decode(code);

        ImageWriter imageWriter = getGifImageWriter();

        File outputFile = new File("anime.gif");
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputFile)) {
            imageWriter.setOutput(imageOutputStream);
            imageWriter.prepareWriteSequence(null);

            boolean isFirst = true;

            for (TetfuPage tetfuPage : decode) {
                // 画像をつくる
                BufferedImage image = createBufferedImage(tetfuPage, minoFactory, colorConverter);

                //
                ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
                ImageTypeSpecifier imageType = new ImageTypeSpecifier(image);
                IIOMetadata metadata = imageWriter.getDefaultImageMetadata(imageType, imageWriteParam);
                String metaFormat = metadata.getNativeMetadataFormatName();
                Node root = metadata.getAsTree(metaFormat);
                if (isFirst)
                    addLoopMetaData(root);
                isFirst = false;

                NodeList childNodes = root.getChildNodes();
                for (int index = 0; index < childNodes.getLength(); index++) {
                    Node node = childNodes.item(index);
                    if (node.getNodeName().equalsIgnoreCase("GraphicControlExtension")) {
                        // Get delay value
                        NamedNodeMap namedNodeMap = node.getAttributes();
                        Node delayTime = namedNodeMap.getNamedItem("delayTime");
                        if (delayTime != null)
                            delayTime.setNodeValue("15"); // 10ms単位
                    }
                }
                metadata.setFromTree(metadata.getNativeMetadataFormatName(), root);

                IIOImage iioImage = new IIOImage(image, null, metadata);
                imageWriter.writeToSequence(iioImage, null);
            }

            // 終了処理
            imageWriter.endWriteSequence();
        }
    }

    private static ImageWriter getGifImageWriter() {
        Iterator<ImageWriter> writerIterator = ImageIO.getImageWritersByFormatName("gif");
        if (writerIterator.hasNext())
            return writerIterator.next();
        throw new IllegalStateException("No reachable");
    }

    private static Node addLoopMetaData(Node root) throws IIOInvalidTreeException {
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
        return root;
    }

    private static BufferedImage createBufferedImage(TetfuPage tetfuPage, MinoFactory minoFactory, ColorConverter colorConverter) {
        int blockSize = BLOCK_SIZE;
        int margin = 2;
        int size = blockSize + margin;

        int width = size * 10 + margin;
        int height = size * 4 + margin;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();

        // 全体を塗る
        graphics.setColor(MyColor.Line.getNormalColor());
        graphics.fillRect(0, 0, width, height);

        // ブロックを塗る
        ColoredField field = tetfuPage.getField();
        for (int yIndex = 0; yIndex < 4; yIndex++) {
            for (int x = 0; x < 10; x++) {
                ColorType type = field.getColorType(x, yIndex);
                MyColor myColor = MyColor.parse(type);
                if (field.isFilled(yIndex)) {
                    graphics.setColor(myColor.getStrongColor());
                } else {
                    graphics.setColor(myColor.getNormalColor());
                }
                int y = 4 - yIndex - 1;
                graphics.fillRect(x * size + margin, y * size + margin, blockSize, blockSize);
            }
        }

        // 手持ちを塗る
        ColorType colorType = tetfuPage.getColorType();
        if (colorType != ColorType.Empty) {
            Block block = colorConverter.parseToBlock(colorType);
            Rotate rotate = tetfuPage.getRotate();
            Mino mino = minoFactory.create(block, rotate);
            MyColor myColor = MyColor.parse(colorType);
            graphics.setColor(myColor.getStrongColor());
            int xIndex = tetfuPage.getX();
            int yIndex = tetfuPage.getY();
            for (int[] positions : mino.getPositions()) {
                int x = xIndex + positions[0];
                int y = 4 - (yIndex+ positions[1]) - 1 ;
                graphics.fillRect(x * size + margin, y * size + margin, blockSize, blockSize);
            }
        }

        return image;
    }

    private enum MyColor {
        Background(ColorType.Empty, Color.BLACK, Color.BLACK),
        Line(null, new Color(0x333333), new Color(0x333333)),
        I(ColorType.I, new Color(0x00999A), new Color(0x24CCCD)),
        T(ColorType.T, new Color(0x9B009B), new Color(0xCE27CE)),
        S(ColorType.S, new Color(0x009B00), new Color(0x26CE22)),
        Z(ColorType.Z, new Color(0x9B0000), new Color(0xCE312D)),
        L(ColorType.L, new Color(0x9A6700), new Color(0xCD9A24)),
        J(ColorType.J, new Color(0x0000BE), new Color(0x3229CF)),
        O(ColorType.O, new Color(0x999A00), new Color(0xCCCE19)),
        Gray(ColorType.Gray, new Color(0x999999), new Color(0xCCCCCC)),;

        private static final EnumMap<ColorType, MyColor> map = new EnumMap<>(ColorType.class);

        static {
            for (MyColor color : MyColor.values())
                if (color.type != null)
                    map.put(color.type, color);
        }

        private static MyColor parse(ColorType type) {
            assert map.containsKey(type);
            return map.get(type);
        }

        private final ColorType type;
        private final Color normal;
        private final Color strong;

        MyColor(ColorType type, Color normal, Color strong) {
            this.type = type;
            this.normal = normal;
            this.strong = strong;
        }

        public Color getNormalColor() {
            return normal;
        }

        public Color getStrongColor() {
            return strong;
        }
    }
}


