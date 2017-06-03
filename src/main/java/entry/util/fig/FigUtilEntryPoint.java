package entry.util.fig;

import common.Stopwatch;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Block;
import core.mino.MinoFactory;
import core.srs.Rotate;
import entry.EntryPoint;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.gif.Bag;
import util.gif.FrameType;
import util.gif.GifSetting;
import util.gif.generator.AllGifGenerator;
import util.gif.generator.FieldOnlyGifGenerator;
import util.gif.generator.GifGenerator;
import util.gif.generator.NoHoldGifGenerator;
import util.gif.position.BasicPositionDecider;
import util.gif.position.RightPositionDecider;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FigUtilEntryPoint implements EntryPoint {
    private final FigUtilSettings settings;

    public FigUtilEntryPoint(FigUtilSettings settings) {
        settings.adjust();
        this.settings = settings;
    }

    @Override
    public void run() throws Exception {
        output("# Setup");
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();

        FrameType frameType = settings.getFrameType();

        boolean usingHold = settings.isUsingHold();
        boolean isInfiniteLoop = settings.getInfiniteLoop();
        File outputFile = new File(settings.getOutputFilePath());

        output();
        output("# Generate");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputFile)) {
            // imageWriterの準備
            ImageWriter imageWriter = getGifImageWriter();
            imageWriter.setOutput(imageOutputStream);
            imageWriter.prepareWriteSequence(null);

            // generatorの準備
            GifGenerator gifGenerator = createGifGenerator(frameType, usingHold, minoFactory, colorConverter);

            int startPage = settings.getStartPage();
            List<TetfuPage> tetfuPages = settings.getTetfuPages();
            String quiz = null;
            int quizIndex = -1;
            for (int index = startPage - 1; 0 <= index; index--) {
                TetfuPage tetfuPage = tetfuPages.get(index);
                String comment = tetfuPage.getComment();
                if (comment.startsWith("#Q=")) {
                    quiz = comment;
                    quizIndex = index;
                    break;
                }
            }

            int endPage = settings.getEndPage();
            List<TetfuPage> usingTetfuPages = tetfuPages.subList(startPage - 1, endPage);
            Bag bag = createBag(colorConverter, startPage, tetfuPages, quiz, quizIndex, usingTetfuPages);

            if (usingTetfuPages.stream().map(TetfuPage::getComment).anyMatch(s -> s.startsWith("#Q="))) {
                output("#### WARNING: Contains Quiz in tetfu after start page. ignored");
            }

            // 必要な回数だけ出力する
            int nextBoxCount = settings.getNextBoxCount();
            for (TetfuPage tetfuPage : usingTetfuPages) {
                // リセット
                gifGenerator.reset();

                // フィールドの更新
                ColoredField field = tetfuPage.getField();
                gifGenerator.updateField(field);

                // ミノを置くかチェック
                ColorType colorType = tetfuPage.getColorType();
                if (ColorType.isMinoBlock(colorType)) {
                    Rotate rotate = tetfuPage.getRotate();
                    int x = tetfuPage.getX();
                    int y = tetfuPage.getY();

                    // 現在のミノの更新
                    gifGenerator.updateMino(colorType, rotate, x, y);

                    // bagの更新
                    Block block = colorConverter.parseToBlock(colorType);
                    bag.use(block);
                }

                // ネクストの更新
                gifGenerator.updateNext(bag.getNext(nextBoxCount));

                // ホールドの更新
                gifGenerator.updateHold(bag.getHold());

                // 画像の出力
                BufferedImage image = gifGenerator.fix();

                // メタデータの作成
                int delay = settings.getDelay();
                IIOMetadata metadata = createMetadata(imageWriter, image, delay, isInfiniteLoop);
                IIOImage iioImage = new IIOImage(image, null, metadata);
                imageWriter.writeToSequence(iioImage, null);

                // 無限ループの設定は最大1度までで十分
                isInfiniteLoop = false;
            }

            // imageWriterの終了処理
            imageWriter.endWriteSequence();
        }

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));
    }

    private Bag createBag(ColorConverter colorConverter, int startPage, List<TetfuPage> tetfuPages, String quiz, int quizIndex, List<TetfuPage> usingTetfuPages) {
        if (settings.isUsingHold() && quiz != null) {
            int holdIndex = quiz.indexOf('[') + 1;
            char holdChar = quiz.charAt(holdIndex);
            Block hold = null;
            if (holdChar != ']')
                hold = Block.valueOf(String.valueOf(holdChar).toUpperCase());

            int currentIndex = quiz.indexOf('(') + 1;
            int currentChar = quiz.charAt(currentIndex);
            String next = quiz.substring(quiz.indexOf(')') + 1, quiz.length());
            List<Block> blocks = IntStream.concat(IntStream.of(currentChar), next.chars())
                    .mapToObj(value -> (char) value)
                    .map(String::valueOf)
                    .map(String::toUpperCase)
                    .map(Block::valueOf)
                    .collect(Collectors.toList());

            Bag bag = new Bag(blocks, hold);
            for (int index = quizIndex; index < startPage - 1; index++) {
                ColorType colorType = tetfuPages.get(index).getColorType();
                bag.use(colorConverter.parseToBlock(colorType));
            }
            return bag;
        } else {
            List<Block> collect = usingTetfuPages.stream()
                    .map(TetfuPage::getColorType)
                    .filter(ColorType::isMinoBlock)
                    .map(colorConverter::parseToBlock)
                    .collect(Collectors.toList());
            return new Bag(collect, null);
        }
    }

    @Override
    public void close() throws Exception {
    }

    private GifGenerator createGifGenerator(FrameType frameType, boolean isUsingHold, MinoFactory minoFactory, ColorConverter colorConverter) {
        int height = settings.getHeight();
        int nextBoxCount = settings.getNextBoxCount();
        if (nextBoxCount < 0)
            throw new IllegalArgumentException("Next Box Count should be positive");

        GifSetting gifSetting = new GifSetting(frameType, height, nextBoxCount);
        switch (frameType) {
            case NoFrame:
                return new FieldOnlyGifGenerator(gifSetting, minoFactory, colorConverter);
            case Basic:
                if (!isUsingHold)
                    return new NoHoldGifGenerator(gifSetting, minoFactory, colorConverter);

                BasicPositionDecider basicPositionDecider = new BasicPositionDecider(gifSetting);
                return new AllGifGenerator(gifSetting, basicPositionDecider, minoFactory, colorConverter);
            case Right:
                if (!isUsingHold)
                    return new NoHoldGifGenerator(gifSetting, minoFactory, colorConverter);

                RightPositionDecider rightPositionDecider = new RightPositionDecider(gifSetting);
                return new AllGifGenerator(gifSetting, rightPositionDecider, minoFactory, colorConverter);
        }
        throw new IllegalStateException("No reachable");
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

    private static void addInfiniteLoopMetaData(Node root) throws IIOInvalidTreeException {
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

    private void output() {
        output("");
    }

    private void output(String str) {
        System.out.println(str);
    }
}
