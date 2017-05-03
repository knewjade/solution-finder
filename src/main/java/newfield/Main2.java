package newfield;

import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import misc.Build;
import misc.OperationWithKey;
import misc.Stopwatch;
import newfield.step1.ColumnParityLimitation;
import newfield.step1.EstimateBuilder;
import newfield.step2.DeleteKey;
import newfield.step2.FullLimitedMino;
import newfield.step2.PositionLimit;
import newfield.step2.PositionLimitParser;
import newfield.step3.CrossBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class Main2 {
    public static void main(String[] args) {
        // I, O, O, T, T, L, J, S, Z, Z
        List<Block> usedBlocks = Arrays.asList(
                Block.O, Block.T, Block.S, Block.Z, Block.L, Block.J,
                Block.S, Block.Z, Block.I
        );
        BlockCounter blockCounter = new BlockCounter(usedBlocks);
        System.out.println(blockCounter);

        Field field = FieldFactory.createField("" +
                "________X_" +
                "________X_" +
                "________X_" +
                "________X_" +
                ""
        );
        int maxClearLine = 4;
        System.out.println(FieldView.toString(field, maxClearLine));

        ParityField parityField = new ParityField(field);
        System.out.println(parityField);

        Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();
        stopwatch.start();

        ColumnParityLimitation limitation = new ColumnParityLimitation(blockCounter, parityField, maxClearLine);

        MinoFactory minoFactory = new MinoFactory();
        PositionLimitParser positionLimitParser = new PositionLimitParser(minoFactory, maxClearLine);
        LockedReachableThreadLocal threadLocal = new LockedReachableThreadLocal(maxClearLine);

        // TODO: 1つしかないミノの方が良い。Iミノだけが1つの場合 or Iしかない 場合は特別選択
        Block selectedBlock = selectBlock(blockCounter);
//        AtomicInteger counter = new AtomicInteger(0);
        List<Block> filteredBlock = Arrays.asList(Block.S, Block.Z, Block.O);
        List<List<OperationWithKey>> operationsWithKey = limitation.enumerate().parallelStream()
                .map(EstimateBuilder::create)
                .flatMap(Collection::stream)
                .flatMap(deltaLimitedMinos -> {
                    List<List<FullLimitedMino>> collect = deltaLimitedMinos.stream()
                            .filter(deltaLimitedMino -> !filteredBlock.contains(deltaLimitedMino.getBlock()))
                            .map(positionLimitParser::parse)
                            .sorted(Comparator.comparingInt(List::size))
                            .collect(Collectors.toList());

                    int splitIndex = 0;
                    for (int index = 0; index < collect.size(); index++) {
                        List<FullLimitedMino> limitedMinos = collect.get(0);
                        assert !limitedMinos.isEmpty();
                        Block block = limitedMinos.get(0).getMino().getBlock();
                        if (block == selectedBlock)
                            splitIndex = index;
                    }

                    List<List<List<FullLimitedMino>>> collect2 = new ArrayList<>();
                    List<FullLimitedMino> limitedMinos = collect.get(splitIndex);
                    for (FullLimitedMino mino : limitedMinos) {
                        ArrayList<List<FullLimitedMino>> base = new ArrayList<>(collect);
                        base.set(splitIndex, singletonList(mino));
                        base.sort(Comparator.comparingInt(List::size));
                        collect2.add(base);
                    }
                    System.out.println(collect2);
                    return collect2.stream();
                })
                .peek(System.out::println)
                .flatMap(sets -> new CrossBuilder(sets, field, maxClearLine).create().stream())
//                .collect(Collectors.toCollection(TreeSet::new)).parallelStream()
//                .peek(fullLimitedMinos -> {
//                    System.out.println(counter.getAndIncrement());
//                })
//                .peek(System.out::println)
//                .flatMap(sets -> new Search(field, sets, maxClearLine).search().stream())
                .filter(operationWithKeys -> Build.existsValidBuildPattern(field, operationWithKeys, maxClearLine, threadLocal.get()))
//                .peek(System.out::println)
                .collect(Collectors.toList());
        System.out.println(operationsWithKey.size());

        List<List<FullLimitedMino>> lists = Arrays.asList(
                singletonList(create(minoFactory, Block.I, Rotate.Left, PositionLimit.OddX, 0L, 0, 3)),
                singletonList(create(minoFactory, Block.O, Rotate.Spawn, PositionLimit.OddX, 0L, 0, 1)),
                singletonList(create(minoFactory, Block.O, Rotate.Spawn, PositionLimit.OddX, 0L, 2, 3)),
                singletonList(create(minoFactory, Block.L, Rotate.Reverse, PositionLimit.OddX, 0L, 0, 1)),
                singletonList(create(minoFactory, Block.J, Rotate.Reverse, PositionLimit.OddX, 0L, 0, 1)),
                singletonList(create(minoFactory, Block.Z, Rotate.Spawn, PositionLimit.EvenX, 0L, 2, 3)),
                singletonList(create(minoFactory, Block.S, Rotate.Left, PositionLimit.OddX, 1024L, 0, 3)),
                singletonList(create(minoFactory, Block.Z, Rotate.Left, PositionLimit.EvenX, 1024L, 0, 3)),
                singletonList(create(minoFactory, Block.T, Rotate.Spawn, PositionLimit.OddX, 0L, 0, 1)),
                singletonList(create(minoFactory, Block.T, Rotate.Reverse, PositionLimit.EvenX, 0L, 2, 3))
        );
        CrossBuilder crossBuilder = new CrossBuilder(lists, field, maxClearLine);
        List<List<OperationWithKey>> lists1 = crossBuilder.create();
        System.out.println(lists1.size());


//        List<List<OperationWithKey>> search = new Search(FieldFactory.createField(4), Arrays.asList(
//                create(minoFactory, Block.O, Rotate.Spawn, PositionLimit.NoLimit, 0L, 0, 1),
//                create(minoFactory, Block.O, Rotate.Spawn, PositionLimit.NoLimit, 0L, 2, 3),
//                create(minoFactory, Block.I, Rotate.Right, PositionLimit.NoLimit, 0L, 0, 3),
//                create(minoFactory, Block.L, Rotate.Reverse, PositionLimit.NoLimit, 0L, 0, 1),
//                create(minoFactory, Block.J, Rotate.Reverse, PositionLimit.NoLimit, 0L, 0, 1),
//                create(minoFactory, Block.T, Rotate.Spawn, PositionLimit.NoLimit, 0L, 0, 1),
//                create(minoFactory, Block.T, Rotate.Reverse, PositionLimit.NoLimit, 0L, 2, 3),
//                create(minoFactory, Block.Z, Rotate.Right, PositionLimit.NoLimit, 0x400L, 0, 3),
//                create(minoFactory, Block.S, Rotate.Right, PositionLimit.NoLimit, 0x400L, 0, 3)
//        ), maxClearLine).search();
//        System.out.println("--");
//        for (List<OperationWithKey> operationWithKeys : search) {
//            System.out.println(operationWithKeys);
//        }

        stopwatch.stop();

        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

    }

    private static FullLimitedMino create(
            MinoFactory minoFactory, Block block, Rotate rotate, PositionLimit positionLimit, long deleteKey, int lowerY, int upperY
    ) {
        Mino mino = minoFactory.create(block, rotate);
        return FullLimitedMino.create(mino, positionLimit, DeleteKey.create(mino, deleteKey, lowerY, upperY));
    }

    private static Block selectBlock(BlockCounter blockCounter) {
        int minValue = Integer.MAX_VALUE;
        Block minBlock = null;
        for (Block block : Arrays.asList(Block.J, Block.L, Block.T)) {
            int count = blockCounter.getCount(block);
            if (count == 1)
                return block;
            else if (0 < count && count < minValue) {
                minValue = count;
                minBlock = block;
            }
        }

        if (blockCounter.getCount(Block.I) == 1)
            return Block.I;

        return minBlock;
    }
}
