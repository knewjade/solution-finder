package _experimental.unused;

import common.ResultHelper;
import common.datastore.Operation;
import common.datastore.Pair;
import common.datastore.Result;
import common.datastore.action.Action;
import common.iterable.AllPermutationIterable;
import common.iterable.CombinationIterable;
import common.order.OrderLookup;
import common.order.StackOrder;
import common.tree.AnalyzeTree;
import common.tree.ConcurrentVisitedTree;
import common.tree.VisitedTree;
import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import core.action.candidate.Candidate;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import lib.Stopwatch;
import searcher.checker.Checker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static core.mino.Block.*;

public class Cart {
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

        ArrayList<Pair<HashableBlocks, Boolean>> results;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("last_output.txt"))) {
            Cart main = new Cart(writer);

            Field field = FieldFactory.createField(marks);
            results = main.sample(field, maxClearLine);
        }

        rec(results, "");

//        ArrayList<Predicate<HashableBlocks>> questions = new ArrayList<>();
//        questions.add((blocks) -> true);
//
//        for (Predicate<HashableBlocks> question : questions) {
//            double value = call(results, question);
//            System.out.println(value);
//        }
    }

    private final BufferedWriter writer;

    private Cart(BufferedWriter writer) {
        this.writer = writer;
    }

    private ArrayList<Pair<HashableBlocks, Boolean>> sample(Field field, int maxClearLine) throws ExecutionException, InterruptedException, IOException {
        output("# Setup Field");
        output(FieldView.toString(field, maxClearLine));

        output();
        // ========================================
        output("# Initialize / User-defined");
        List<Block> usingBlocks = Arrays.asList(I, S, Z, J, L, O, T);

        output("Max clear lines: " + maxClearLine);
        output("Using pieces: " + usingBlocks);

        output();
        // ========================================
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);

        output("Available processors = " + core);

        // 残りのスペースが4の倍数でないときはエラー
        int emptyCount = maxClearLine * 10 - field.getNumOfAllBlocks();
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
                    List<Block> blocks = ResultHelper.createOperationStream(result)
                            .map(Operation::getBlock)
                            .collect(Collectors.toList());

                    int reverseMaxDepth = result.getLastHold() != null ? blocks.size() + 1 : blocks.size();
                    ArrayList<StackOrder<Block>> reversePieces = OrderLookup.reverseBlocks(blocks, reverseMaxDepth);
                    for (StackOrder<Block> piece : reversePieces) {
                        visitedTree.set(true, piece.toList());
                    }
                }

                return new Pair<>(target, checkResult);
            });
            futureResults.add(future);
        }

        // 結果を集計する
        AnalyzeTree analyzeTree = new AnalyzeTree();
        for (Future<Pair<List<Block>, Boolean>> future : futureResults) {
            Pair<List<Block>, Boolean> resultPair = future.get();
            List<Block> blocks = resultPair.getKey();
            Boolean result = resultPair.getValue();
            analyzeTree.set(result, blocks);
        }

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();
        // ========================================
        output("-------------------");

        //
        ArrayList<Pair<HashableBlocks, Boolean>> results = new ArrayList<>();
        for (Future<Pair<List<Block>, Boolean>> future : futureResults) {
            Pair<List<Block>, Boolean> pair = future.get();
            HashableBlocks blocks = new HashableBlocks(pair.getKey());
            results.add(new Pair<>(blocks, pair.getValue()));
        }

        output();
        // ========================================
        output("# Finalize");
        executorService.shutdown();
        output("done");

        writer.flush();

        return results;
    }

