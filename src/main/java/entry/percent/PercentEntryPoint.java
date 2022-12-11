package entry.percent;

import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.pattern.PatternGenerator;
import common.tree.AnalyzeTree;
import concurrent.*;
import core.FinderConstant;
import core.action.candidate.Candidate;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.srs.MinoRotation;
import entry.DropType;
import entry.EntryPoint;
import entry.Verify;
import entry.path.output.MyFile;
import entry.searching_pieces.NormalEnumeratePieces;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PercentEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final PercentSettings settings;
    private final BufferedWriter logWriter;

    public PercentEntryPoint(PercentSettings settings) throws FinderInitializeException {
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
        Verify.maxClearLineUnder24(maxClearLine);

        // Output field
        output(FieldView.toString(field, maxClearLine));

        // Setup max depth
        int maxDepth = Verify.maxDepth(field, maxClearLine);  // パフェに必要なミノ数

        output();

        // ========================================

        // Output user-defined
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Kicks: " + settings.getKicksName().toLowerCase());
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

        ExecutorService executorService = createExecutorService();

        output("Version = " + FinderConstant.VERSION);
        output("Necessary Pieces = " + maxDepth);

        output();

        // ========================================

        // Holdができるときは必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        output("# Enumerate pieces");
        int piecesDepth = generator.getDepth();
        int popCount = settings.isUsingHold() && maxDepth < piecesDepth ? maxDepth + 1 : maxDepth;
        output("Piece pop count = " + popCount);
        if (popCount < piecesDepth) {
            output();
            output("####################################################################");
            output("WARNING: Inputted pieces is more than 'necessary blocks'.");
            output("         Because reduce unnecessary pieces,");
            output("         there is a possibility of getting no expected percentages.");
            output("####################################################################");
            output();
        }

        // 探索パターンの列挙
        NormalEnumeratePieces normalEnumeratePieces = new NormalEnumeratePieces(generator, maxDepth, settings.isUsingHold());
        Set<LongPieces> searchingPieces = normalEnumeratePieces.enumerate();

        output("Searching pattern size (duplicate) = " + normalEnumeratePieces.getCounter());
        output("Searching pattern size ( no dup. ) = " + searchingPieces.size());

        output();

        // ========================================

        // 探索を行う
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        Supplier<MinoRotation> minoRotationSupplier = settings.createMinoRotationSupplier();
        ThreadLocal<? extends Candidate<Action>> candidateThreadLocal = createCandidateThreadLocal(minoRotationSupplier, settings.getDropType(), maxClearLine);
        ThreadLocal<? extends Reachable> reachableThreadLocal = createReachableThreadLocal(minoRotationSupplier, settings.getDropType(), maxClearLine);
        MinoFactory minoFactory = new MinoFactory();
        PercentCore percentCore = new PercentCore(executorService, candidateThreadLocal, settings.isUsingHold(), reachableThreadLocal, minoFactory);
        percentCore.run(field, searchingPieces, maxClearLine, maxDepth);

        AnalyzeTree tree = percentCore.getResultTree();
        List<Pair<Pieces, Boolean>> resultPairs = percentCore.getResultPairs();

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        // Output tree
        output("# Output");
        output(tree.show());

        output();

        // Output failed patterns
        int treeDepth = settings.getTreeDepth();
        if (piecesDepth < treeDepth)
            treeDepth = piecesDepth;

        output(String.format("Success pattern tree [Head %d pieces]:", treeDepth));
        output(tree.tree(treeDepth));

        output("-------------------");

        int failedMaxCount = settings.getFailedCount();
        // skip if failedMaxCount == 0
        if (0 < failedMaxCount) {
            output(String.format("Fail pattern (max. %d)", failedMaxCount));

            List<Pair<Pieces, Boolean>> failedPairs = resultPairs.stream()
                    .filter(pair -> !pair.getValue())
                    .limit(failedMaxCount)
                    .collect(Collectors.toList());

            outputFailedPatterns(failedPairs);
        } else if (failedMaxCount < 0) {
            output("Fail pattern (all)");

            List<Pair<Pieces, Boolean>> failedPairs = resultPairs.stream()
                    .filter(pair -> !pair.getValue())
                    .collect(Collectors.toList());

            outputFailedPatterns(failedPairs);
        }

        output();

        // ========================================

        output("# Finalize");
        if (executorService != null)
            executorService.shutdown();

        output("done");
    }

    private ExecutorService createExecutorService() throws FinderExecuteException {
        int threadCount = settings.getThreadCount();
        if (threadCount == 1) {
            // single thread
            output("Threads = 1");
            return null;
        } else if (1 < threadCount) {
            // Specified thread count
            output("Threads = " + threadCount);
            return Executors.newFixedThreadPool(threadCount);
        } else {
            // NOT specified thread count
            int core = Runtime.getRuntime().availableProcessors();
            output("Threads = " + core);
            return Executors.newFixedThreadPool(core);
        }
    }

    private ThreadLocal<? extends Candidate<Action>> createCandidateThreadLocal(
            Supplier<MinoRotation> minoRotationSupplier, DropType dropType, int maxClearLine
    ) throws FinderInitializeException {
        boolean use180Rotation = dropType.uses180Rotation();

        switch (dropType) {
            case Harddrop:
                return new HarddropCandidateThreadLocal();
            case Softdrop:
            case Softdrop180:
                return new LockedCandidateThreadLocal(minoRotationSupplier, maxClearLine, use180Rotation);
            case SoftdropTOnly:
                return new SoftdropTOnlyCandidateThreadLocal(minoRotationSupplier, maxClearLine, use180Rotation);
        }
        throw new FinderInitializeException("Unsupported droptype: droptype=" + dropType);
    }

    private ThreadLocal<? extends Reachable> createReachableThreadLocal(
            Supplier<MinoRotation> minoRotationSupplier, DropType dropType, int maxClearLine
    ) throws FinderInitializeException {
        boolean use180Rotation = dropType.uses180Rotation();

        switch (dropType) {
            case Harddrop:
                return new HarddropReachableThreadLocal(maxClearLine);
            case Softdrop:
            case Softdrop180:
                return new ILockedReachableThreadLocal(minoRotationSupplier, maxClearLine, use180Rotation);
            case SoftdropTOnly:
                return new SoftdropTOnlyReachableThreadLocal(minoRotationSupplier, maxClearLine, use180Rotation);
        }
        throw new FinderInitializeException("Unsupported droptype: droptype=" + dropType);
    }

    private void outputFailedPatterns(List<Pair<Pieces, Boolean>> failedPairs) throws FinderExecuteException {
        for (Pair<Pieces, Boolean> resultPair : failedPairs)
            output(resultPair.getKey().getPieces().toString());

        if (failedPairs.isEmpty())
            output("nothing");
    }

    private void output() throws FinderExecuteException {
        output("");
    }

    private void output(String str) throws FinderExecuteException {
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
