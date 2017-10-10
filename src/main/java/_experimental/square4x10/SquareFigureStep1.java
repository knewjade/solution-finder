package _experimental.square4x10;

import common.datastore.OperationWithKey;
import common.parser.OperationWithKeyInterpreter;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import lib.Stopwatch;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.TaskResultHelper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// すべてのパフェパターンをcsvに書き出す
// SRSで置くことができるパターンのみ出力
public class SquareFigureStep1 {
    private static final int FIELD_WIDTH = 10;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        SizedBit sizedBit = new SizedBit(3, 4);
        int emptyWidth = Integer.parseInt(args[0]);

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // フィールドの定義
        Field initField = createSquareEmptyField(emptyWidth, sizedBit.getHeight());
        System.out.println(FieldView.toString(initField));
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);

        // 初期化: BasicSolutions
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        BasicSolutions basicSolutions = createMappedBasicSolutions(minoFactory, minoShifter, sizedBit);

        // 初期化: PackSearcher
        SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        System.out.println(searcher.count());

        // 初期化: ExecutorService
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // 出力ファイルの準備
        String name = String.format("output/result_%dx%d.csv", emptyWidth, sizedBit.getHeight());
        Charset cs = StandardCharsets.UTF_8;

        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(name), false), cs))) {
            // 実行
            SquareFigureStep1 step = new SquareFigureStep1(searcher, bufferedWriter, executorService);
            step.run();

            executorService.shutdown();
            executorService.awaitTermination(10L, TimeUnit.DAYS);
            bufferedWriter.flush();
        }

        stopwatch.stop();
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));
    }

    private static Field createSquareEmptyField(int emptyWidth, int emptyHeight) {
        Field field = FieldFactory.createField(emptyHeight);
        for (int x = emptyWidth; x < FIELD_WIDTH; x++)
            for (int y = 0; y < emptyHeight; y++)
                field.setBlock(x, y);
        return field;
    }

    private static BasicSolutions createMappedBasicSolutions(MinoFactory minoFactory, MinoShifter minoShifter, SizedBit sizedBit) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        return new MappedBasicSolutions(calculate);
    }

    private static SolutionFilter createSRSSolutionFilter(SizedBit sizedBit, Field initField) {
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
    }

    private final PackSearcher searcher;
    private final BufferedWriter bufferedWriter;
    private final ExecutorService executorService;

    private SquareFigureStep1(PackSearcher searcher, BufferedWriter bufferedWriter, ExecutorService executorService) {
        this.searcher = searcher;
        this.bufferedWriter = bufferedWriter;
        this.executorService = executorService;
    }

    private void run() throws ExecutionException, InterruptedException {
        SizedBit sizedBit = searcher.getSizedBit();
        searcher
                .stream(resultStream -> {
                    return resultStream.map(result -> {
                        Stream<OperationWithKey> operationsStream = result.getMemento().getOperationsStream(sizedBit.getWidth());
                        return OperationWithKeyInterpreter.parseToString(operationsStream.collect(Collectors.toList()));
                    });
                })
                .forEach(result -> executorService.submit(() -> outputResult(result)));
    }

    private void outputResult(String line) {
        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