//    private static double callFirst(List<Pair<HashableBlocks, Boolean>> results) {
//        Counter counter = new Counter();
//
//        for (Pair<HashableBlocks, Boolean> pair : results) {
//            counter.set(pair.getValue());
//        }
//
//        return calcGiniIndex(counter);
//    }

    private static void rec(List<Pair<HashableBlocks, Boolean>> results, String answers) {
        if (5 <= answers.length()) {
            checkLast(results, answers);
            return;
        }

        // 64 = 2^6
        double minValue = Double.MAX_VALUE;
        int maxIndex = -1;
        int maxPattern = -1;
        for (int index = 0; index < 4; index++) {
            for (int pattern = 1; pattern < 64; pattern++) {
                final int finalIndex = index;
                Split split = new Split(pattern);
                List<Block> left = split.getLeft();
                Predicate<HashableBlocks> question = (HashableBlocks blocks) -> left.contains(blocks.getBlocks().get(finalIndex));
                double value = call(results, question);
                if (value < minValue) {
                    minValue = value;
                    maxIndex = index;
                    maxPattern = pattern;
                }
            }
        }

        Split maxSplit = new Split(maxPattern);
        List<Block> maxLeft = maxSplit.getLeft().size() < maxSplit.getRight().size() ? maxSplit.getLeft() : maxSplit.getRight();
        int maxFinalIndex = maxIndex;

        Predicate<HashableBlocks> maxQuestion = (HashableBlocks blocks) -> maxLeft.contains(blocks.getBlocks().get(maxFinalIndex));
        ArrayList<Pair<HashableBlocks, Boolean>> nextTrue = new ArrayList<>();
        ArrayList<Pair<HashableBlocks, Boolean>> nextFalse = new ArrayList<>();
        for (Pair<HashableBlocks, Boolean> result : results) {
            HashableBlocks blocks = result.getKey();
            if (maxQuestion.test(blocks))
                nextTrue.add(result);
            else
                nextFalse.add(result);
        }

        if (nextTrue.isEmpty() || nextFalse.isEmpty()) {
            for (int count = 0; count < answers.length(); count++)
                System.out.print("  ");
            System.out.println("retire");
            checkLast(results, answers + "x");
            results.forEach(pair -> System.out.println(pair.getKey().getBlocks() + " " + pair.getValue()));
            return;
        }

        for (int count = 0; count < answers.length(); count++)
            System.out.print("  ");
        System.out.println("Do you have " + maxLeft + " at " + maxIndex + " ?");

        double sucPercent1 = getSucPercent(nextTrue);
        if (sucPercent1 == 0.0 || sucPercent1 == 1.0) {
            for (int count = 0; count < answers.length() + 1; count++)
                System.out.print("  ");

            if (sucPercent1 == 0.0)
                System.out.println(answers + "y is all NG");
            else
                System.out.println(answers + "y is all OK");
        } else {
            rec(nextTrue, answers + "y");
        }

        double sucPercent2 = getSucPercent(nextFalse);
        if (sucPercent2 == 0.0 || sucPercent2 == 1.0) {
            for (int count = 0; count < answers.length() + 1; count++)
                System.out.print("  ");

            if (sucPercent2 == 0.0)
                System.out.println(answers + "n is all NG");
            else
                System.out.println(answers + "n is all OK");
        } else {
            rec(nextFalse, answers + "n");
        }
    }

    private static double getSucPercent(List<Pair<HashableBlocks, Boolean>> results) {
        Counter counter = new Counter();
        for (Pair<HashableBlocks, Boolean> result : results) {
            counter.set(result.getValue());
        }
        return counter.getTruePercent();
    }

    private static void checkLast(List<Pair<HashableBlocks, Boolean>> results, String answers) {
        Counter counter = new Counter();

        for (Pair<HashableBlocks, Boolean> pair : results) {
            Boolean isTrue = pair.getValue();
            counter.set(isTrue);
        }

        for (int count = 0; count < answers.length(); count++)
            System.out.print("  ");
        System.out.printf("%s: %2.1f%% [%d/%d]%n", answers, counter.getTruePercent() * 100, counter.getTrueCount(), counter.getAllCount());
    }

    private static double call(List<Pair<HashableBlocks, Boolean>> results, Predicate<HashableBlocks> question) {
        Counter questionTrueCounter = new Counter();
        Counter questionFalseCounter = new Counter();

        for (Pair<HashableBlocks, Boolean> pair : results) {
            HashableBlocks blocks = pair.getKey();
            Boolean isTrue = pair.getValue();
            if (question.test(blocks)) {
                questionTrueCounter.set(isTrue);
            } else {
                questionFalseCounter.set(isTrue);
            }
        }

        return calc(questionTrueCounter, questionFalseCounter);
    }

    private static double calc(Counter questionTrueCounter, Counter questionFalseCounter) {
        int trueCount = questionTrueCounter.getAllCount();
        int falseCount = questionFalseCounter.getAllCount();
        double allCount = trueCount + falseCount;

        double sum = 0.0;
        if (0 < trueCount)
            sum += (trueCount / allCount) * calcGiniIndex(questionTrueCounter);

        if (0 < falseCount)
            sum += (falseCount / allCount) * calcGiniIndex(questionFalseCounter);

        return sum;
    }

    private static double calcGiniIndex(Counter questionCounter) {
        double sum = Math.pow(questionCounter.getTruePercent(), 2) + Math.pow(questionCounter.getFalsePercent(), 2);
        return 1.0 - sum;
    }


    private void output() throws IOException {
        output("");
    }

    private void output(String str) throws IOException {
        writer.append(str);
        writer.newLine();
        System.out.println(str);
    }

    private static class Counter {
        private int trueCounter = 0;
        private int falseCounter = 0;

        public void set(boolean flag) {
            if (flag)
                addTrue();
            else
                addFalse();
        }

        private void addTrue() {
            trueCounter += 1;
        }

        private void addFalse() {
            falseCounter += 1;
        }

        public int getTrueCount() {
            return trueCounter;
        }

        public int getAllCount() {
            return trueCounter + falseCounter;
        }

        public double getTruePercent() {
            int allCount = getAllCount();
            assert allCount != 0;
            return (double) trueCounter / allCount;
        }

        public double getFalsePercent() {
            int allCount = getAllCount();
            assert allCount != 0;
            return (double) falseCounter / allCount;
        }

        public double getEntropy() {
            int allCount = getAllCount();
            return calcEntropy(trueCounter, allCount) + calcEntropy(falseCounter, allCount);
        }

        private double calcEntropy(double count, double allCount) {
            return (count / allCount) * log2(allCount / count);
        }

        private double log2(double v) {
            return Math.log(v) / Math.log(2);
        }
    }

    private static class Split {
        private final List<Block> left = new ArrayList<>();
        private final List<Block> right = new ArrayList<>();

        public Split(int v) {
            assert 0 < v && v < 64;
            for (Block block : Block.values()) {
                if ((v & 1) == 0)
                    left.add(block);
                else
                    right.add(block);
                v >>>= 1;
            }
        }

        public List<Block> getLeft() {
            return left;
        }

        public List<Block> getRight() {
            return right;
        }
    }
}
