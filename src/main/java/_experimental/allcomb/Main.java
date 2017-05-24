package _experimental.allcomb;

import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.UsingBlockAndValidKeyMementoFilter;
import _experimental.allcomb.solutions.BasicSolutions;
import _experimental.allcomb.solutions.BasicSolutionsCalculator;
import _experimental.allcomb.task.Field4x10MinoPackingHelper;
import _experimental.allcomb.task.TaskResultHelper;
import _experimental.newfield.LockedReachableThreadLocal;
import common.OperationWithKeyHelper;
import common.Stopwatch;
import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.iterable.CombinationIterable;
import core.column_field.ColumnField;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final int WIDTH = 3;
    private static final int HEIGHT = 4;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Field initField = FieldFactory.createField("" +
                "_______XXX" +
                "_______XXX" +
                "_______XXX" +
                "_______XXX" +
                ""
        );

        // ミノのリストを作成する
        SizedBit sizedBit = new SizedBit(WIDTH, HEIGHT);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);

        // 検索条件を決める
        MementoFilter mementoFilter = createMementoFilter(initField, sizedBit);

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();

        // 基本パターンを読み込む
        File file = new File("basic");
        BasicSolutions solutions = null;
        if (file.exists()) {
            solutions = readAndCreateSolutions(file, mementoFilter, separableMinos, sizedBit);
        }

        // 基本パターンを読み込めていない場合は、計算し保存する
        if (solutions == null)
            solutions = createAndWriteSolutions(file, mementoFilter, separableMinos, sizedBit);

        // 基本パターン作成にかかった時間を表示
        stopwatch1.stop();
        System.out.println(stopwatch1.toMessage(TimeUnit.MILLISECONDS));

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

    private static BasicSolutions createAndWriteSolutions(File file, MementoFilter mementoFilter, SeparableMinos separableMinos, SizedBit sizedBit) {
        // 基本パターンを計算する
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, Set<MinoField>> calculate = calculator.calculate();

        // ファイルに出力する
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // 高さを出力する
            writer.write("" + sizedBit.getHeight());
            writer.newLine();

            // 各基本パターンを出力する
            for (Map.Entry<ColumnField, Set<MinoField>> entry : calculate.entrySet()) {
                Set<MinoField> value = entry.getValue();
                // 空のときは出力しない
                if (!value.isEmpty()) {
                    // 1行ごとに "キーとなる地形#対応する操作" となる
                    // キーとなる地形を出力
                    ColumnField columnField = entry.getKey();
                    String board = encodeColumnField(columnField);
                    writer.write(board);

                    writer.write('#');

                    // キーに対応する操作結果を出力
                    String operations = value.parallelStream()
                            .map(minoField -> {
                                String collect = minoField.getOperations().stream()
                                        .map(separableMinos::toIndex)
                                        .map(String::valueOf)
                                        .collect(Collectors.joining(","));
                                String outer = encodeColumnField(minoField.getOuterField());
                                return collect + "/" + outer;
                            })
                            .collect(Collectors.joining(";"));
                    writer.write(operations);

                    writer.newLine();
                }
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BasicSolutions(calculate, mementoFilter);
    }

    private static BasicSolutions readAndCreateSolutions(File file, MementoFilter mementoFilter, SeparableMinos separableMinos, SizedBit sizedBit) {
        // ファイルから読み込む
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            // 高さを読み込む
            int height = Integer.valueOf(reader.readLine());
            if (height != sizedBit.getHeight())
                return null;

            HashMap<ColumnField, List<MinoField>> map = reader.lines()
                    .parallel()
                    .map(line -> {
                        // 地形と操作結果に分割する
                        String[] sharpSplit = line.split("#");
                        assert sharpSplit.length == 2;

                        // 地形に変換
                        ColumnField field = decodeToColumnField(sharpSplit[0]);

                        // 操作結果に変換
                        ArrayList<MinoField> minoFields = new ArrayList<>();
                        for (String operationBoard : sharpSplit[1].split(";")) {
                            String[] slashSplit = operationBoard.split("/");
                            assert slashSplit.length == 2;

                            // 操作に変換
                            String operation = slashSplit[0];
                            String[] split1 = operation.split(",");
                            List<OperationWithKey> operations = new ArrayList<>();
                            for (String s2 : split1) {
                                Integer index = Integer.valueOf(s2);
                                SeparableMino mino = separableMinos.getAt(index);
                                operations.add(mino.toOperation());
                            }

                            // 結果に変換
                            ColumnField outer = decodeToColumnField(slashSplit[1]);

                            minoFields.add(new MinoField(operations, outer, height));
                        }

                        return new Pair<>(field, minoFields);
                    })
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (u, u2) -> {
                        // 通常、同じキーの結果は複数個存在しない
                        throw new IllegalStateException("No reachable");
                    }, HashMap::new));

            return new BasicSolutions(map, mementoFilter);
        } catch (Exception e) {
            return null;
        }
    }

    private static ColumnField decodeToColumnField(String s) {
        String[] split = s.split(",");
        switch (split.length) {
            case 1:
                return new ColumnSmallField(Long.valueOf(split[0]));
        }
        throw new IllegalStateException("No reachable");
    }

    private static String encodeColumnField(ColumnField columnField) {
        return IntStream.range(0, columnField.getBoardCount())
                .boxed()
                .map(columnField::getBoard)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
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