package entry.path;

import common.SyntaxException;
import common.ValidPiecesPool;
import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.pattern.LoadedPatternGenerator;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import concurrent.*;
import core.FinderConstant;
import core.action.reachable.Reachable;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
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
import searcher.pack.solutions.FilterOnDemandBasicSolutions;
import searcher.pack.task.BasicMinoPackingHelper;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PerfectPackSearcher;
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

        // Setup reserved blocks
        BlockField reservedBlocks = settings.getReservedBlock();
        if (settings.isReserved()) {
            Verify.reservedBlocks(reservedBlocks);

            for (int y = maxClearLine - 1; 0 <= y; y--) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < 10; x++) {
                    if (reservedBlocks.getBlock(x, y) != null)
                        builder.append(reservedBlocks.getBlock(x, y).getName());
                    else if (!field.isEmpty(x, y))
                        builder.append('X');
                    else
                        builder.append('_');
                }
                output(builder.toString());
            }
        } else {
            output(FieldView.toString(field, maxClearLine));
        }

        // Setup max depth
        int maxDepth = Verify.maxDepth(field, maxClearLine);  // パフェに必要なミノ数

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
        PatternGenerator generator = Verify.patterns(patterns, maxDepth);

        // Output patterns
        for (String pattern : patterns.subList(0, Math.min(5, patterns.size())))
            output("  " + pattern);

        if (5 < patterns.size())
            output(String.format("  ... and more, total %s lines", patterns.size()));

        output();

        // ========================================

        // Setup core
        output("# Initialize / System");

        int threadCount = getThreadCount();

        // Output system-defined
        output("Version = " + FinderConstant.VERSION);
        output("Threads = " + threadCount);
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
        int popCount = isUsingHold && maxDepth < piecesDepth ? maxDepth + 1 : maxDepth;
        output("Piece pop count = " + popCount);
        if (popCount < piecesDepth) {
            output();
            output("####################################################################");
            output("WARNING: more pieces is inputted than necessary.");
            output("         so redundant results may be obtained.");
            output("####################################################################");
            output();
        }

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
        ValidPiecesPool validPiecesPool = createValidPiecesPool(maxDepth, patterns, isUsingHold);
        PathCore pathCore = createPathCore(field, maxClearLine, maxDepth, minoFactory, colorConverter, sizedBit, solutionFilter, isUsingHold, basicSolutions, threadCount, validPiecesPool);
        List<PathPair> pathPairList = run(pathCore, field, sizedBit, reservedBlocks);
        stopwatch2.stop();

        output("     ... done");
        output("  -> Stopwatch stop : " + stopwatch2.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output file");
        OutputType outputType = settings.getOutputType();
        PathOutput pathOutput = createOutput(outputType, generator, maxDepth, minoFactory, colorConverter);
        int numOfAllPatternSequences = validPiecesPool.getAllSpecifiedPieces().size();
        PathPairs pathPairs = new PathPairs(pathPairList, numOfAllPatternSequences);
        pathOutput.output(pathPairs, field, sizedBit);

        output();

        // ========================================

        output("# Finalize");
        output("done");
    }

    private ValidPiecesPool createValidPiecesPool(int maxDepth, List<String> patterns, boolean isUsingHold) throws FinderInitializeException, FinderExecuteException {
        try {
            PatternGenerator blocksGenerator = new LoadedPatternGenerator(patterns);
            return new ValidPiecesPool(blocksGenerator, maxDepth, isUsingHold);
        } catch (SyntaxException e) {
            output("Pattern syntax error");
            output(e.getMessage());
            throw new FinderInitializeException("Pattern syntax error", e);
        }
    }

    private int getThreadCount() {
        int threadCount = settings.getThreadCount();
        if (threadCount <= 0)
            return Runtime.getRuntime().availableProcessors();
        return threadCount;
    }

    private SizedBit decideSizedBitSolutionWidth(int maxClearLine) {
        return maxClearLine <= 4 ? new SizedBit(3, maxClearLine) : new SizedBit(2, maxClearLine);
    }

    private BasicSolutions calculateBasicSolutions(Field field, MinoFactory minoFactory, MinoShifter minoShifter, SizedBit sizedBit, SolutionFilter solutionFilter) throws FinderInitializeException {
        // ミノのリストを作成する
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

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

    private PathCore createPathCore(Field field, int maxClearLine, int maxDepth, MinoFactory minoFactory, ColorConverter colorConverter, SizedBit sizedBit, SolutionFilter solutionFilter, boolean isUsingHold, BasicSolutions basicSolutions, int threadCount, ValidPiecesPool validPiecesPool) throws FinderInitializeException {
        assert 1 <= threadCount;
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, field);
        TaskResultHelper taskResultHelper = createTaskResultHelper(maxClearLine);
        PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper, threadCount != 1);
        FumenParser fumenParser = createFumenParser(settings.isTetfuSplit(), minoFactory, colorConverter);

        DropType dropType = settings.getDropType();
        ThreadLocal<BuildUpStream> threadLocalBuildUpStream = createBuildUpStreamThreadLocal(dropType, maxClearLine);
        ThreadLocal<? extends Reachable> reachableThreadLocal = createReachableThreadLocal(dropType, maxClearLine);

        return new PathCore(searcher, maxDepth, isUsingHold, fumenParser, threadLocalBuildUpStream, reachableThreadLocal, validPiecesPool);
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
        ThreadLocal<? extends Reachable> reachableThreadLocal = createReachableThreadLocal(dropType, maxClearLine);
        return new BuildUpListUpThreadLocal(reachableThreadLocal, maxClearLine);
    }

    private ThreadLocal<? extends Reachable> createReachableThreadLocal(DropType dropType, int maxClearLine) throws FinderInitializeException {
        switch (dropType) {
            case Softdrop:
                return new LockedReachableThreadLocal(maxClearLine);
            case Harddrop:
                return new HarddropReachableThreadLocal(maxClearLine);
            case Rotation180:
                return new SRSAnd180ReachableThreadLocal(maxClearLine);
            case SoftdropTOnly:
                return new SoftdropTOnlyReachableThreadLocal(maxClearLine);
            case TSpinZero:
                return new TSpinOrHarddropReachableThreadLocal(maxClearLine, 0, false);
            case TSpinMini:
                return new TSpinOrHarddropReachableThreadLocal(maxClearLine, 1, false);
            case TSpinSingle:
                return new TSpinOrHarddropReachableThreadLocal(maxClearLine, 1, true);
            case TSpinDouble:
                return new TSpinOrHarddropReachableThreadLocal(maxClearLine, 2, true);
            case TSpinTriple:
                return new TSpinOrHarddropReachableThreadLocal(maxClearLine, 3, true);
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

    private PathOutput createOutput(OutputType outputType, PatternGenerator generator, int maxDepth, MinoFactory minoFactory, ColorConverter colorConverter) throws FinderExecuteException, FinderInitializeException {
        switch (outputType) {
            case CSV:
                return new CSVPathOutput(this, settings);
            case HTML:
                return new LinkPathOutput(this, settings, minoFactory, colorConverter);
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

        if (settings.isLogOutputToConsole())
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
