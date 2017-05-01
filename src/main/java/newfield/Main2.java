package newfield;

import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import misc.Build;
import misc.OperationWithKey;
import misc.Stopwatch;
import newfield.step1.ColumnParityLimitation;
import newfield.step1.DeltaLimitedMino;
import newfield.step1.EstimateBuilder;
import newfield.step2.FullLimitedMino;
import newfield.step2.PositionLimitParser;
import newfield.step3.CrossBuilder;
import newfield.step4.Search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main2 {
    public static void main(String[] args) {
        List<Block> usedBlocks = Arrays.asList(
                Block.L, Block.S, Block.J, Block.I, Block.O, Block.Z, Block.T,Block.O, Block.Z, Block.T
        );
        BlockCounter blockCounter = new BlockCounter(usedBlocks);
        System.out.println(blockCounter);

        Field field = FieldFactory.createField("" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
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

        List<List<OperationWithKey>> operationsWithKey  = limitation.enumerate().parallelStream()
                .map(EstimateBuilder::create)
                .flatMap(Collection::stream)
                .limit(1)
                .peek(lists -> System.out.println(lists))
                .map(deltaLimitedMinos -> deltaLimitedMinos.stream().map(positionLimitParser::parse).sorted(Comparator.comparingInt(List::size)).collect(Collectors.toList()))
                .limit(1)
                .peek(lists -> {
                    for (List<FullLimitedMino> list : lists) {
                        System.out.println(list);
                    }
                })
                .flatMap(sets -> new CrossBuilder(sets, field, maxClearLine).create().stream())
                .flatMap(sets -> new Search(field, sets, maxClearLine).search().stream())
                .limit(10)
                .filter(operationWithKeys -> Build.existsValidBuildPattern(field, operationWithKeys, maxClearLine, threadLocal.get()))
                .peek(System.out::println)
                .collect(Collectors.toList());

        System.out.println(operationsWithKey.size());

        stopwatch.stop();

        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

    }
}
