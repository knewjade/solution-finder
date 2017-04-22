package entry.percent;

import concurrent.checker.invoker.Pair;
import core.field.Field;
import core.field.FieldView;
import core.mino.Block;
import entry.EntryPoint;
import entry.searching_pieces.NormalEnumeratePieces;
import misc.PiecesGenerator;
import misc.Stopwatch;
import misc.SyntaxException;
import tree.AnalyzeTree;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PercentEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String CHARSET = "utf-8";

    private final PercentSettings settings;
    private final BufferedWriter logWriter;

    public PercentEntryPoint(PercentSettings settings) throws IOException {
        this.settings = settings;

        String logFilePath = settings.getLogFilePath();
        File logFile = new File(logFilePath);
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
        int emptyCount = maxClearLine * 10 - field.getAllBlockCount();
        if (emptyCount % 4 != 0)
            throw new IllegalArgumentException("Error: EmptyCount should be mod 4: " + emptyCount);

        // ブロック数が足りないときはエラー
        int maxDepth = emptyCount / 4;
        int piecesDepth = generator.getDepth();
        if (piecesDepth < maxDepth)
            throw new IllegalArgumentException("Error: blocks size check short: " + piecesDepth + " < " + maxDepth);

        output("Need Pieces = " + maxDepth);

        output();
        // ========================================
        output("# Enumerate pieces");
        output("Piece pop count = " + (settings.isUsingHold() ? maxDepth + 1 : maxDepth));

        // 探索パターンの列挙
        NormalEnumeratePieces normalEnumeratePieces = new NormalEnumeratePieces(generator, maxDepth, settings.isUsingHold());
        List<List<Block>> searchingPieces = normalEnumeratePieces.enumerate();

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
        output("Success pattern tree [Head 3 pieces]:");
        output(tree.tree(3));

        output("-------------------");
        output("Fail pattern (Max. 100)");
        int counter = 0;
        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
            Boolean result = resultPair.getValue();
            if (!result) {
                output(resultPair.getKey().toString());
                counter += 1;
                if (100 <= counter)
                    break;
            }
        }

        if (counter == 0)
            output("nothing");

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
