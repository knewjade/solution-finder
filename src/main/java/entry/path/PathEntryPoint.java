package entry.path;

import common.SyntaxException;
import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.pattern.IBlocksGenerator;
import common.tetfu.common.ColorConverter;
import core.FinderConstant;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import entry.DropType;
import entry.EntryPoint;
import entry.Verify;
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
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class PathEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final PathSettings settings;
    private final BufferedWriter logWriter;

    public PathEntryPoint(PathSettings settings) throws FinderInitializeException {
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
        output("# Setup Field");

        // Setup field
        Field field = settings.getField();
        Verify.field(field);

        // Setup max clear line
        int maxClearLine = settings.getMaxClearLine();
        Verify.maxClearLineUnder10(maxClearLine);

        // Output field
        output(FieldView.toString(field, maxClearLine));

        // Setup reserved blocks
        BlockField reservedBlocks = settings.getReservedBlock();
        if (settings.isRevered()) {
            Verify.reservedBlocks(reservedBlocks);
            output("");
            output("# Setup Reserved blocks");
            output(BlockFieldView.toString(reservedBlocks));
        }

        // Setup space
        int emptyCount = Verify.emptyCount(field, maxClearLine);  // 残りのスペース
        int maxDepth = Verify.maxDepth(emptyCount);  // パフェに必要なミノ数

        output();

        // ========================================

        // Output user-defined
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Drop: " + settings.getDropType().name().toLowerCase());
        output("Searching patterns:");

        // Setup patterns
        List<String> patterns = settings.getPatterns();
        IBlocksGenerator generator = Verify.patterns(patterns, emptyCount, maxDepth);

        // Output patterns
        for (String pattern : patterns)
            output("  " + pattern);

        output();

        // ========================================

        // Setup core
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();

        // Output system-defined
        output("Version = " + FinderConstant.VERSION);
        output("Available processors = " + core);
        output("Need Pieces = " + maxDepth);

        output();

        // ========================================

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        ColorConverter colorConverter = new ColorConverter();
        SizedBit sizedBit = decideSizedBitSolutionWidth(maxClearLine);
        SolutionFilter solutionFilter = new ForPathSolutionFilter(generator, maxClearLine);

        // Holdができるときは必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        output("# Enumerate pieces");
        boolean isUsingHold = settings.isUsingHold();
        int piecesDepth = generator.getDepth();
        output("Piece pop count = " + (isUsingHold && maxDepth < piecesDepth ? maxDepth + 1 : maxDepth));

        output();

        // ========================================

        output("# Cache");
        output("  -> Stopwatch start");

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();
        BasicSolutions basicSolutions = calculateBasicSolutions(field, minoFactory, minoShifter, sizedBit, solutionFilter);
        stopwatch1.stop();

        output("     ... done");
        output("  -> Stopwatch stop : " + stopwatch1.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Search");
        output("  -> Stopwatch start");
        output("     ... searching");

        Stopwatch stopwatch2 = Stopwatch.createStartedStopwatch();
        PathCore pathCore = createPathCore(field, maxClearLine, maxDepth, patterns, minoFactory, colorConverter, sizedBit, solutionFilter, isUsingHold, basicSolutions);
        List<PathPair> pathPairs = run(pathCore, field, sizedBit, reservedBlocks);
        stopwatch2.stop();

        output("     ... done");
        output("  -> Stopwatch stop : " + stopwatch2.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output file");
        OutputType outputType = settings.getOutputType();
        PathOutput pathOutput = createOutput(outputType, generator, maxDepth);
        pathOutput.output(pathPairs, field, sizedBit);

        output();

        // ========================================

        output("# Finalize");
        output("done");
    }

    private SizedBit decideSizedBitSolutionWidth(int maxClearLine) {
        return maxClearLine <= 4 ? new SizedBit(3, maxClearLine) : new SizedBit(2, maxClearLine);
    }

    private BasicSolutions calculateBasicSolutions(Field field, MinoFactory minoFactory, MinoShifter minoShifter, SizedBit sizedBit, SolutionFilter solutionFilter) throws FinderInitializeException {
        // ミノのリストを作成する
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        SeparableMinos separableMinos = new SeparableMinos(factory.create());

        // 基本パターンを計算
        int cachedMinBit = settings.getCachedMinBit();
        Predicate<ColumnField> predicate = createPredicate(cachedMinBit);
        ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
        return new FilterOnDemandBasicSolutions(separableMinos, sizedBit, maxOuterBoard, predicate, solutionFilter);
    }

    private Predicate<ColumnField> createPredicate(int cachedMinBit) throws FinderInitializeException {
        if (cachedMinBit == 0)
            return columnField -> true;
        else if (0 < cachedMinBit)
            return BasicSolutions.createBitCountPredicate(cachedMinBit);
        throw new FinderInitializeException("Cached-min-bit should be 0 <= bit: bit=" + cachedMinBit);
    }

    private PathCore createPathCore(Field field, int maxClearLine, int maxDepth, List<String> patterns, MinoFactory minoFactory, ColorConverter colorConverter, SizedBit sizedBit, SolutionFilter solutionFilter, boolean isUsingHold, BasicSolutions basicSolutions) throws FinderInitializeException, FinderExecuteException {
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, field);
        TaskResultHelper taskResultHelper = createTaskResultHelper(maxClearLine);
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        FumenParser fumenParser = createFumenParser(settings.isTetfuSplit(), minoFactory, colorConverter);
        ThreadLocal<BuildUpStream> threadLocalBuildUpStream = createBuildUpStreamThreadLocal(settings.getDropType(), maxClearLine);
        try {
            return new PathCore(patterns, searcher, maxDepth, isUsingHold, fumenParser, threadLocalBuildUpStream);
        } catch (SyntaxException e) {
            output("Pattern syntax error");
            output(e.getMessage());
            throw new FinderInitializeException("Pattern syntax error", e);
        }
    }

    private TaskResultHelper createTaskResultHelper(int height) {
        if (height == 4)
            return new Field4x10MinoPackingHelper();
        return new BasicMinoPackingHelper();
    }

    private FumenParser createFumenParser(boolean isTetfuSplit, MinoFactory minoFactory, ColorConverter colorConverter) {
        if (isTetfuSplit)
            return new SequenceFumenParser(minoFactory, colorConverter);
        return new OneFumenParser(minoFactory, colorConverter);
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

    private PathOutput createOutput(OutputType outputType, IBlocksGenerator generator, int maxDepth) throws FinderExecuteException, FinderInitializeException {
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
            flush();
            logWriter.close();
        } catch (IOException | FinderExecuteException e) {
            throw new FinderTerminateException(e);
        }
    }
}
