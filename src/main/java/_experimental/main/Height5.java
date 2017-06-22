package _experimental.main;

import common.Stopwatch;
import common.datastore.BlockField;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.BasicSolutions;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.task.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Height5 {
    private static int counter = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();

        Field field = FieldFactory.createField("" +
                "XX_XXXXXXX" +
                "X__XXXXXXX" +
                "___XXXXXXX" +
                "___XXXXXXX" +
                "__XXXXXXXX" +
                "_XXXXXXXXX" +
                ""
        );
        System.out.println(FieldView.toString(field, height));

        System.out.println("===");

        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, reachableThreadLocal, sizedBit);

        BasicSolutions basicSolutions = new BasicSolutions(calculate, solutionFilter);
        PackSearcher searcher = createSearcher(sizedBit, basicSolutions, field, solutionFilter);

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        searcher.callback(resultStream -> {
                    resultStream
                            .limit(100L)
                            .map(Result::getMemento)
                            .map(minoFieldMemento -> minoFieldMemento.getOperationsStream(width))
                            .map(operationWithKeyStream -> {
                                BlockField blockField = new BlockField(height);
                                StringBuilder blocks = new StringBuilder();
                                operationWithKeyStream
                                        .sequential()
                                        .sorted(Comparator.comparing(o -> o.getMino().getBlock()))
                                        .forEach(key -> {
                                            Field test = FieldFactory.createField(height);
                                            Mino mino = key.getMino();
                                            test.putMino(mino, key.getX(), key.getY());
                                            test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                                            blockField.merge(test, mino.getBlock());
                                            blocks.append(key.getMino().getBlock());
                                        });
                                return parseBlockFieldToTetfuElement(field, colorConverter, blockField, blocks.toString());
                            })
                            .map(element -> {
                                String encode = new Tetfu(minoFactory, colorConverter).encode(Collections.singletonList(element));
                                return element.getComment() + ": http://fumen.zui.jp/?v115@" + encode;
                            })
                            .forEach(System.out::println);
                    return true;
                }
        );

        stopwatch.stop();

        System.out.println(stopwatch.toMessage(TimeUnit.MINUTES));
        System.out.println(stopwatch.toMessage(TimeUnit.SECONDS));
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
        System.out.println("solutions = " + counter);
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private static PackSearcher createSearcher(SizedBit sizedBit, BasicSolutions basicSolutions, Field initField, SolutionFilter solutionFilter) throws InterruptedException, ExecutionException {
        // フィールドの変換
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);

        // 探索準備
        TaskResultHelper taskResultHelper = createTaskResultHelper(sizedBit);
        return new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
    }

    private static TaskResultHelper createTaskResultHelper(SizedBit sizedBit) {
        if (sizedBit.getWidth() == 3 && sizedBit.getHeight() == 4)
            return new Field4x10MinoPackingHelper();
        return new BasicMinoPackingHelper();
    }

    private static TetfuElement parseBlockFieldToTetfuElement(Field initField, ColorConverter colorConverter, BlockField blockField, String comment) {
        ColoredField coloredField = ColoredFieldFactory.createField(24);
        fillInField(coloredField, ColorType.Gray, initField);

        for (Block block : Block.values()) {
            Field target = blockField.get(block);
            ColorType colorType = colorConverter.parseToColorType(block);
            fillInField(coloredField, colorType, target);
        }

        return new TetfuElement(coloredField, ColorType.Empty, Rotate.Reverse, 0, 0, comment);
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
