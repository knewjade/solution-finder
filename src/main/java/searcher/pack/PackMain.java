package searcher.pack;

import common.OperationWithKeyHelper;
import common.Stopwatch;
import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.datastore.pieces.Pieces;
import common.iterable.CombinationIterable;
import common.pattern.PiecesGenerator;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.memento.UsingBlockAndValidKeySolutionFilter;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.BasicSolutions;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.TaskResultHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PackMain {
    private static final int WIDTH = 3;
    private static final int HEIGHT = 4;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<String> allOnHold = Arrays.asList(
                "*p7, *p4",
                "*, *p3, *p7",
                "*, *p7, *p3",
                "*, *p4, *p6",
                "*, *, *p7, *p2",
                "*, *p5, *p5",
                "*, *p2, *p7, *",
                "*, *p6, *p4"
        );

        PiecesGenerator pieces = new PiecesGenerator(allOnHold);
        HashSet<BlockCounter> counters = StreamSupport.stream(pieces.spliterator(), true)
                .map(Pieces::getBlocks)
                .map(BlockCounter::new)
                .collect(Collectors.toCollection(HashSet::new));

        System.out.println(counters.size());

        Field initField = FieldFactory.createField("" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXXXX__" +
                "XXXXXXX___" +
                ""
        );

        // ミノのリストを作成する
        SizedBit sizedBit = new SizedBit(WIDTH, HEIGHT);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);

        // 検索条件を決める
        SolutionFilter solutionFilter = createUsingBlockAndValidKeyMementoFilter(initField, sizedBit, counters);

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();

        // 基本パターンを計算
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new BasicSolutions(calculate, solutionFilter);

        // 基本パターン作成にかかった時間を表示
        stopwatch1.stop();
        System.out.println(stopwatch1.toMessage(TimeUnit.MILLISECONDS));

        System.out.println("========");

        Stopwatch stopwatch2 = Stopwatch.createStartedStopwatch();

        // 探索フィールドを3x4の範囲に変換する
        // TODO: 壁をみつけて分割統治
        // TODO: すでにフィールドが埋まっている場合は探索しない
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(WIDTH, HEIGHT, initField);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        PackSearcher searcher = new PackSearcher(inOutPairFields, solutions, sizedBit, solutionFilter, taskResultHelper);

        // ファイルに書き出すとき
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        File outputFile = new File("./output/pack_result");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), StandardCharsets.UTF_8))) {
            searcher.forEach(result -> {
                List<OperationWithKey> operations = result.getMemento().getOperationsStream(sizedBit.getWidth())
                        .sorted(OperationWithKeyComparator::compareOperationWithKey)
                        .collect(Collectors.toList());
                String operationString = OperationWithKeyHelper.parseToString(operations);

                singleThreadExecutor.submit(() -> {
                    try {
                        writer.write(operationString);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        singleThreadExecutor.shutdown();
        singleThreadExecutor.awaitTermination(1L, TimeUnit.HOURS);  // 十分に長い時間待つ

        // 探索にかかった時間を表示
        stopwatch2.stop();
        System.out.println(stopwatch2.toMessage(TimeUnit.MILLISECONDS));
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private static SolutionFilter createMementoFilter(Field initField, SizedBit sizedBit) {
        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(HEIGHT);
        return new SRSValidSolutionFilter(initField, reachableThreadLocal, sizedBit);
//        SolutionFilter mementoFilter = new NoDeleteLineSolutionFilter(initField, reachableThreadLocal, sizedBit.HEIGHT);
    }

    private static SolutionFilter createUsingBlockAndValidKeyMementoFilter(Field initField, SizedBit sizedBit, HashSet<BlockCounter> counters) {
        HashSet<Long> validBlockCounters = new HashSet<>();

        for (BlockCounter counter : counters) {
            List<Block> usingBlocks = counter.getBlocks();
            for (int size = 1; size <= usingBlocks.size(); size++) {
                CombinationIterable<Block> combinationIterable = new CombinationIterable<>(usingBlocks, size);
                for (List<Block> blocks : combinationIterable) {
                    BlockCounter newCounter = new BlockCounter(blocks);
                    validBlockCounters.add(newCounter.getCounter());
                }
            }
        }

        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new UsingBlockAndValidKeySolutionFilter(initField, validBlockCounters, reachableThreadLocal, sizedBit);
    }
}