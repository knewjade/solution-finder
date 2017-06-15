package _experimental.main;

import common.Stopwatch;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.pack.InOutPairField;
import searcher.pack.RecursiveMinoField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.BasicSolutions;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.TaskResultHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class All10Mino {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, List<RecursiveMinoField>> calculate = calculator.calculate();

        Field field = FieldFactory.createField(height);
        System.out.println(FieldView.toString(field, height));

        System.out.println("===");

        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, reachableThreadLocal, sizedBit);

        BasicSolutions basicSolutions = BasicSolutions.create(calculate, solutionFilter);
        PackSearcher searcher = createSearcher(sizedBit, basicSolutions, field, solutionFilter);

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        Long counter = searcher.callback(Stream::count);

        stopwatch.stop();

        System.out.println(stopwatch.toMessage(TimeUnit.MINUTES));
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
        System.out.println("solutions = " + counter);
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private static PackSearcher createSearcher(SizedBit sizedBit, BasicSolutions basicSolutions, Field initField, SolutionFilter solutionFilter) throws InterruptedException, ExecutionException {
        // フィールドの変換
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);

        // 探索準備
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        return new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
    }
}
