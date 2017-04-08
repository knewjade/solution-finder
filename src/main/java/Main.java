import action.candidate.Candidate;
import analyzer.CheckerTree;
import concurrent.CandidateThreadLocal;
import concurrent.CheckerThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import javafx.util.Pair;
import misc.Stopwatch;
import misc.iterable.CombinationIterable;
import misc.iterable.PermutationIterable;
import searcher.checker.Checker;
import searcher.common.action.Action;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static core.mino.Block.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        String fieldStr = readAll("field.txt");
        Field field = FieldFactory.createField(fieldStr);
        sample(field);
    }

    static String readAll(String path) throws IOException {
        return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
                .collect(Collectors.joining(""));
    }

    private static void sample(Field field) throws ExecutionException, InterruptedException {
        System.out.println("# Initialize / User-defined");
        List<Block> usingBlocks = Arrays.asList(I, T, S, Z, J, L, O);
        int maxClearLine = 4;

        System.out.println("Max clear lines: " + maxClearLine);
        System.out.println("Using blocks: " + usingBlocks);

        System.out.println();
        // ========================================
        System.out.println("# Setup Field");
        System.out.println(FieldView.toString(field, maxClearLine));

        System.out.println();
        // ========================================
        System.out.println("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        ThreadLocal<Checker<Action>> checkerThreadLocal = new CheckerThreadLocal<>();
        CandidateThreadLocal candidateThreadLocal = new CandidateThreadLocal(maxClearLine);

        System.out.println("Available processors = " + core);

        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getAllBlockCount();
        if (emptyCount % 4 != 0)
            throw new IllegalArgumentException("Error: EmptyCount should be mod 4: " + emptyCount);

        // ブロック数が足りないときはエラー
        int maxDepth = emptyCount / 4;
        if (usingBlocks.size() < maxDepth)
            throw new IllegalArgumentException("Error: blocks size check short: " + usingBlocks.size() + " < " + maxDepth);

        System.out.println();
        // ========================================
        System.out.println("# Enumerate target");

        // 必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        int combinationPopCount = maxDepth + 1;
        if (usingBlocks.size() < combinationPopCount)
            combinationPopCount = usingBlocks.size();

        System.out.println("Block pop count = " + combinationPopCount);

        List<List<Block>> searchingTargets = new ArrayList<>();
        // 組み合わせの列挙
        Iterable<List<Block>> combinationIterable = new CombinationIterable<>(usingBlocks, combinationPopCount);
        for (List<Block> combination : combinationIterable) {
            // 組み合わせから、順列を列挙
            Iterable<List<Block>> permutationIterable = new PermutationIterable<>(combination);
            for (List<Block> permutation : permutationIterable) {
                searchingTargets.add(permutation);
            }
        }

        System.out.println("Searching target count = " + searchingTargets.size());

        System.out.println();
        // ========================================
        System.out.println("# Search");
        System.out.println("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        List<Future<Pair<List<Block>, Boolean>>> futureResults = new ArrayList<>();
        for (List<Block> target : searchingTargets) {
            Future<Pair<List<Block>, Boolean>> future = executorService.submit(() -> {
                Checker<Action> checker = checkerThreadLocal.get();
                Candidate<Action> candidate = candidateThreadLocal.get();
                boolean result = checker.check(field, target, candidate, maxClearLine, maxDepth);
                return new Pair<>(target, result);
            });
            futureResults.add(future);
        }

        // 結果を集計する
        CheckerTree checkerTree = new CheckerTree();
        for (Future<Pair<List<Block>, Boolean>> future : futureResults) {
            Pair<List<Block>, Boolean> resultPair = future.get();
            List<Block> blocks = resultPair.getKey();
            Boolean result = resultPair.getValue();
            checkerTree.set(result, blocks);
        }

        stopwatch.stop();
        System.out.println("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        System.out.println();
        // ========================================
        System.out.println("# Output");
        checkerTree.show();
        checkerTree.tree(2);

        System.out.println();
        // ========================================
        System.out.println("# Finalize");
        executorService.shutdown();
        System.out.println("done");
    }
}
