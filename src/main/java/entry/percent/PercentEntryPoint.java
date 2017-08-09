package entry.percent;

import common.datastore.pieces.LongBlocks;
import lib.Stopwatch;
import common.SyntaxException;
import common.datastore.Pair;
import common.pattern.PiecesGenerator;
import common.tree.AnalyzeTree;
import core.field.Field;
import core.field.FieldView;
import core.mino.Block;
import entry.EntryPoint;
import entry.searching_pieces.NormalEnumeratePieces;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PercentEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String CHARSET = "utf-8";

    private final PercentSettings settings;
    private final BufferedWriter logWriter;

    public PercentEntryPoint(PercentSettings settings) throws IOException {
        this.settings = settings;

        String logFilePath = settings.getLogFilePath();
        File logFile = new File(logFilePath);

        // 親ディレクトリがない場合は作成
        if (!logFile.getParentFile().exists()) {
            boolean mairSuccess = logFile.getParentFile().mkdir();
            if (!mairSuccess) {
                throw new IllegalStateException("Failed to make output directory");
            }
        }

        if (logFile.isDirectory())
            throw new IllegalArgumentException("Cannot specify directory as log file path");
        if (logFile.exists() && !logFile.canWrite())
            throw new IllegalArgumentException("Cannot write log file");

        this.logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, false), CHARSET));
    }

    @Override
    public void run() throws Exception {
        output("# Setup Field");
        Field field = settings.getField();
        if (field == null)
            throw new IllegalArgumentException("Should specify field");

        int maxClearLine = settings.getMaxClearLine();
        if (maxClearLine < 2 || 12 < maxClearLine)
            throw new IllegalArgumentException("Field Height should be 2 <= height <= 12");

        output(FieldView.toString(field, maxClearLine));

        output();
        // ========================================
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Searching patterns:");
        List<String> patterns = settings.getPatterns();
        if (patterns.isEmpty())
            throw new IllegalArgumentException("Should specify patterns, not allow empty");

        try {
            PiecesGenerator.verify(patterns);
        } catch (SyntaxException e) {
            throw new IllegalArgumentException("Invalid patterns", e);
        }

        for (String pattern : patterns)
            output("  " + pattern);

        output();
        // ========================================
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        PiecesGenerator generator = new PiecesGenerator(patterns);

        output("Available processors = " + core);

        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getNumOfAllBlocks();
        if (emptyCount % 4 != 0)
            throw new IllegalArgumentException("Error: EmptyCount should be mod 4: " + emptyCount);

        // ブロック数が足りないときはエラー
        int maxDepth = emptyCount / 4;
        int piecesDepth = generator.getDepth();
        if (piecesDepth < maxDepth)
            throw new IllegalArgumentException("Error: pieces is too short: " + piecesDepth + " < " + maxDepth);

        output("Necessary Pieces = " + maxDepth);

        output();
        // ========================================
        output("# Enumerate pieces");
        int popCount = settings.isUsingHold() && maxDepth < piecesDepth ? maxDepth + 1 : maxDepth;
        output("Piece pop count = " + popCount);
        if (popCount < piecesDepth) {
            output();
            output("####################################################################");
            output("WARNING: Inputted pieces is more than 'necessary pieces'.");
            output("         Because reduce unnecessary pieces,");
            output("         there is a possibility of getting no expected percentages.");
            output("####################################################################");
            output();
        }

        // 探索パターンの列挙
        NormalEnumeratePieces normalEnumeratePieces = new NormalEnumeratePieces(generator, maxDepth, settings.isUsingHold());
        Set<LongBlocks> searchingPieces = normalEnumeratePieces.enumerate();

        output("Searching pattern size (duplicate) = " + normalEnumeratePieces.getCounter());
        output("Searching pattern size ( no dup. ) = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // 探索を行う
        PercentCore percentCore = new PercentCore(maxClearLine, executorService, settings.isUsingHold());
        percentCore.run(field, searchingPieces, maxClearLine, maxDepth);
        AnalyzeTree tree = percentCore.getResultTree();
        List<Pair<List<Block>, Boolean>> resultPairs = percentCore.getResultPairs();

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();
        // ========================================
        output("# Output");
        output(tree.show());

        output();

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

            List<Pair<List<Block>, Boolean>> failedPairs = resultPairs.stream()
                    .filter(pair -> !pair.getValue())
                    .limit(failedMaxCount)
                    .collect(Collectors.toList());

            for (Pair<List<Block>, Boolean> resultPair : failedPairs)
                output(resultPair.getKey().toString());

            if (failedPairs.isEmpty())
                output("nothing");
        } else if (failedMaxCount < 0) {
            output("Fail pattern (all)");

            List<Pair<List<Block>, Boolean>> failedPairs = resultPairs.stream()
                    .filter(pair -> !pair.getValue())
                    .collect(Collectors.toList());

            for (Pair<List<Block>, Boolean> resultPair : failedPairs)
                output(resultPair.getKey().toString());

            if (failedPairs.isEmpty())
                output("nothing");
        }

        output();
        // ========================================
        output("# Finalize");
        executorService.shutdown();
        output("done");

        flush();
    }

    private void output() throws IOException {
        output("");
    }

    private void output(String str) throws IOException {
        logWriter.append(str).append(LINE_SEPARATOR);

        if (settings.isOutputToConsole())
            System.out.println(str);
    }

    private void flush() throws IOException {
        logWriter.flush();
    }

    @Override
    public void close() throws Exception {
        logWriter.close();
    }
}
