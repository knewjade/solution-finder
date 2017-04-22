package entry.percent;

import concurrent.checker.invoker.Pair;
import core.field.Field;
import core.field.FieldView;
import core.mino.Block;
import entry.searching_pieces.NormalEnumeratePieces;
import misc.PiecesGenerator;
import misc.Stopwatch;
import tree.AnalyzeTree;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PercentInvoker {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final List<Writer> writers;
    private final Settings settings;
    private final boolean isOutputToConsole;

    public PercentInvoker(Writer writer, Settings settings) {
        this(Collections.singletonList(writer), settings, true);
    }

    PercentInvoker(Writer writer, Settings settings, boolean isOutputToConsole) {
        this(Collections.singletonList(writer), settings, isOutputToConsole);
    }

    private PercentInvoker(List<Writer> writers, Settings settings, boolean isOutputToConsole) {
        this.writers = writers;
        this.settings = settings;
        this.isOutputToConsole = isOutputToConsole;
    }

    public void invoke(Field field, List<String> patterns, int maxClearLine) throws ExecutionException, InterruptedException, IOException {
        output("# Setup Field");
        output(FieldView.toString(field, maxClearLine));

        output();
        // ========================================
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Searching patterns:");
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

        // Holdできるなら必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        int combinationPopCount = settings.isUsingHold() ? maxDepth + 1 : maxDepth;
        if (piecesDepth < combinationPopCount)
            combinationPopCount = piecesDepth;

        output("Piece pop count = " + combinationPopCount);

        NormalEnumeratePieces normalEnumeratePieces = new NormalEnumeratePieces(generator, maxDepth, settings.isUsingHold());
        List<List<Block>> searchingPieces = normalEnumeratePieces.enumerate();

        output("Searching pattern size (duplicate) = " + normalEnumeratePieces.getCounter());
        output("Searching pattern size ( no dup. ) = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

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
        for (Writer writer : writers)
            writer.append(str).append(LINE_SEPARATOR);

        if (isOutputToConsole)
            System.out.println(str);
    }

    private void flush() throws IOException {
        for (Writer writer : writers)
            writer.flush();
    }
}
