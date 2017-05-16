package _experimental.allcomb;

import _experimental.newfield.Main3;
import common.Stopwatch;
import common.buildup.BuildUp;
import common.datastore.BlockField;
import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.action.reachable.LockedReachable;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.SmallField;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.path.BlockFieldOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final char EMPTY_CHAR = '_';
    private static final char EXISTS_CHAR = 'X';

    public static void main(String[] args) {
        int width = 3;
        int height = 4;
        int maxBit = height * width;
        long fillBoard = (1L << maxBit) - 1L;

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, width, height);
        ArrayList<SeparableMino> minos = new ArrayList<>();
        for (Block block : Block.values()) {
            minos.addAll(factory.create(block));
        }

        HashMap<ColumnField, ColumnFieldConnections> connectionsMap = new HashMap<>();
        connectionsMap.put(new ColumnSmallField(fillBoard), ColumnFieldConnections.FILLED);

        List<ColumnSmallField> collect = LongStream.range(0, fillBoard)
                .boxed()
                .sorted(Comparator.comparingLong(Long::bitCount).reversed())
                .map(ColumnSmallField::new)
                .collect(Collectors.toList());

        HashMap<Long, Field> fieldMapForInner = new HashMap<>();
        HashMap<Long, Field> fieldMapForOuter = new HashMap<>();
        fieldMapForOuter.put(fillBoard << maxBit, new SmallField());

        for (ColumnSmallField columnField : collect) {
            long board = columnField.getBoard(0);

            SmallField innerField = new SmallField();
            SmallField outerField = new SmallField();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (columnField.isEmpty(x, y, height)) {
                        outerField.setBlock(x + width, y);
                    } else {
                        innerField.setBlock(x, y);
                    }
                }
            }

            fieldMapForInner.put(board, innerField);
            fieldMapForOuter.put(board << maxBit, outerField);

            ArrayList<ColumnFieldConnection> connectionList = new ArrayList<>();
            for (SeparableMino mino : minos) {
                ColumnField minoField = mino.getField();
                if (columnField.canMerge(minoField)) {
                    ColumnField freeze = columnField.freeze(height);
                    freeze.merge(minoField);

                    ColumnFieldConnection connection = new ColumnFieldConnection(mino, freeze, height);
                    connectionList.add(connection);
                }
            }
            connectionsMap.put(columnField, new ColumnFieldConnections(connectionList));
        }

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        HashMap<ColumnField, Set<MinoField>> resultsMap = new HashMap<>();
        ColumnSmallField emptyField = new ColumnSmallField();
        int counter = 0;
        for (ColumnField columnField : collect) {
            counter++;
            System.out.println(counter);
            System.out.println(toString(columnField, width, height));
            Obj obj = new Obj(connectionsMap, resultsMap, emptyField, fieldMapForInner, fieldMapForOuter, height);
            Set<MinoField> results = obj.a(columnField);
            System.out.println(results.size());
            System.out.println("#######");
        }
        stopwatch.stop();
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));


        System.out.println("========");

        Set<MinoField> minoFields = resultsMap.get(new ColumnSmallField());
        HashSet<ColumnField> nextOuter = new HashSet<>();
        Field initField = FieldFactory.createField("" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                ""
        );
        ColorConverter colorConverter = new ColorConverter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable lockedReachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
        int counter2 = 0;
        for (MinoField minoField : minoFields) {
            long l = minoField.getOuterField().getBoard(0) >> maxBit;
            if (l == 0L) {
                List<OperationWithKey> operations = minoField.getOperations();
                if (BuildUp.existsValidBuildPattern(initField, operations, height, lockedReachable)) {
                    boolean isLineDeleted = false;
                    for (OperationWithKey operation : operations) {
                        if (operation.getNeedDeletedKey() != 0L) {
                            isLineDeleted = true;
                            break;
                        }
                    }
                    if (isLineDeleted)
                        continue;

                    counter2++;
                    List<Block> blocks = operations.stream().map(operationWithKey -> operationWithKey.getMino().getBlock()).collect(Collectors.toList());
                    TetfuElement tetfuElement = parseBlockFieldToTetfuElement(initField, colorConverter, minoField.getBlockField(), blocks);
                    Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                    String encode = tetfu.encode(Collections.singletonList(tetfuElement));
                    System.out.printf("http://fumen.zui.jp/?v115@%s%n", encode);
                }
            }
            nextOuter.add(minoField.getOuterField());
        }
        System.out.println(counter2);
        System.out.println(nextOuter.size());
    }

    public static String toString(ColumnField field, int maxWidth, int maxHeight) {
        StringBuilder builder = new StringBuilder();
        for (int y = maxHeight - 1; y >= 0; y--) {
            for (int x = 0; x < maxWidth; x++)
                builder.append(field.isEmpty(x, y, maxHeight) ? EMPTY_CHAR : EXISTS_CHAR);

            if (y != 0)
                builder.append(LINE_SEPARATOR);
        }
        return builder.toString();
    }

    private static class Obj {
        private final Map<ColumnField, ColumnFieldConnections> connectionsMap;
        private final Map<ColumnField, Set<MinoField>> resultsMap;
        private final ColumnField initOuterField;
        private final HashMap<Long, Field> fieldMapForInner;
        private final HashMap<Long, Field> fieldMapForOuter;
        private final int height;
        private final LinkedList<SeparableMino> minos = new LinkedList<>();

        private final HashSet<MinoField> results = new HashSet<>();
        private SmallField wallField;

        public Obj(Map<ColumnField, ColumnFieldConnections> connectionsMap, Map<ColumnField, Set<MinoField>> resultsMap, ColumnField outerField, HashMap<Long, Field> fieldMapForInner, HashMap<Long, Field> fieldMapForOuter, int height) {
            this.connectionsMap = connectionsMap;
            this.resultsMap = resultsMap;
            this.initOuterField = outerField;
            this.fieldMapForInner = fieldMapForInner;
            this.fieldMapForOuter = fieldMapForOuter;
            this.height = height;
        }

        public Set<MinoField> a(ColumnField columnField) {
            assert results.isEmpty();

            SmallField smallField = new SmallField();
            for (int y = 0; y < height; y++) {
                for (int x = 6; x < 10; x++) {
                    smallField.setBlock(x, y);
                }
            }
            smallField.merge(fieldMapForInner.get(columnField.getBoard(0)));
            this.wallField = smallField;

            aRec(columnField, initOuterField);
            resultsMap.put(columnField, results);
            return results;
        }

        private void aRec(ColumnField columnField, ColumnField outerField) {
            ColumnFieldConnections connections = connectionsMap.get(columnField);

            // 全てが埋まったとき、解とする
            if (ColumnFieldConnections.isFilled(connections)) {
                Field freeze = wallField.freeze(height);
                freeze.merge(fieldMapForOuter.get(outerField.getBoard(0)));

                List<OperationWithKey> operations = toOperationWithKeys(minos);
                if (BuildUp.existsValidBuildPattern(freeze, operations, height, new AllReachable())) {
                    MinoField result = new MinoField(operations, outerField.freeze(height), height);
                    results.add(result);
                }

                return;
            }

            Set<MinoField> minoFieldSet = resultsMap.getOrDefault(columnField, null);
            if (minoFieldSet != null) {
                // 解なし
                if (minoFieldSet.isEmpty())
                    return;

                ColumnField needSpaceField = outerField.freeze(height);
                needSpaceField.reduce(initOuterField);
                for (MinoField minoField : minoFieldSet) {
                    ColumnField o = minoField.getOuterField();
                    // outerに隙間があるとき
                    if (o.canMerge(needSpaceField)) {
                        List<OperationWithKey> operations = toOperationWithKeys(this.minos);
                        operations.addAll(minoField.getOperations());

                        ColumnField freeze = o.freeze(height);
                        freeze.merge(needSpaceField);

                        Field freeze2 = wallField.freeze(height);
                        freeze2.merge(fieldMapForOuter.get(freeze.getBoard(0)));

                        if (BuildUp.existsValidBuildPattern(freeze2, operations, height, new AllReachable())) {
                            MinoField result = new MinoField(operations, freeze, height);
                            results.add(result);
                        }
                    }
                }
                return;
            }

            for (ColumnFieldConnection connection : connections.getConnections()) {
                ColumnField nextOuterField = connection.getOuterField();
                if (nextOuterField.canMerge(outerField)) {
                    minos.addLast(connection.getMino());
                    ColumnField innerField = connection.getInnerField();
                    nextOuterField.merge(outerField);
                    aRec(innerField, nextOuterField);
                    nextOuterField.reduce(outerField);
                    minos.pollLast();
                }
            }
        }
    }

    private static List<OperationWithKey> toOperationWithKeys(List<SeparableMino> minos) {
        ArrayList<OperationWithKey> operations = new ArrayList<>();
        for (SeparableMino mino : minos) {
            OperationWithKey key = new OperationWithKey(mino.getMino(), mino.getX(), mino.getDeleteKey(), mino.getLowerY());
            operations.add(key);
        }
        return operations;
    }

    private static TetfuElement parseBlockFieldToTetfuElement(Field initField, ColorConverter colorConverter, BlockField blockField, List<Block> blocks) {
        ColoredField coloredField = ColoredFieldFactory.createField(24);
        fillInField(coloredField, ColorType.Gray, initField);

        for (Block block : Block.values()) {
            Field target = blockField.get(block);
            ColorType colorType = colorConverter.parseToColorType(block);
            fillInField(coloredField, colorType, target);
        }

        String blocksStr = blocks.toString();
        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, blocksStr);
    }

    private static void fillInField(ColoredField coloredField, ColorType colorType, Field target) {
        for (int y = 0; y < target.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
            }
        }
    }

    private static class MinoField implements Comparable<MinoField> {
        private final List<OperationWithKey> operations;
        private final ColumnField outerField;
        private final BlockField blockField;

        public MinoField(List<OperationWithKey> operations, ColumnField outerField, int height) {
            this.operations = operations;
            this.outerField = outerField;
            BlockField blockField = new BlockField(4);
            for (OperationWithKey operation : operations) {
                SmallField smallField = new SmallField();
                Mino mino = operation.getMino();
                smallField.putMino(mino, operation.getX(), operation.getY());
                smallField.insertWhiteLineWithKey(operation.getNeedDeletedKey());
                blockField.merge(smallField, mino.getBlock());
            }
            this.blockField = blockField;
        }

        public ColumnField getOuterField() {
            return outerField;
        }

        public List<OperationWithKey> getOperations() {
            return operations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MinoField minoField = (MinoField) o;
            return blockField.equals(minoField.blockField);
        }

        @Override
        public int hashCode() {
            return blockField.hashCode();
        }

        @Override
        public int compareTo(MinoField o) {
            return blockField.compareTo(o.blockField);
        }

        public BlockField getBlockField() {
            return blockField;
        }
    }

    private static class AllReachable implements Reachable {
        @Override
        public boolean checks(Field field, Mino mino, int x, int y, int appearY) {
            return field.isOnGround(mino, x, y);
        }
    }
}
