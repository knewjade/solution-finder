package _experimental.main;

import lib.Stopwatch;
import common.datastore.OperationWithKey;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.solutions.FilterOnDemandBasicSolutions;
import searcher.pack.task.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

        Field field = FieldFactory.createField("" +
                        "X_________" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_" +
                        "XXXXXX____" +
                        ""
        );
        System.out.println(FieldView.toString(field, height));

        System.out.println("===");

//        ColumnSmallField initOuterField = InOutPairField.createMaxOuterBoard(sizedBit.getWidth(), sizedBit.getHeight(), field);
//        System.out.println(ColumnFieldView.toString(initOuterField, 5, height));

        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, reachableThreadLocal, sizedBit);
        Predicate<ColumnField> predicate = BasicSolutions.createBitCountPredicate(2);
        BasicSolutions basicSolutions = new FilterOnDemandBasicSolutions(separableMinos, sizedBit, predicate, solutionFilter);
        PackSearcher searcher = createSearcher(sizedBit, basicSolutions, field, solutionFilter);

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        List<Result> results = searcher.toList();
        System.out.println(results.size());
        for (Result result : results) {
            System.out.println(result.getMemento().getOperationsStream(width).collect(Collectors.toList()));
            for (OperationWithKey operationWithKey : result.getMemento().getOperationsStream(width).collect(Collectors.toList())) {
                System.out.println(operationWithKey.getMino().getBlock());
            }
        }

        stopwatch.stop();

        System.out.println(stopwatch.toMessage(TimeUnit.MINUTES));
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

        System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
    }

    private static PackSearcher createSearcher(SizedBit sizedBit, BasicSolutions basicSolutions, Field initField, SolutionFilter solutionFilter) throws InterruptedException, ExecutionException {
        // フィールドの変換
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);

        // 探索準備
        TaskResultHelper taskResultHelper = createTaskResultHelper(sizedBit);
        return new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
    }

    private static TaskResultHelper createTaskResultHelper(SizedBit sizedBit) {
        if (sizedBit.getWidth() == 3 && sizedBit.getHeight() == 4)
            return new Field4x10MinoPackingHelper();
        return new BasicMinoPackingHelper();
    }


}
