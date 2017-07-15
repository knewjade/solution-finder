package _experimental.unused;

import core.action.candidate.Candidate;
import searcher.checker.Checker;
import common.tree.AnalyzeTree;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import lib.Stopwatch;
import common.iterable.CombinationIterable;
import common.iterable.AllPermutationIterable;
import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import common.datastore.action.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static core.mino.Block.*;

public class ConcurrentMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread.sleep(1000);

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        Field field = FieldFactory.createField(marks);
        List<Block> allBlocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 7;
        int maxDepth = 6;
        int maxClearLine = 4;

        // Executor
        int core = Runtime.getRuntime().availableProcessors();
//        System.out.println(core);
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        ThreadLocal<Checker<Action>> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);

        // enumerate combinations and sort
        ArrayList<Callable<PairObj>> callables = new ArrayList<>();
        Iterable<List<Block>> permutations = new CombinationIterable<>(allBlocks, popCount);
        for (List<Block> permutation : permutations) {
            Iterable<List<Block>> combinations = new AllPermutationIterable<>(permutation);
            for (List<Block> blocks : combinations) {
                callables.add(() -> {
                    Checker<Action> checker = checkerThreadLocal.get();
                    Candidate<Action> candidate = candidateThreadLocal.get();
                    boolean check = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
                    return new PairObj(blocks, check);
                });
            }
        }

        stopwatch.start();

        List<Future<PairObj>> futures = executorService.invokeAll(callables);

        AnalyzeTree tree = new AnalyzeTree();
        for (Future<PairObj> future : futures) {
            PairObj obj = future.get();
            List<Block> combination = obj.blocks;
//                System.out.print(combination + " => ");
            if (obj.isSucceed) {
//                    System.out.println("success");
                tree.success(combination);
            } else {
//                    System.out.println("fail");
                tree.fail(combination);
//                    treeFail.fail(combination);
            }
        }

        stopwatch.stop();

//        // Show
//        tree.show();
//        System.out.println("---");
//        tree.tree(1);
//        System.out.println("---");
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

        executorService.shutdown();
    }

    private static class PairObj {
        private final List<Block> blocks;
        private final boolean isSucceed;

        PairObj(List<Block> blocks, boolean isSucceed) {
            this.blocks = blocks;
            this.isSucceed = isSucceed;
        }
    }
}
