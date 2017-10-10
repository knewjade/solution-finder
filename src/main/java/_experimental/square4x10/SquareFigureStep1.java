package _experimental.square4x10;

import common.datastore.OperationWithKey;
import common.parser.OperationWithKeyInterpreter;
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
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SquareFigureStep1 {
    private static final int FIELD_WIDTH = 10;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        SizedBit sizedBit = new SizedBit(3, 4);
        int emptyWidth = 8;

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
        SolutionFilter solutionFilter = new AllPassedSolutionFilter();
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        Long count = searcher.count();
        System.out.println(count);
//        // 初期化: ExecutorService
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//
//        // 出力ファイルの準備
//        String name = String.format("output/result_%dx%d.csv", emptyWidth, sizedBit.getHeight());
//        BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(name), Charset.defaultCharset(), StandardOpenOption.CREATE_NEW);
//
//        try {
//            // 実行
//            SquareFigureStep1 step = new SquareFigureStep1(searcher, bufferedWriter, executorService);
//            step.run();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        } finally {
//            executorService.shutdown();
//            executorService.awaitTermination(10L, TimeUnit.DAYS);
//            bufferedWriter.flush();
//            bufferedWriter.close();
//        }

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

    private final PackSearcher searcher;
    private final BufferedWriter bufferedWriter;
    private final ExecutorService executorService;

    private SquareFigureStep1(PackSearcher searcher, BufferedWriter bufferedWriter, ExecutorService executorService) {
        this.searcher = searcher;
        this.bufferedWriter = bufferedWriter;
        this.executorService = executorService;
    }

    private void run() throws ExecutionException, InterruptedException {
        searcher.forEach(result -> executorService.submit(() -> outputResult(result)));
    }

    private void outputResult(Result result) {
        SizedBit sizedBit = searcher.getSizedBit();
        Stream<OperationWithKey> operationsStream = result.getMemento().getOperationsStream(sizedBit.getWidth());
        String str = OperationWithKeyInterpreter.parseToString(operationsStream.collect(Collectors.toList()));

        try {
            bufferedWriter.write(str);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
