package _experimental.newfield;

import _experimental.newfield.step1.ColumnParityLimitation;
import _experimental.newfield.step1.DeltaLimitedMino;
import _experimental.newfield.step1.EstimateBuilder;
import _experimental.newfield.step2.DeleteKey;
import _experimental.newfield.step2.FullLimitedMino;
import _experimental.newfield.step2.PositionLimit;
import _experimental.newfield.step2.PositionLimitParser;
import _experimental.newfield.step3.CrossBuilder;
import common.Stopwatch;
import common.buildup.BuildUp;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
import common.iterable.CombinationIterable;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

public class Main3 {
    public static void main(String[] args) {
        Field field = FieldFactory.createField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX" +
                "____XXXXXX"
        );
        int maxClearLine = 4;

//        Field verifyField = field;
        Field verifyField = field.freeze(maxClearLine);
        System.out.println(FieldView.toString(field, maxClearLine));

        Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();
        stopwatch.start();

        Set<List<Block>> sets = new HashSet<>();
        List<Block> allBlocks = Arrays.asList(Block.values());
        List<Block> blocks = new ArrayList<>();
        blocks.addAll(allBlocks);
        blocks.addAll(allBlocks);
        CombinationIterable<Block> combinationIterable = new CombinationIterable<>(blocks, 4);
        for (List<Block> blockList : combinationIterable) {
            blockList.sort(Comparator.comparingInt(allBlocks::indexOf));
            sets.add(blockList);
        }
        for (List<Block> usedBlocks2 : sets) {
            List<List<OperationWithKey>> operationsWithKey = search(usedBlocks2, field, maxClearLine, verifyField);
            List<BlockField> blockFields = operationsWithKey.stream()
                    .map(operationWithKeys -> {
                        BlockField blockField = new BlockField(maxClearLine);
                        for (OperationWithKey key : operationWithKeys) {
                            Field test = FieldFactory.createField(maxClearLine);
                            Mino mino = key.getMino();
                            test.putMino(mino, key.getX(), key.getY());
                            test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                            blockField.merge(test, mino.getBlock());
                        }
                        return blockField;
                    })
                    .collect(Collectors.toList());
//            System.out.println(operationsWithKey.size());

            MinoFactory minoFactory = new MinoFactory();
            ColorConverter colorConverter = new ColorConverter();
            for (BlockField blockField : blockFields) {
                ColoredField coloredField = ColoredFieldFactory.createField(24);
                fillInField(coloredField, ColorType.Gray, field);

                for (Block block : Block.values()) {
                    Field target = blockField.get(block);
                    ColorType colorType = colorConverter.parseToColorType(block);
                    fillInField(coloredField, colorType, target);
                }
//            System.out.println(ColoredFieldView.toString(coloredField));
                Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                String encode = tetfu.encode(coloredField, singletonList(TetfuElement.EMPTY));
                System.out.println(String.format("v115@%s", encode));
            }
        }


//        List<List<FullLimitedMino>> lists = Arrays.asList(
//                singletonList(create(minoFactory, Block.I, Rotate.Left, PositionLimit.OddX, 0L, 0, 3)),
//                singletonList(create(minoFactory, Block.O, Rotate.Spawn, PositionLimit.OddX, 0L, 0, 1)),
//                singletonList(create(minoFactory, Block.O, Rotate.Spawn, PositionLimit.OddX, 0L, 2, 3)),
//                singletonList(create(minoFactory, Block.L, Rotate.Reverse, PositionLimit.OddX, 0L, 0, 1)),
//                singletonList(create(minoFactory, Block.J, Rotate.Reverse, PositionLimit.OddX, 0L, 0, 1)),
//                singletonList(create(minoFactory, Block.Z, Rotate.Spawn, PositionLimit.EvenX, 0L, 2, 3)),
//                singletonList(create(minoFactory, Block.S, Rotate.Left, PositionLimit.OddX, 1024L, 0, 3)),
//                singletonList(create(minoFactory, Block.Z, Rotate.Left, PositionLimit.EvenX, 1024L, 0, 3)),
//                singletonList(create(minoFactory, Block.T, Rotate.Spawn, PositionLimit.OddX, 0L, 0, 1)),
//                singletonList(create(minoFactory, Block.T, Rotate.Reverse, PositionLimit.EvenX, 0L, 2, 3))
//        );
//        CrossBuilder crossBuilder = new CrossBuilder(lists, FieldFactory.createField(maxClearLine), maxClearLine);
//        List<List<OperationWithKey>> lists1 = crossBuilder.create();
//        System.out.println(lists1.size());


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

//        MinoFactory minoFactory = new MinoFactory();
//        List<List<FullLimitedMino>> lists = Arrays.asList(
//                singletonList(create(minoFactory, Block.J, Rotate.Right, PositionLimit.OddX, 0L, 0, 2)),
//                singletonList(create(minoFactory, Block.L, Rotate.Left, PositionLimit.EvenX, 1048576, 0, 4))
//        );
//        CrossBuilder crossBuilder = new CrossBuilder(lists, FieldFactory.createField("" +
//                "__XXXXXXXX" +
//                "__XXXXXXXX" +
//                "__XXXXXXXX" +
//                "__XXXXXXXX"
//        ), maxClearLine);
//        List<List<OperationWithKey>> lists1 = crossBuilder.create();
//        for (List<OperationWithKey> operationWithKeys : lists1) {
//            System.out.println(operationWithKeys);
//        }
//        System.out.println(lists1.size());

