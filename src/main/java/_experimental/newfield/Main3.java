package _experimental.newfield;

import _experimental.newfield.step1.ColumnParityLimitation;
import _experimental.newfield.step1.DeltaLimitedMino;
import _experimental.newfield.step1.EstimateBuilder;
import _experimental.newfield.step2.FullLimitedMino;
import _experimental.newfield.step2.PositionLimitParser;
import _experimental.newfield.step3.CrossBuilder;
import common.OperationWithKeyHelper;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main3 {
    public static void main(String[] args) {
        Field field = FieldFactory.createField("" +
                "X_______XX" +
                "X_______XX" +
                "X_______XX" +
                "X_______XX"
        );
        int maxClearLine = 4;

        Field verifyField = field.freeze(maxClearLine);
        System.out.println(FieldView.toString(field, maxClearLine));

        Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();
        stopwatch.start();

        Set<List<Block>> sets = new HashSet<>();
        List<Block> allBlocks = Arrays.asList(Block.values());
        List<Block> blocks = new ArrayList<>();
        blocks.addAll(allBlocks);
        int popCount = (maxClearLine * 10 - field.getNumOfAllBlocks()) / 4;
        CombinationIterable<Block> combinationIterable = new CombinationIterable<>(blocks, popCount);
        for (List<Block> blockList : combinationIterable) {
            blockList.sort(Comparator.comparingInt(allBlocks::indexOf));
            sets.add(blockList);
        }

        TreeSet<Obj> allObjSet = new TreeSet<>();
        int counter = 0;
        for (List<Block> usedBlocks : sets) {
            counter++;
            System.out.println(usedBlocks);
            System.out.println(counter + " / " + sets.size());
            List<List<OperationWithKey>> operationsWithKey = search(usedBlocks, field, maxClearLine, verifyField);
            List<Obj> objs = operationsWithKey.stream()
                    .map(operationWithKeys -> {
                        boolean isDeleted = false;
                        BlockField blockField = new BlockField(maxClearLine);
                        for (OperationWithKey key : operationWithKeys) {
                            Field test = FieldFactory.createField(maxClearLine);
                            Mino mino = key.getMino();
                            test.putMino(mino, key.getX(), key.getY());
                            test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                            blockField.merge(test, mino.getBlock());

                            if (key.getNeedDeletedKey() != 0L)
                                isDeleted = true;
                        }
                        return new Obj(usedBlocks, blockField, isDeleted, operationWithKeys);
                    })
                    .collect(Collectors.toList());

            System.out.println(objs.size());
            objs.stream()
                    .map(obj -> OperationWithKeyHelper.parseToString(obj.operations))
                    .sorted().sequential()
                    .forEach(System.out::println);

            allObjSet.addAll(objs);
        }
        System.out.println(allObjSet.size());
        System.exit(0);

        ArrayList<Obj> allObjs = new ArrayList<>(allObjSet);
        allObjs.sort(Main3::blockListComparator);

//            System.out.println(operationsWithKey.size());

        ColorConverter colorConverter = new ColorConverter();

        System.out.println("<h3>各ミノ最大1つずつ & ライン消去なし</h3>");
        Predicate<Obj> objPredicate = obj -> !obj.isDouble && !obj.isDeleted;
        List<TetfuElement> oneNoDelete = createTetfuElements(field, allObjs, colorConverter, objPredicate);
        viewTetfu(oneNoDelete);

        System.out.println("<h3>同一ミノを2つ利用 & ライン消去なし</h3>");
        Predicate<Obj> objPredicate1 = obj -> obj.isDouble && !obj.isDeleted;
        List<TetfuElement> doubleNoDelete = createTetfuElements(field, allObjs, colorConverter, objPredicate1);
        viewTetfu(doubleNoDelete);

        System.out.println("<h3>各ミノ最大1つずつ & ライン消去あり</h3>");
        Predicate<Obj> objPredicate2 = obj -> !obj.isDouble && obj.isDeleted;
        List<TetfuElement> oneDeleted = createTetfuElements(field, allObjs, colorConverter, objPredicate2);
        viewTetfu(oneDeleted);

        System.out.println("<h3>同一ミノを2つ利用 & ライン消去あり</h3>");
        Predicate<Obj> objPredicate3 = obj -> obj.isDouble && obj.isDeleted;
        List<TetfuElement> doubleDeleted = createTetfuElements(field, allObjs, colorConverter, objPredicate3);
        viewTetfu(doubleDeleted);


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

    private static int blockListComparator(Obj o1, Obj o2) {
        List<Block> blocks1 = o1.blocks;
        List<Block> blocks2 = o2.blocks;
        int size1 = blocks1.size();
        int size2 = blocks2.size();
        int minSize = size1 < size2 ? size1 : size2;

        for (int index = 0; index < minSize; index++) {
            int compare = blocks1.get(index).compareTo(blocks2.get(index));
            if (compare != 0)
                return compare;
        }

        return Integer.compare(size1, size2);
    }

    private static List<TetfuElement> createTetfuElements(Field field, ArrayList<Obj> allObjs, ColorConverter colorConverter, Predicate<Obj> objPredicate) {
        TreeSet<Obj> treeSet = new TreeSet<>(Main3::blockListComparator);
        for (Obj obj : allObjs) {
            if (!objPredicate.test(obj))
                continue;

            boolean add = treeSet.add(obj);
            if (!add) {
                SortedSet<Obj> objs = treeSet.tailSet(obj);
                Obj same = objs.first();
                assert same.blocks.equals(obj.blocks);
                same.duplicate += 1;
            }
        }

        return treeSet.stream()
                .map(obj -> parseBlockFieldToTetfuElement(field, colorConverter, obj))
                .collect(Collectors.toList());
    }

    private static void viewTetfu(List<TetfuElement> elements) {
        if (elements.isEmpty()) {
            System.out.printf("<p>該当なし</p>%n");
        } else {
            MinoFactory minoFactory = new MinoFactory();
            ColorConverter colorConverter = new ColorConverter();
            int sizePerOne = 40;
            int split = ((elements.size() - 1) / sizePerOne) + 1;
            for (int index = 0; index < split; index++) {
                int startIndex = index * sizePerOne;
                int toIndex = index == split - 1 ? elements.size() : startIndex + sizePerOne;
                List<TetfuElement> subList = elements.subList(startIndex, toIndex);
                Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                String encode = tetfu.encode(subList);
                if (split == 1)
                    System.out.printf("<p><a href='http://fumen.zui.jp/?v115@%s' target='_blank'>全 %d パターン</a></p>%n", encode, elements.size());
                else
                    System.out.printf("<p><a href='http://fumen.zui.jp/?v115@%s' target='_blank'>全 %d パターン (%d/%d)</a></p>%n", encode, elements.size(), index + 1, split);
            }
        }
    }

    private static TetfuElement parseBlockFieldToTetfuElement(Field initField, ColorConverter colorConverter, Obj obj) {
        ColoredField coloredField = ColoredFieldFactory.createField(24);
        fillInField(coloredField, ColorType.Gray, initField);

        BlockField blockField = obj.blockField;
        for (Block block : Block.values()) {
            Field target = blockField.get(block);
            ColorType colorType = colorConverter.parseToColorType(block);
            fillInField(coloredField, colorType, target);
        }

        String blocks = obj.blocks.toString();
        if (0 < obj.duplicate)
            blocks += " 他 " + obj.duplicate + "pattern";
        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, blocks);
    }

    private static void fillInField(ColoredField coloredField, ColorType colorType, Field target) {
        for (int y = 0; y < target.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
            }
        }
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

    private static final Comparator<OperationWithKey> OPERATION_WITH_KEY_COMPARATOR = (o1, o2) -> {
        Mino mino1 = o1.getMino();
        Mino mino2 = o2.getMino();

        int compareBlock = mino1.getBlock().compareTo(mino2.getBlock());
        if (compareBlock != 0)
            return compareBlock;

        int compareRotate = mino1.getRotate().compareTo(mino2.getRotate());
        if (compareRotate != 0)
            return compareRotate;

        int compareX = Integer.compare(o1.getX(), o2.getX());
        if (compareX != 0)
            return compareX;

        int compareY = Integer.compare(o1.getY(), o2.getY());
        if (compareY != 0)
            return compareY;

        return Long.compare(o1.getNeedDeletedKey(), o2.getNeedDeletedKey());
    };

    private static class Obj implements Comparable<Obj> {
        private final List<Block> blocks;
        private final BlockField blockField;
        private final boolean isDeleted;
        private final List<OperationWithKey> operations;
        private final boolean isDouble;
        private int duplicate = 0;

        public Obj(List<Block> blocks, BlockField blockField, boolean isDeleted, List<OperationWithKey> operations) {
            operations.sort(OPERATION_WITH_KEY_COMPARATOR);
            this.blocks = blocks;
            this.blockField = blockField;
            this.isDeleted = isDeleted;
            this.operations = operations;

            BlockCounter blockCounter = new BlockCounter(blocks);
            boolean isDouble = false;
            for (Block block : Block.values()) {
                if (2 <= blockCounter.getCount(block))
                    isDouble = true;
            }
            this.isDouble = isDouble;
        }

        @Override
        public int compareTo(Obj o) {
            int compareBlockField = blockField.compareTo(o.blockField);
            if (compareBlockField != 0)
                return compareBlockField;

            int compareSize = Integer.compare(operations.size(), o.operations.size());
            if (compareSize != 0)
                return compareSize;

            for (int index = 0; index < operations.size(); index++) {
                int compare = OPERATION_WITH_KEY_COMPARATOR.compare(operations.get(index), o.operations.get(index));
                if (compare != 0)
                    return compare;
            }

            return 0;
        }
    }
}
