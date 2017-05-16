package _experimental.allcomb;

import common.Stopwatch;
import common.buildup.BuildUp;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;
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
import core.field.SmallField;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final char EMPTY_CHAR = '_';
    private static final char EXISTS_CHAR = 'X';
    private static final int WIDTH = 3;

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        int height = 4;

        // ミノのリストを作成する
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, WIDTH, height);
        List<SeparableMino> minos = factory.create();

        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(minos, height);
        BasicSolutions solutions = calculator.calculate();

        stopwatch.stop();
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));


        System.out.println("========");

        Set<MinoField> minoFields = solutions.get(new ColumnSmallField());
        HashSet<ColumnField> nextOuter = new HashSet<>();
        Field initField = FieldFactory.createField("" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                ""
        );
        int maxBit = new Bit(WIDTH, height).maxBit;
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
}