        stopwatch.stop();

        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

    }

    private static void fillInField(ColoredField coloredField, ColorType colorType, Field target) {
        for (int y = 0; y < target.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
            }
        }
    }

    private static FullLimitedMino create(
            MinoFactory minoFactory, Block block, Rotate rotate, PositionLimit positionLimit, long deleteKey, int lowerY, int upperY
    ) {
        Mino mino = minoFactory.create(block, rotate);
        return FullLimitedMino.create(mino, positionLimit, DeleteKey.create(mino, deleteKey, lowerY, upperY));
    }

    public static List<List<OperationWithKey>> search(List<Block> usedBlocks, Field field, int maxClearLine, Field verifyField) {
        MinoFactory minoFactory = new MinoFactory();
        PositionLimitParser positionLimitParser = new PositionLimitParser(minoFactory, maxClearLine);
        LockedReachableThreadLocal threadLocal = new LockedReachableThreadLocal(maxClearLine);

        ParityField parityField = new ParityField(field);
        BlockCounter blockCounter = new BlockCounter(usedBlocks);
        ColumnParityLimitation limitation = new ColumnParityLimitation(blockCounter, parityField, maxClearLine);

//        System.out.println(parityField);
//        System.out.println(blockCounter);

        return limitation.enumerate().parallelStream()
                .map(EstimateBuilder::create)
                .flatMap(Collection::stream)
//                .peek(System.out::println)
                .flatMap((List<DeltaLimitedMino> deltaLimitedMinos) -> {
                    // 変換 DeltaLimitedMinos to FullLimitedMino
                    List<List<FullLimitedMino>> collect = deltaLimitedMinos.stream()
                            .map(positionLimitParser::parse)
                            .collect(Collectors.toList());

                    // 候補数が小さい順  // 同種のブロックを固めるため
                    List<Block> priority = collect.stream()
                            .sorted(Comparator.comparingInt(List::size))
                            .map(fullLimitedMinos -> fullLimitedMinos.get(0).getMino().getBlock())
                            .collect(Collectors.toList());

                    // ソートする
                    collect.sort((o1, o2) -> {
                        int compare = Integer.compare(priority.indexOf(o1.get(0).getMino().getBlock()), priority.indexOf(o2.get(0).getMino().getBlock()));
                        if (compare != 0)
                            return compare;
                        return -Integer.compare(o1.size(), o2.size());
                    });

                    return Stream.of(collect);
                })
                .limit(Long.MAX_VALUE)
//                .peek(System.out::println)
                .flatMap(sets -> new CrossBuilder(sets, field, maxClearLine).create().stream())
                .filter(operationWithKeys -> BuildUp.existsValidBuildPattern(verifyField, operationWithKeys, maxClearLine, threadLocal.get()))
//                .filter(operationWithKeys -> {
//                    for (OperationWithKey operationWithKey : operationWithKeys) {
//                        if (operationWithKey.getNeedDeletedKey() != 0L)
//                            return false;
//                    }
//                    return true;
//                })
//                .peek(System.out::println)
                .collect(Collectors.toList());
    }
}
