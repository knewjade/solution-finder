import concurrent.CheckerThreadLocal;
import concurrent.LockedCandidateThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import concurrent.invoker.ConcurrentCheckerInvoker;
import concurrent.invoker.Pair;
import misc.Stopwatch;
import misc.iterable.AllPermutationIterable;
import misc.iterable.CombinationIterable;
import searcher.common.action.Action;
import tree.CheckerTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static core.mino.Block.*;

// TODO: List of ALL
// Write unittest for searcher.common
// Computerize from main
// Write unittest for main
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        int maxClearLine;
        String marks = "";
        try (Scanner scanner = new Scanner(new File("field.txt"))) {
            if (!scanner.hasNextInt())
                throw new IllegalArgumentException("Cannot read Field Height");
            maxClearLine = scanner.nextInt();

            if (maxClearLine < 2 || 12 < maxClearLine)
                throw new IllegalArgumentException("Field Height should be 2 <= height <= 12");

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext())
                stringBuilder.append(scanner.nextLine());

            marks = stringBuilder.toString();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("last_output.txt"))) {
            Main main = new Main(writer);

            Field field = FieldFactory.createField(marks);
            main.sample(field, maxClearLine);
        }
    }

    private final BufferedWriter writer;

    private Main(BufferedWriter writer) {
        this.writer = writer;
    }

    private void sample(Field field, int maxClearLine) throws ExecutionException, InterruptedException, IOException {
        output("# Setup Field");
        output(FieldView.toString(field, maxClearLine));

        output();
        // ========================================
        output("# Initialize / User-defined");
        List<Block> usingBlocks = Arrays.asList(T, I, S, Z, J, L, O);

        output("Max clear lines: " + maxClearLine);
        output("Using pieces: " + usingBlocks);

        output();
        // ========================================
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerThreadLocal<Action> checkerThreadLocal = new CheckerThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckerInvoker invoker = new ConcurrentCheckerInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        output("Available processors = " + core);

        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getAllBlockCount();
        if (emptyCount % 4 != 0)
            throw new IllegalArgumentException("Error: EmptyCount should be mod 4: " + emptyCount);

        // ブロック数が足りないときはエラー
        int maxDepth = emptyCount / 4;
        if (usingBlocks.size() < maxDepth)
            throw new IllegalArgumentException("Error: blocks size check short: " + usingBlocks.size() + " < " + maxDepth);

        output();
        // ========================================
        output("# Enumerate pieces");

        // 必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        int combinationPopCount = maxDepth + 1;
        if (usingBlocks.size() < combinationPopCount)
            combinationPopCount = usingBlocks.size();

        output("Piece pop count = " + combinationPopCount);

        List<List<Block>> searchingPieces = new ArrayList<>();
        // 組み合わせの列挙
        Iterable<List<Block>> combinationIterable = new CombinationIterable<>(usingBlocks, combinationPopCount);
        for (List<Block> combination : combinationIterable) {
            // 組み合わせから、順列を列挙
            Iterable<List<Block>> permutationIterable = new AllPermutationIterable<>(combination);
            for (List<Block> permutation : permutationIterable) {
                searchingPieces.add(permutation);
            }
        }

        output("Searching pattern count = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        List<Pair<List<Block>, Boolean>> resultPairs = invoker.search(field, searchingPieces, maxClearLine, maxDepth);

        // 結果を集計する
        CheckerTree tree = new CheckerTree();
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

        writer.flush();
    }

    private void output() throws IOException {
        output("");
    }

    private void output(String str) throws IOException {
        writer.append(str);
        writer.newLine();
        System.out.println(str);
    }
}
