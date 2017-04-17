package entry;

import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerNoHoldThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.ConcurrentCheckerInvoker;
import concurrent.checker.invoker.Pair;
import concurrent.checker.invoker.no_hold.ConcurrentCheckerNoHoldInvoker;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.field.Field;
import core.field.FieldView;
import core.mino.Block;
import misc.PiecesGenerator;
import misc.SafePieces;
import misc.Stopwatch;
import searcher.common.action.Action;
import tree.AnalyzeTree;
import tree.VisitedTree;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CheckerEntry {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final List<Writer> writers;
    private final Settings settings;
    private final boolean isOutputToConsole;

    public CheckerEntry(Writer writer, Settings settings) {
        this(Collections.singletonList(writer), settings, true);
    }

    CheckerEntry(Writer writer, Settings settings, boolean isOutputToConsole) {
        this(Collections.singletonList(writer), settings, isOutputToConsole);
    }

    private CheckerEntry(List<Writer> writers, Settings settings, boolean isOutputToConsole) {
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
        PiecesGenerator generator = new PiecesGenerator(patterns);

        output("Max clear lines: " + maxClearLine);
        output("Searching patterns:");
        for (String pattern : patterns)
            output("  " + pattern);

        output();
        // ========================================
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        ConcurrentCheckerInvoker invoker = createConcurrentCheckerInvoker(maxClearLine, executorService);

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

        List<List<Block>> searchingPieces = createSearchingPieces(generator, combinationPopCount);

        output("Searching pattern size ( no dup. ) = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        AnalyzeTree tree = new AnalyzeTree();
        for (Pair<List<Block>, Boolean> resultPair : resultPairs) {
            List<Block> pieces = resultPair.getKey();
            Boolean result = resultPair.getValue();
            tree.set(result, pieces);
        }

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

    private ConcurrentCheckerInvoker createConcurrentCheckerInvoker(int maxClearLine, ExecutorService executorService) {
        if (settings.isUsingHold()) {
            CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
            LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
            return new ConcurrentCheckerUsingHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);
        } else {
            CheckerNoHoldThreadLocal<Action> checkerThreadLocal = new CheckerNoHoldThreadLocal<>();
            LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
            return new ConcurrentCheckerNoHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);
        }
    }

    private List<List<Block>> createSearchingPieces(PiecesGenerator generator, int combinationPopCount) throws IOException {
        int counter = 0;
        List<List<Block>> searchingPieces = new ArrayList<>();
        VisitedTree duplicateCheckTree = new VisitedTree();
        boolean isOverPieces = combinationPopCount < generator.getDepth();

        // 組み合わせの列挙
        for (SafePieces pieces : generator) {
            counter++;
            List<Block> blocks = pieces.getBlocks();
            if (isOverPieces)
                blocks = blocks.subList(0, combinationPopCount);

            if (!duplicateCheckTree.isVisited(blocks)) {
                searchingPieces.add(blocks);
                duplicateCheckTree.success(blocks);
            }
        }

        output("Searching pattern size (duplicate) = " + counter);

        return searchingPieces;
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
