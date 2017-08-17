package _experimental.putter;

import common.datastore.BlockCounter;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import lib.Randoms;
import lib.Stopwatch;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PathSpeedTestMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // SRS: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=Mapped
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create
        BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        // Assert
        Randoms randoms = new Randoms();
        Field initField = randoms.field(height, 7);
        SolutionFilter solutionFilterSRS = createSRSSolutionFilter(sizedBit, initField);
        SolutionFilter solutionFilter = new AllPassedSolutionFilter();

        for (int count = 0; count < 30; count++) {
            System.out.println(count);
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);
            PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            List<Result> results = searcher.toList();
        }

        BlockCounter allBlock = new BlockCounter(Block.valueList());

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        List<Result> results = searcher.toList();

        List<MinoFieldMemento> results2 = searcher.stream(resultStream ->
                resultStream
                        .map(Result::getMemento)
                        .collect(Collectors.toList())
        );

        List<MinoFieldMemento> results3 = searcher.stream(resultStream ->
                resultStream
                        .map(Result::getMemento)
                        .filter(memento -> allBlock.equals(memento.getSumBlockCounter()))
                        .filter(solutionFilterSRS::testLast)
                        .collect(Collectors.toList())
        );
        stopwatch.stop();

        System.out.println(results.size());
        System.out.println(results2.size());
        System.out.println(results3.size());
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
    }

    private static BasicSolutions createMappedBasicSolutions(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        return new MappedBasicSolutions(calculate);
    }

    private static SolutionFilter createSRSSolutionFilter(SizedBit sizedBit, Field initField) {
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
    }
}
