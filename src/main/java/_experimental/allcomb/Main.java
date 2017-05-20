package _experimental.allcomb;

import _experimental.allcomb.memento.KeyMementoFilter;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.task.Field4x10MinoPackingTask;
import _experimental.allcomb.task.TaskResultHelper;
import _experimental.newfield.LockedReachableThreadLocal;
import common.Stopwatch;
import common.iterable.CombinationIterable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int WIDTH = 3;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        int height = 4;

        // ミノのリストを作成する
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, WIDTH, height);
        List<SeparableMino> minos = factory.create();

        // TODO: 消去ラインのフィルタをするならこの段階
        // TODO: ミノ種類に制限がある場合はここでもする
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(minos, height);
        BasicSolutions solutions = calculator.calculate();

        stopwatch.stop();
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));


        System.out.println("========");

//        Set<MinoField> minoFields = solutions.get(new ColumnSmallField());
//        HashSet<ColumnField> nextOuter = new HashSet<>();
        Field initField = FieldFactory.createField("" +
                "______XXXX" +
                "______XXXX" +
                "______XXXX" +
                "______XXXX" +
                ""
        );
        List<InOutPairField> inOutPairFields = createInOutPairFields(height, initField);
        Bit bit = new Bit(WIDTH, height);
//        search(inOutPairFields, 0, inOutPairFields.get(0).getInnerField(), solutions, bit);
        System.out.println(inOutPairFields);


        // TODO: ミノの制限をちゃんとする
        HashSet<Long> validBlockCounters = new HashSet<>();
        List<Block> usingBlocks = Arrays.asList(Block.values());
        for (int size = 0; size < usingBlocks.size(); size++) {
            CombinationIterable<Block> combinationIterable = new CombinationIterable<>(usingBlocks, size);
            for (List<Block> blocks : combinationIterable) {
                BlockCounter counter = new BlockCounter(blocks);
                validBlockCounters.add(counter.getCounter());
            }
        }
        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(height);
        MementoFilter mementoFilter = new KeyMementoFilter(initField, reachableThreadLocal, bit.height);
//        MementoFilter mementoFilter = new UsingBlockAndKeyMementoFilter(initField, validBlockCounters, reachableThreadLocal, bit.height);
//        MementoFilter mementoFilter = new AllPassedMementoFilter();
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingTask();
        ListUpSearcher searcher = new ListUpSearcher(inOutPairFields, solutions, bit, mementoFilter, taskResultHelper);
        searcher.search();
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