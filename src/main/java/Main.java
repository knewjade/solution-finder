import action.candidate.Candidate;
import analyzer.CheckerTree;
import analyzer.ConcurrentVisitedTree;
import analyzer.VisitedTree;
import concurrent.CheckerThreadLocal;
import concurrent.LockedCandidateThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import javafx.util.Pair;
import main.OrderLookup;
import main.Pieces;
import misc.Stopwatch;
import misc.iterable.CombinationIterable;
import misc.iterable.PermutationIterable;
import searcher.checker.Checker;
import searcher.common.Operation;
import searcher.common.Result;
import searcher.common.action.Action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import static core.mino.Block.*;

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
        List<Block> usingBlocks = Arrays.asList(I, S, Z, J, L, O);

        output("Max clear lines: " + maxClearLine);
        output("Using pieces: " + usingBlocks);

        output();
        // ========================================
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerThreadLocal<Action> checkerThreadLocal = new CheckerThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);

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
            Iterable<List<Block>> permutationIterable = new PermutationIterable<>(combination);
            for (List<Block> permutation : permutationIterable) {
                searchingPieces.add(permutation);
            }
        }

        output("Searching pattern count = " + searchingPieces.size());

        output();
        // ========================================
        output("# Search");
        output("  -> Stopwatch start");
        ConcurrentVisitedTree visitedTree = new ConcurrentVisitedTree();
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        List<Future<Pair<List<Block>, Boolean>>> futureResults = new ArrayList<>();
        for (List<Block> target : searchingPieces) {
            Future<Pair<List<Block>, Boolean>> future = executorService.submit(() -> {
                int succeed = visitedTree.isSucceed(target);
                if (succeed != VisitedTree.NO_RESULT)
                    return new Pair<>(target, succeed == VisitedTree.SUCCEED);

                Checker<Action> checker = checkerThreadLocal.get();
                Candidate<Action> candidate = candidateThreadLocal.get();
                boolean checkResult = checker.check(field, target, candidate, maxClearLine, maxDepth);
                visitedTree.set(checkResult, target);

                if (checkResult) {
                    Result result = checker.getResult();
                    List<Operation> operations = result.createOperations();
                    ArrayList<Block> operationBlocks = new ArrayList<>();
                    for (Operation operation : operations) {
                        operationBlocks.add(operation.getBlock());
                    }

                    int reverseMaxDepth = result.getLastHold() != null ? operationBlocks.size() + 1 : operationBlocks.size();
                    ArrayList<Pieces> reversePieces = OrderLookup.reverse(operationBlocks, reverseMaxDepth);
                    for (Pieces piece : reversePieces) {
                        visitedTree.set(true, piece.getBlocks());
                    }
                }

                return new Pair<>(target, checkResult);
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
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();
        // ========================================
        output("# Output");
        output(checkerTree.show());

        output();
        output("Success pattern tree [Head 3 pieces]:");
        output(checkerTree.tree(3));

        output("-------------------");
        output("Fail pattern (Max. 100)");
        int counter = 0;
        for (Future<Pair<List<Block>, Boolean>> future : futureResults) {
            Pair<List<Block>, Boolean> resultPair = future.get();
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
