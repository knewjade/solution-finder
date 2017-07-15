package _experimental.unused;

import common.tree.AnalyzeTree;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import lib.Stopwatch;
import common.iterable.AllPermutationIterable;
import common.iterable.CombinationIterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static core.mino.Block.*;

public class CheckerMain {
    public static void main(String[] args) {
//        measure();
        enumerate();
//        enumerate2();
    }

    private static void measure() {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, L, J, S, Z, J, L, T);
        int maxClearLine = 4;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

        // Field
        String marks = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "";
        Field field = FieldFactory.createField(marks);

        // Measure
        invoker.measure(field, blocks, 500);
        invoker.show(false);
        invoker.clearStopwatch();

        System.out.println("---");

        invoker.measure(field, blocks, 1000);
        invoker.show(true);
    }

    private static void enumerate() {
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 7;
        int maxClearLine = 4;
        CheckerInvoker invoker = CheckerInvoker.createInstance(maxClearLine);

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        Field field = FieldFactory.createField(marks);

        // enumerate combinations and sort
        ArrayList<List<Block>> allCombinations = new ArrayList<>();
        Iterable<List<Block>> permutations = new CombinationIterable<>(blocks, popCount);
        for (List<Block> permutation : permutations) {
            Iterable<List<Block>> combinations = new AllPermutationIterable<>(permutation);
            for (List<Block> combination : combinations) {
//                combination.add(0, Block.T);
                allCombinations.add(combination);
            }
        }
        allCombinations.sort((o1, o2) -> {
            int size = o1.size();
            int sizeCompare = Integer.compare(size, o2.size());
            if (sizeCompare != 0)
                return sizeCompare;

            for (int index = 0; index < size; index++) {
                int compare = Integer.compare(o1.get(index).getNumber(), o2.get(index).getNumber());
                if (compare != 0)
                    return compare;
            }

            return 0;
        });

        System.out.println(allCombinations.size());

        // Measure
        AnalyzeTree tree = new AnalyzeTree();
//        AnalyzeTree treeFail = new AnalyzeTree();
        for (List<Block> combination : allCombinations) {
            invoker.measure(field, combination, 1);

//                System.out.print(combination + " => ");
//            if (invoker.getLastResult()) {
////                    System.out.println("success");
//                success(combination);
//            } else {
////                    System.out.println("fail");
//                fail(combination);
////                    treeFail.fail(combination);
//            }
        }

        stopwatch.stop();

        // Show
//        invoker.show();
//        tree.show();
//        System.out.println("---");
//        tree(1);
//        System.out.println("---");
//        tree(3);
//
//        System.out.println(PerfectValidator.validateCount);
//        System.out.println(PerfectValidator.allCount);
//        System.out.println((double) PerfectValidator.validateCount / PerfectValidator.allCount);
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
    }

    private static void enumerate2() {
        // Invoker
        List<Block> blocks = Arrays.asList(I, T, S, Z, J, L, O);
        int popCount = 7;
        int maxClearLine = 4;
        CheckmateInvoker invoker = CheckmateInvoker.createPerfectCheckmateUsingHold(maxClearLine);

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        Field field = FieldFactory.createField(marks);

        // enumerate combinations and sort
        ArrayList<List<Block>> allCombinations = new ArrayList<>();
        Iterable<List<Block>> permutations = new CombinationIterable<>(blocks, popCount);
        for (List<Block> permutation : permutations) {
            Iterable<List<Block>> combinations = new AllPermutationIterable<>(permutation);
            for (List<Block> combination : combinations) {
//                combination.add(0, Block.T);
                allCombinations.add(combination);
            }
        }
        allCombinations.sort((o1, o2) -> {
            int size = o1.size();
            int sizeCompare = Integer.compare(size, o2.size());
            if (sizeCompare != 0)
                return sizeCompare;

            for (int index = 0; index < size; index++) {
                int compare = Integer.compare(o1.get(index).getNumber(), o2.get(index).getNumber());
                if (compare != 0)
                    return compare;
            }

            return 0;
        });

        System.out.println(allCombinations.size());

        // Measure
        AnalyzeTree tree = new AnalyzeTree();
//        AnalyzeTree treeFail = new AnalyzeTree();
        for (List<Block> combination : allCombinations) {
            invoker.measure(field, combination, 1);

//                System.out.print(combination + " => ");
            if (0 < invoker.getLastResults().size()) {
//                    System.out.println("success");
                tree.success(combination);
            } else {
//                    System.out.println("fail");
                tree.fail(combination);
//                    treeFail.fail(combination);
            }
        }


        // Show
        invoker.show(true);
        tree.show();
        System.out.println("---");
        tree.tree(1);
        System.out.println("---");
//        tree.tree(3);
    }
}
