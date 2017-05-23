package _experimental.allcomb;

import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.UsingBlockAndValidKeyMementoFilter;
import _experimental.allcomb.solutions.BasicSolutions;
import _experimental.allcomb.solutions.BasicSolutionsCalculator;
import _experimental.allcomb.task.Field4x10MinoPackingHelper;
import _experimental.allcomb.task.Result;
import _experimental.allcomb.task.TaskResultHelper;
import _experimental.newfield.LockedReachableThreadLocal;
import common.OperationWithKeyHelper;
import common.Stopwatch;
import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.iterable.CombinationIterable;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import pack.separable_mino.SeparableMino;
import pack.separable_mino.SeparableMinoFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int WIDTH = 3;
    private static final int HEIGHT = 4;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();

        Field initField = FieldFactory.createField("" +
                "______XXXX" +
                "______XXXX" +
                "______XXXX" +
                "______XXXX" +
                ""
        );

        // ミノのリストを作成する
        SizedBit sizedBit = new SizedBit(WIDTH, HEIGHT);
        List<SeparableMino> minos = createSeparableMinos(sizedBit);

        // 検索条件を決める
        MementoFilter mementoFilter = createMementoFilter(initField, sizedBit);

        // 基本パターンを作る
        // TODO: 結果を外部ファイルにキャッシュする
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(minos, sizedBit);
        BasicSolutions solutions = calculator.calculate(mementoFilter);

        // 基本パターン生成にかかった時間を表示
        stopwatch1.stop();
        System.out.println(stopwatch1.toMessage(TimeUnit.SECONDS));

        System.out.println("========");

        Stopwatch stopwatch2 = Stopwatch.createStartedStopwatch();

        // 探索フィールドを3x4の範囲に変換する
        // TODO: 壁をみつけて分割統治
        // TODO: すでにフィールドが埋まっている場合は探索しない
        List<InOutPairField> inOutPairFields = createInOutPairFields(HEIGHT, initField);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        PackSearcher searcher = new PackSearcher(inOutPairFields, solutions, sizedBit, mementoFilter, taskResultHelper);

        // リスト化するとき
//        List<Result> results = searcher.toList();
//        System.out.println(results.size());

        // ファイルに書き出すとき
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./listup/output", false), StandardCharsets.UTF_8))) {

            searcher.forEach(result -> {
                LinkedList<OperationWithKey> operations = result.getMemento().getOperations();
                operations.sort(OperationWithKeyComparator::compareOperationWithKey);
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

    private static List<SeparableMino> createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        return factory.create();
    }

    private static MementoFilter createMementoFilter(Field initField, SizedBit sizedBit) {
        // TODO: ミノの制限をちゃんとする
        HashSet<Long> validBlockCounters = new HashSet<>();
        List<Block> usingBlocks = new ArrayList<>();
        usingBlocks.addAll(Arrays.asList(Block.values()));
//        usingBlocks.addAll(Arrays.asList(Block.values()));
        for (int size = 1; size <= 7; size++) {
            CombinationIterable<Block> combinationIterable = new CombinationIterable<>(usingBlocks, size);
            for (List<Block> blocks : combinationIterable) {
                BlockCounter counter = new BlockCounter(blocks);
                validBlockCounters.add(counter.getCounter());
            }
        }
        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(HEIGHT);
//        return new ValidKeyMementoFilter(initField, reachableThreadLocal, sizedBit.getHeight());
        return new UsingBlockAndValidKeyMementoFilter(initField, validBlockCounters, reachableThreadLocal, sizedBit.getHeight());
//        MementoFilter mementoFilter = new NoDeleteLineMementoFilter(initField, reachableThreadLocal, sizedBit.HEIGHT);
    }

    private static List<InOutPairField> createInOutPairFields(int height, Field initField) {
        Field field = initField.freeze(height);
        InOutPairField pairField1 = parse(field, WIDTH, height);

        field.slideLeft(3);

        InOutPairField pairField2 = parse(field, WIDTH, height);

        field.slideLeft(3);
        for (int y = 0; y < height; y++)
            for (int x = 4; x < WIDTH * 2; x++)
                field.setBlock(x, y);

        InOutPairField pairField3 = parse(field, WIDTH, height);

        return Arrays.asList(pairField1, pairField2, pairField3);
    }

    private static InOutPairField parse(Field field, int width, int height) {
        ColumnSmallField innerField = new ColumnSmallField();
        ColumnSmallField outerField = new ColumnSmallField();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!field.isEmpty(x, y))
                    innerField.setBlock(x, y, height);
            }
            for (int x = width; x < width * 2; x++) {
                if (!field.isEmpty(x, y))
                    outerField.setBlock(x, y, height);
            }
        }
        return new InOutPairField(innerField, outerField);
    }
}