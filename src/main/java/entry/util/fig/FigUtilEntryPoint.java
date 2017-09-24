package entry.util.fig;

import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.FinderConstant;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import entry.EntryPoint;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.fig.Bag;
import util.fig.FigSetting;
import util.fig.FrameType;
import util.fig.generator.AllFigGenerator;
import util.fig.generator.FieldOnlyFigGenerator;
import util.fig.generator.FigGenerator;
import util.fig.generator.NoHoldFigGenerator;
import util.fig.position.BasicPositionDecider;
import util.fig.position.RightPositionDecider;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public void run() throws FinderException {
        output("# Setup");
        output("Version = " + FinderConstant.VERSION);

        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();

        FrameType frameType = settings.getFrameType();

        File outputFile = new File(settings.getOutputFilePath());

        output();
        output("# Generate");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        FigFormat figFormat = settings.getFigFormat();

        generatorFigure(minoFactory, colorConverter, frameType, outputFile, figFormat);


        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));
    }

    private void generatorFigure(MinoFactory minoFactory, ColorConverter colorConverter, FrameType frameType, File outputFile, FigFormat figFormat) throws FinderException {
        try {
            switch (figFormat) {
                case Gif:
                    createGif(minoFactory, colorConverter, frameType, outputFile);
                    break;
                case Png:
                    createPng(minoFactory, colorConverter, frameType);
                    break;
            }
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }
    }

    private void createGif(MinoFactory minoFactory, ColorConverter colorConverter, FrameType frameType, File originalOutputFile) throws FinderException {
        String outputFilePath = getRemoveExtensionFromPath(getCanonicalPath(originalOutputFile));
        if (outputFilePath.isEmpty())
            outputFilePath = "fig";
        outputFilePath += ".gif";

        File outputFile = new File(outputFilePath);
        if (outputFile.isDirectory())
            throw new FinderInitializeException("Cannot specify directory as output file path: Output=" + settings.getOutputFilePath());
        if (outputFile.exists() && !outputFile.canWrite())
            throw new FinderInitializeException("Cannot write output file: Output=" + settings.getOutputFilePath());

        output("  .... Output to " + getCanonicalPath(outputFile));
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputFile)) {
            // imageWriterの準備
            ImageWriter imageWriter = getGifImageWriter();
            imageWriter.setOutput(imageOutputStream);
            imageWriter.prepareWriteSequence(null);

            // generatorの準備
            boolean usingHold = settings.isUsingHold();
            FigGenerator figGenerator = createFigGenerator(frameType, usingHold, minoFactory, colorConverter);

            // 開始ページまでにQuizが含まれているかを確認する
            int startPageIndex = settings.getStartPageIndex();
            List<TetfuPage> tetfuPages = settings.getTetfuPages();
            String quiz = null;
            int quizIndex = -1;
            for (int index = startPageIndex; 0 <= index; index--) {
                TetfuPage tetfuPage = tetfuPages.get(index);
                String comment = tetfuPage.getComment();
                if (comment.startsWith("#Q=")) {
                    quiz = comment;
                    quizIndex = index;
                    break;
                }
            }

            // Bagの作成
            int endPage = settings.getEndPage();
            List<TetfuPage> usingTetfuPages = tetfuPages.subList(startPageIndex, endPage);
            Bag bag = createBag(colorConverter, startPageIndex, tetfuPages, quiz, quizIndex, usingTetfuPages);

            // もし開始ページ以降にQuizが含まれるときは無視することを警告
            if (tetfuPages.subList(startPageIndex + 1, endPage).stream().map(TetfuPage::getComment).anyMatch(s -> s.startsWith("#Q="))) {
                output("#### WARNING: Contains Quiz in tetfu after start page. ignored");
            }

            // 必要な回数だけ出力する
            boolean isInfiniteLoop = settings.getInfiniteLoop();
            int nextBoxCount = settings.getNextBoxCount();
            for (TetfuPage tetfuPage : usingTetfuPages) {
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
                    Block block = colorConverter.parseToBlock(colorType);
                    bag.use(block);
                }

                // ネクストの更新
                figGenerator.updateNext(bag.getNext(nextBoxCount));

                // ホールドの更新
                figGenerator.updateHold(bag.getHold());

                // 画像の生成
                BufferedImage image = figGenerator.fix();

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
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }
    }

    private String getCanonicalPath(File file) throws FinderInitializeException {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
    }

    private void createPng(MinoFactory minoFactory, ColorConverter colorConverter, FrameType frameType) throws IOException, FinderException {
        // 日付から新しいディレクトリ名を生成
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateDirName = format.format(date);

        // 出力先の親ディレクトリを取得
        File originOutputFile = new File(settings.getOutputFilePath());
        File parentDirectory = originOutputFile.getParentFile();

        // 出力先ディレクトリを作成
        String baseName = parentDirectory.getCanonicalPath() + File.separatorChar + dateDirName;
        File outputDirectoryFile = new File(baseName);
        for (int suffix = 0; outputDirectoryFile.exists(); suffix++) {
            outputDirectoryFile = new File(baseName + "_" + suffix);
        }

        // ファイル名の取得
        String outputFileName = getRemoveExtensionFromPath(originOutputFile.getName());
        if (outputFileName.isEmpty())
            outputFileName = "fig";

        // 出力先ディレクトリがない場合は作成
        if (!outputDirectoryFile.exists()) {
            boolean mkdirsSuccess = outputDirectoryFile.mkdirs();
            if (!mkdirsSuccess) {
                throw new FinderInitializeException("Failed to make output directory: OutputFilePath=" + originOutputFile.getName());
            }
        }

        output("  .... Output to " + outputDirectoryFile.getCanonicalPath());

        // generatorの準備
        boolean usingHold = settings.isUsingHold();
        FigGenerator figGenerator = createFigGenerator(frameType, usingHold, minoFactory, colorConverter);

        // 開始ページまでにQuizが含まれているかを確認する
        int startPageIndex = settings.getStartPageIndex();
        List<TetfuPage> tetfuPages = settings.getTetfuPages();
        String quiz = null;
        int quizIndex = -1;
        for (int index = startPageIndex; 0 <= index; index--) {
            TetfuPage tetfuPage = tetfuPages.get(index);
            String comment = tetfuPage.getComment();
            if (comment.startsWith("#Q=")) {
                quiz = comment;
                quizIndex = index;
                break;
            }
        }

        // Bagの作成
        int endPage = settings.getEndPage();
        List<TetfuPage> usingTetfuPages = tetfuPages.subList(startPageIndex, endPage);
        Bag bag = createBag(colorConverter, startPageIndex, tetfuPages, quiz, quizIndex, usingTetfuPages);

        // もし開始ページ以降にQuizが含まれるときは無視することを警告
        if (tetfuPages.subList(startPageIndex + 1, endPage).stream().map(TetfuPage::getComment).anyMatch(s -> s.startsWith("#Q="))) {
            output("#### WARNING: Contains Quiz in tetfu after start page. ignored");
        }

        // 必要な回数だけ出力する
        int page = startPageIndex + 1;
        int nextBoxCount = settings.getNextBoxCount();
        for (TetfuPage tetfuPage : usingTetfuPages) {
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
                Block block = colorConverter.parseToBlock(colorType);
                bag.use(block);
            }

            // ネクストの更新
            figGenerator.updateNext(bag.getNext(nextBoxCount));

            // ホールドの更新
            figGenerator.updateHold(bag.getHold());

            // 画像の生成
            BufferedImage image = figGenerator.fix();

            // 画像の出力
            String path = String.format("%s" + File.separatorChar + "%s_%03d.png", outputDirectoryFile.getCanonicalPath(), outputFileName, page);
            ImageIO.write(image, "png", new File(path));

            page++;
        }
    }

    private String getRemoveExtensionFromPath(String path) {
        int pointIndex = path.lastIndexOf('.');
        int separatorIndex = path.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return path;

        // .があるとき
        if (pointIndex != -1)
            return path.substring(0, pointIndex);

        return path;
    }

    private Bag createBag(ColorConverter colorConverter, int startPageIndex, List<TetfuPage> tetfuPages, String quiz, int quizIndex, List<TetfuPage> usingTetfuPages) {
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
            for (int index = quizIndex; index < startPageIndex; index++) {
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
    public void close() throws FinderTerminateException {
    }

    private FigGenerator createFigGenerator(FrameType frameType, boolean isUsingHold, MinoFactory minoFactory, ColorConverter colorConverter) {
        int height = settings.getHeight();
        int nextBoxCount = settings.getNextBoxCount();
        assert 0 <= nextBoxCount;

        FigSetting figSetting = new FigSetting(frameType, height, nextBoxCount);
        switch (frameType) {
            case NoFrame:
                return new FieldOnlyFigGenerator(figSetting, minoFactory, colorConverter);
            case Basic:
                if (!isUsingHold)
                    return new NoHoldFigGenerator(figSetting, minoFactory, colorConverter);

                BasicPositionDecider basicPositionDecider = new BasicPositionDecider(figSetting);
                return new AllFigGenerator(figSetting, basicPositionDecider, minoFactory, colorConverter);
            case Right:
                if (!isUsingHold)
                    return new NoHoldFigGenerator(figSetting, minoFactory, colorConverter);

                RightPositionDecider rightPositionDecider = new RightPositionDecider(figSetting);
                return new AllFigGenerator(figSetting, rightPositionDecider, minoFactory, colorConverter);
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

    private void output() {
        output("");
    }

    private void output(String str) {
        System.out.println(str);
    }
}
