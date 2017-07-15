package _experimental.perfect11;

import lib.Stopwatch;
import common.parser.OperationWithKeyInterpreter;
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
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.TaskResultHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class All10Mino {
    private static int counter = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, ? extends MinoFields> calculate = calculator.calculate();

        Field field = FieldFactory.createField(height);
        System.out.println(FieldView.toString(field, height));

        System.out.println("===");

        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, reachableThreadLocal, sizedBit);

        BasicSolutions basicSolutions = new MappedBasicSolutions(calculate, solutionFilter);
        PackSearcher searcher = createSearcher(sizedBit, basicSolutions, field, solutionFilter);

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        ExecutorService service = Executors.newSingleThreadExecutor();
        File outputFile = new File("output/all10mino.csv");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            searcher.forEach(result -> {
                String line = OperationWithKeyInterpreter.parseToString(result.getMemento().getOperationsStream(width));
                service.submit(() -> {
                    All10Mino.counter += 1;
                    try {
                        writer.write(line);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                });
            });
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        service.shutdown();

        stopwatch.stop();

        System.out.println(stopwatch.toMessage(TimeUnit.MINUTES));
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
        System.out.println("solutions = " + counter);
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
