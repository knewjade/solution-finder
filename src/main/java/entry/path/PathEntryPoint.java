package entry.path;

import common.SyntaxException;
import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.pattern.BlocksGenerator;
import common.tetfu.common.ColorConverter;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import entry.DropType;
import entry.EntryPoint;
import entry.path.output.*;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.FilterOnDemandBasicSolutions;
import searcher.pack.task.BasicMinoPackingHelper;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.TaskResultHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class PathEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final StandardOpenOption[] FILE_OPEN_OPTIONS = new StandardOpenOption[]{
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE
    };

    private final PathSettings settings;
    private final BufferedWriter logWriter;

    public PathEntryPoint(PathSettings settings) throws FinderInitializeException {
        this.settings = settings;

        // ログファイルの出力先を整備
        String logFilePath = settings.getLogFilePath();
        File logFile = new File(logFilePath);

        // 親ディレクトリがない場合は作成
        if (!logFile.getParentFile().exists()) {
            boolean mairSuccess = logFile.getParentFile().mkdir();
            if (!mairSuccess) {
                throw new FinderInitializeException("Failed to make log directory: LogFilePath=" + logFilePath);
            }
        }

        if (logFile.isDirectory())
            throw new FinderInitializeException("Cannot specify directory as log file path: LogFilePath=" + logFilePath);
        if (logFile.exists() && !logFile.canWrite())
            throw new FinderInitializeException("Cannot write log file: LogFilePath=" + logFilePath);

        try {
            this.logWriter = Files.newBufferedWriter(logFile.toPath(), CHARSET, FILE_OPEN_OPTIONS);
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
    }

    @Override
    public void run() throws FinderException {
        output("# Setup Field");
        Field field = settings.getField();
        if (field == null)
            throw new FinderInitializeException("Should specify field");

        int maxClearLine = settings.getMaxClearLine();
        if (maxClearLine < 2 || 10 < maxClearLine)
            throw new FinderInitializeException("Clear-Line should be 2 <= line <= 10: line=" + maxClearLine);

        output(FieldView.toString(field, maxClearLine));

        BlockField reservedBlocks = settings.getReservedBlock();
        if (settings.isRevered()) {
            assert reservedBlocks != null;
            output("");
            System.out.println("# Setup Reserved blocks");
            output(BlockFieldView.toString(reservedBlocks));
        }

        output();
        // ========================================
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Drop: " + settings.getDropType().name().toLowerCase());
        output("Searching patterns:");
        List<String> patterns = settings.getPatterns();
        if (patterns.isEmpty())
            throw new FinderInitializeException("Should specify patterns, not allow empty");

        try {
            BlocksGenerator.verify(patterns);
        } catch (SyntaxException e) {
            output("Pattern syntax error");
            output(e.getMessage());
            throw new FinderInitializeException("Pattern syntax error", e);
        }

        for (String pattern : patterns)
            output("  " + pattern);

        output();
        // ========================================
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();

        output("Available processors = " + core);

        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getNumOfAllBlocks();
        if (emptyCount % 4 != 0)
            throw new FinderInitializeException("Empty block in field should be multiples of 4: EmptyCount=" + emptyCount);

        // ブロック数が足りないときはエラー
        int maxDepth = emptyCount / 4;
        BlocksGenerator generator = new BlocksGenerator(patterns);
        int piecesDepth = generator.getDepth();
        if (piecesDepth < maxDepth)
            throw new FinderInitializeException(String.format("Should specify equal to or more than %d pieces: CurrentPieces=%d", maxDepth, piecesDepth));

        output("Need Pieces = " + maxDepth);

        output();
        // ========================================
        output("# Enumerate pieces");

        // Holdができるときは必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        boolean isUsingHold = settings.isUsingHold();
        output("Piece pop count = " + (isUsingHold && maxDepth < generator.getDepth() ? maxDepth + 1 : maxDepth));

        // フォーマットを決める
        // 出力ファイルが正しく出力できるか確認も行う
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        OutputType outputType = settings.getOutputType();
        PathOutput pathOutput = createOutput(outputType, generator, maxDepth);

        // ミノのリストを作成する
        int basicSolutionWidth = decideBasicSolutionWidth(maxClearLine);
        SizedBit sizedBit = new SizedBit(basicSolutionWidth, maxClearLine);
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        SeparableMinos separableMinos = new SeparableMinos(factory.create());

        // 検索条件を決める
        SolutionFilter solutionFilter = new ForPathSolutionFilter(generator, maxClearLine);

        output();
        // ========================================
        output("# Cache");
        output("  -> Stopwatch start");

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();

        // 基本パターンを計算
        int cachedMinBit = settings.getCachedMinBit();
        Predicate<ColumnField> predicate = createPredicate(cachedMinBit);
        ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
        BasicSolutions basicSolutions = new FilterOnDemandBasicSolutions(separableMinos, sizedBit, maxOuterBoard, predicate, solutionFilter);

        output("     ... done");

        stopwatch1.stop();
        output("  -> Stopwatch stop : " + stopwatch1.toMessage(TimeUnit.MILLISECONDS));

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch2 = Stopwatch.createStartedStopwatch();

        // 探索して、列挙する準備を行う
        output("     ... packing");
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, field);
        TaskResultHelper taskResultHelper = createTaskResultHelper(maxClearLine);
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        ColorConverter colorConverter = new ColorConverter();
        FumenParser fumenParser = createFumenParser(settings.isTetfuSplit(), minoFactory, colorConverter);
        PathCore pathCore = createPathCore(patterns, maxDepth, isUsingHold, searcher, fumenParser, maxClearLine);

        List<PathPair> pathPairs = run(pathCore, field, sizedBit, reservedBlocks);

        output("     ... done");

        stopwatch2.stop();
        output("  -> Stopwatch stop : " + stopwatch2.toMessage(TimeUnit.MILLISECONDS));

        output();
        // ========================================
        output("# Output file");
        pathOutput.output(pathPairs, field, sizedBit);

        output();
        // ========================================
        output("# Finalize");
        output("done");

        flush();
    }

    private List<PathPair> run(PathCore pathCore, Field field, SizedBit sizedBit, BlockField blockField) throws FinderExecuteException {
        try {
            if (blockField == null)
                return pathCore.run(field, sizedBit);
            else
                return pathCore.run(field, sizedBit, blockField);
        } catch (InterruptedException | ExecutionException e) {
            throw new FinderExecuteException(e);
        }
    }

    private int decideBasicSolutionWidth(int maxClearLine) {
        return maxClearLine <= 4 ? 3 : 2;
    }

    private FumenParser createFumenParser(boolean isTetfuSplit, MinoFactory minoFactory, ColorConverter colorConverter) {
        if (isTetfuSplit)
            return new SequenceFumenParser(minoFactory, colorConverter);
        return new OneFumenParser(minoFactory, colorConverter);
    }

    private PathCore createPathCore(List<String> patterns, int maxDepth, boolean isUsingHold, PackSearcher searcher, FumenParser fumenParser, int maxClearLine) throws FinderInitializeException {
        ThreadLocal<BuildUpStream> threadLocalBuildUpStream = createBuildUpStreamThreadLocal(settings.getDropType(), maxClearLine);
        return new PathCore(patterns, searcher, maxDepth, isUsingHold, fumenParser, threadLocalBuildUpStream);
    }

    private ThreadLocal<BuildUpStream> createBuildUpStreamThreadLocal(DropType dropType, int maxClearLine) throws FinderInitializeException {
        switch (dropType) {
            case Softdrop:
                return new LockedBuildUpListUpThreadLocal(maxClearLine);
            case Harddrop:
                return new HarddropBuildUpListUpThreadLocal(maxClearLine);
        }
        throw new FinderInitializeException("Unsupport droptype: droptype=" + dropType);
    }

    private Predicate<ColumnField> createPredicate(int cachedMinBit) throws FinderInitializeException {
        if (cachedMinBit == 0)
            return columnField -> true;
        else if (0 < cachedMinBit)
            return BasicSolutions.createBitCountPredicate(cachedMinBit);
        throw new FinderInitializeException("Cached-min-bit should be 0 <= bit: bit=" + cachedMinBit);
    }

    private TaskResultHelper createTaskResultHelper(int height) {
        if (height == 4)
            return new Field4x10MinoPackingHelper();
        return new BasicMinoPackingHelper();
    }

    private PathOutput createOutput(OutputType outputType, BlocksGenerator generator, int maxDepth) throws FinderExecuteException, FinderInitializeException {
        switch (outputType) {
            case CSV:
                return new CSVPathOutput(this, settings);
            case Link:
                return new LinkPathOutput(this, settings);
            case TetfuCSV:
                return new FumenCSVPathOutput(this, settings);
            case PatternCSV:
                return new PatternCSVPathOutput(this, settings, generator, maxDepth);
            case UseCSV:
                return new UseCSVPathOutput(this, settings, generator, maxDepth);
            default:
                throw new FinderExecuteException("Unsupported format: format=" + outputType);
        }
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
            logWriter.close();
        } catch (IOException e) {
            throw new FinderTerminateException(e);
        }
    }
}
