package entry.util.fig;

import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import core.FinderConstant;
import core.mino.MinoFactory;
import core.mino.Piece;
import entry.EntryPoint;
import entry.path.output.MyFile;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import util.fig.Bag;
import util.fig.FigColors;
import util.fig.FigSetting;
import util.fig.FrameType;
import util.fig.generator.AllFigGenerator;
import util.fig.generator.FieldOnlyFigGenerator;
import util.fig.generator.FigGenerator;
import util.fig.generator.NoHoldFigGenerator;
import util.fig.output.FigWriter;
import util.fig.output.GifWriter;
import util.fig.output.PngWriter;
import util.fig.position.BasicPositionDecider;
import util.fig.position.RightPositionDecider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FigUtilEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final FigUtilSettings settings;
    private final BufferedWriter logWriter;

    public FigUtilEntryPoint(FigUtilSettings settings) throws FinderInitializeException {
        settings.adjust();
        this.settings = settings;

        // ログファイルの出力先を整備
        String logFilePath = settings.getLogFilePath();
        MyFile logFile = new MyFile(logFilePath);

        logFile.mkdirs();
        logFile.verify();

        try {
            this.logWriter = logFile.newBufferedWriter();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
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

        List<TetfuPage> usingTetfuPages = useTetfuPages();
        FigFormat figFormat = settings.getFigFormat();
        FigWriter figWriter = createFigWriter(minoFactory, colorConverter, frameType, outputFile, figFormat, usingTetfuPages);
        generate(figWriter, usingTetfuPages);

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));
    }

    private void generate(FigWriter figWriter, List<TetfuPage> usingTetfuPages) throws FinderException {
        try {
            figWriter.write(usingTetfuPages);
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }
    }

    private List<TetfuPage> useTetfuPages() {
        List<TetfuPage> tetfuPages = settings.getTetfuPages();
        int startPageIndex = settings.getStartPageIndex();
        int endPage = settings.getEndPage();
        return tetfuPages.subList(startPageIndex, endPage);
    }

    private FigWriter createFigWriter(MinoFactory minoFactory, ColorConverter colorConverter, FrameType frameType, File outputFile, FigFormat figFormat, List<TetfuPage> usingTetfuPages) throws FinderException {
        switch (figFormat) {
            case Gif:
                return createGif(minoFactory, colorConverter, frameType, outputFile, usingTetfuPages);
            case Png:
                return createPng(minoFactory, colorConverter, frameType, usingTetfuPages);
        }
        throw new FinderExecuteException("No support fig format");
    }

    private FigWriter createGif(MinoFactory minoFactory, ColorConverter colorConverter, FrameType frameType, File originalOutputFile, List<TetfuPage> usingTetfuPages) throws FinderException {
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
        Quiz quiz = parseQuiz();

        // カラーテーマの読み込み
        Properties colorThemeProperties = readProperties(settings.getColorTheme());

        // generatorの準備
        boolean usingHold = settings.isUsingHold();
        FigGenerator figGenerator = createFigGenerator(
                frameType, usingHold, minoFactory, colorConverter, colorThemeProperties
        );

        // Bagの作成
        List<TetfuPage> tetfuPages = settings.getTetfuPages();
        int startPageIndex = settings.getStartPageIndex();
        int endPage = settings.getEndPage();
        Bag bag = createBag(colorConverter, startPageIndex, tetfuPages, quiz, usingTetfuPages);

        // もし開始ページ以降にQuizが含まれるときは無視することを警告
        if (tetfuPages.subList(startPageIndex + 1, endPage).stream().map(TetfuPage::getComment).anyMatch(s -> s.startsWith("#Q="))) {
            output("#### WARNING: Contains Quiz in tetfu after start page. ignored");
        }

        int nextBoxCount = settings.getNextBoxCount();
        int delay = settings.getDelay();
        boolean isInfiniteLoop = settings.getInfiniteLoop();

        return new GifWriter(minoFactory, colorConverter, figGenerator, bag, nextBoxCount, delay, outputFile, isInfiniteLoop);
    }

    private Properties readProperties(String name) throws FinderInitializeException {
        Properties colorThemeProperties = new Properties();
        String path = String.format("theme/%s.properties", name);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            colorThemeProperties.load(reader);
        } catch (NoSuchFileException e) {
            throw new FinderInitializeException("Not found color theme", e);
        } catch (IOException e) {
            throw new FinderInitializeException("Occur error when read color theme", e);
        }
        return colorThemeProperties;
    }

    private Quiz parseQuiz() {
        // 開始ページまでにQuizが含まれているかを確認する
        int startPageIndex = settings.getStartPageIndex();
        List<TetfuPage> tetfuPages = settings.getTetfuPages();
        Quiz quiz = Quiz.EMPTY;
        for (int index = startPageIndex; 0 <= index; index--) {
            TetfuPage tetfuPage = tetfuPages.get(index);
            String comment = tetfuPage.getComment();
            if (comment.startsWith("#Q="))
                return new Quiz(comment, index);
        }
        return quiz;
    }

    private Bag createBag(ColorConverter colorConverter, int startPageIndex, List<TetfuPage> tetfuPages, Quiz quiz, List<TetfuPage> usingTetfuPages) {
        if (quiz.isValid()) {
            String comment = quiz.comment;
            int holdIndex = comment.indexOf('[') + 1;
            char holdChar = comment.charAt(holdIndex);
            Piece hold = null;
            if (holdChar != ']')
                hold = Piece.valueOf(String.valueOf(holdChar).toUpperCase());

            int currentIndex = comment.indexOf('(') + 1;
            int currentChar = comment.charAt(currentIndex);
            String next = comment.substring(comment.indexOf(')') + 1);
            List<Piece> pieces = IntStream.concat(IntStream.of(currentChar), next.chars())
                    .mapToObj(value -> (char) value)
                    .map(String::valueOf)
                    .map(String::toUpperCase)
                    .map(Piece::valueOf)
                    .collect(Collectors.toList());

            Bag bag = new Bag(pieces, hold);
            for (int index = quiz.index; index < startPageIndex; index++) {
                ColorType colorType = tetfuPages.get(index).getColorType();
                bag.use(colorConverter.parseToBlock(colorType));
            }
            return bag;
        } else {
            List<Piece> collect = usingTetfuPages.stream()
                    .map(TetfuPage::getColorType)
                    .filter(ColorType::isMinoBlock)
                    .map(colorConverter::parseToBlock)
                    .collect(Collectors.toList());
            return new Bag(collect, null);
        }
    }

    private FigWriter createPng(MinoFactory minoFactory, ColorConverter colorConverter, FrameType frameType, List<TetfuPage> usingTetfuPages) throws FinderException {
        // 日付から新しいディレクトリ名を生成
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateDirName = format.format(date);

        // 出力先の親ディレクトリを取得
        File originOutputFile = new File(settings.getOutputFilePath());
        File parentDirectory = originOutputFile.getParentFile();

        // 出力先ディレクトリを作成
        String baseName = getCanonicalPath(parentDirectory) + File.separatorChar + dateDirName;
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

        output("  .... Output to " + getCanonicalPath(outputDirectoryFile));
        Quiz quiz = parseQuiz();

        // カラーテーマの読み込み
        Properties colorThemeProperties = readProperties(settings.getColorTheme());

        // generatorの準備
        boolean usingHold = settings.isUsingHold();
        FigGenerator figGenerator = createFigGenerator(
                frameType, usingHold, minoFactory, colorConverter, colorThemeProperties
        );

        // Bagの作成
        List<TetfuPage> tetfuPages = settings.getTetfuPages();
        int startPageIndex = settings.getStartPageIndex();
        int endPage = settings.getEndPage();
        Bag bag = createBag(colorConverter, startPageIndex, tetfuPages, quiz, usingTetfuPages);

        // もし開始ページ以降にQuizが含まれるときは無視することを警告
        if (tetfuPages.subList(startPageIndex + 1, endPage).stream().map(TetfuPage::getComment).anyMatch(s -> s.startsWith("#Q="))) {
            output("#### WARNING: Contains Quiz in tetfu after start page. ignored");
        }

        String path = String.format("%s" + File.separatorChar + "%s", getCanonicalPath(outputDirectoryFile), outputFileName);

        int nextBoxCount = settings.getNextBoxCount();
        return new PngWriter(minoFactory, colorConverter, figGenerator, bag, nextBoxCount, path, startPageIndex);
    }

    private String getCanonicalPath(File file) throws FinderInitializeException {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
    }

    private String getRemoveExtensionFromPath(String path) {
        int pointIndex = path.lastIndexOf('.');
        int separatorIndex = path.lastIndexOf(File.separatorChar);

        // .がない or セパレータより前にあるとき
        if (pointIndex <= separatorIndex)
            return path;

        // .があるとき
        return path.substring(0, pointIndex);
    }

    private FigGenerator createFigGenerator(
            FrameType frameType, boolean isUsingHold, MinoFactory minoFactory, ColorConverter colorConverter,
            Properties colorThemeProperties
    ) {
        int height = settings.getHeight();
        int nextBoxCount = settings.getNextBoxCount();
        assert 0 <= nextBoxCount;

        FigSetting figSetting = new FigSetting(frameType, height, nextBoxCount);
        FigColors figColors = new FigColors(colorThemeProperties);

        switch (frameType) {
            case NoFrame: {
                return new FieldOnlyFigGenerator(figSetting, figColors, minoFactory, colorConverter);
            }
            case Basic: {
                if (!isUsingHold)
                    return new NoHoldFigGenerator(figSetting, figColors, minoFactory, colorConverter);

                BasicPositionDecider basicPositionDecider = new BasicPositionDecider(figSetting);
                return new AllFigGenerator(figSetting, basicPositionDecider, figColors, minoFactory, colorConverter);
            }
            case Right: {
                if (!isUsingHold)
                    return new NoHoldFigGenerator(figSetting, figColors, minoFactory, colorConverter);

                RightPositionDecider rightPositionDecider = new RightPositionDecider(figSetting);
                return new AllFigGenerator(figSetting, rightPositionDecider, figColors, minoFactory, colorConverter);
            }
        }

        throw new IllegalStateException("No reachable");
    }

    private void output() throws FinderExecuteException {
        output("");
    }

    public void output(String str) throws FinderExecuteException {
        try {
            logWriter.append(str).append(LINE_SEPARATOR);
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }

        if (settings.isOutputToConsole())
            System.out.println(str);
    }

    private void flush() throws FinderExecuteException {
        try {
            logWriter.flush();
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }
    }

    @Override
    public void close() throws FinderTerminateException {
        try {
            flush();
            logWriter.close();
        } catch (IOException | FinderExecuteException e) {
            throw new FinderTerminateException(e);
        }
    }

    private static class Quiz {
        private static final Quiz EMPTY = new Quiz("", -1);

        private final String comment;
        private final int index;

        private Quiz(String comment, int index) {
            this.comment = comment;
            this.index = index;
        }

        public boolean isValid() {
            return comment != null && comment.startsWith("#Q=");
        }
    }
}


