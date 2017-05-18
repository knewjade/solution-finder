package _experimental.allcomb;

import common.Stopwatch;
import common.datastore.BlockField;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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

//        Set<MinoField> minoFields = solutions.get(new ColumnSmallField());
//        HashSet<ColumnField> nextOuter = new HashSet<>();
        Field initField = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "__________" +
//                        "_______XXX" +
//                        "_______XXX" +
//                        "_______XXX" +
//                        "_______XXX" +
//                "___XXXXXXX" +
//                "___XXXXXXX" +
                ""
        );
        List<InOutPairField> inOutPairFields = createInOutPairFields(height, initField);
        Bit bit = new Bit(WIDTH, height);
//        search(inOutPairFields, 0, inOutPairFields.get(0).getInnerField(), solutions, bit);
        System.out.println(inOutPairFields);
        Searcher searcher = new Searcher(inOutPairFields, solutions, bit);
        searcher.search();
    }

    private static void search(List<InOutPairField> inOutPairFields, int index, ColumnField innerField, BasicSolutions solutions, Bit bit) {
        if (inOutPairFields.size() == index) {
            // solution
            System.out.println("Found");
            return;
        }
        InOutPairField inOutPairField = inOutPairFields.get(index);
        ColumnField outerField = inOutPairField.getOuterField();

        if (innerField.getBoard(0) == bit.fillBoard) {
            int nextIndex = index + 1;

            if (inOutPairFields.size() == nextIndex) {
                // 探索しなくても、I-Left(9,1)をいれるかいれないかだけで判断可能
                ColumnField currentOuterField = inOutPairFields.get(index).getOuterField();
                ColumnSmallField nextInnerField = new ColumnSmallField(currentOuterField.getBoard(0) >> bit.maxBit);
                search(inOutPairFields, nextIndex, nextInnerField, solutions, bit);
            } else {
                ColumnField nextInnerField = inOutPairFields.get(nextIndex).getInnerField();
                search(inOutPairFields, nextIndex, nextInnerField, solutions, bit);
            }

            return;
        }

        Set<MinoField> minoFields = solutions.get(innerField);
        assert minoFields != null : innerField.getBoard(0);
        for (MinoField minoField : minoFields) {
            ColumnField minoOuterField = minoField.getOuterField();
            // 注目範囲外outerで重なりがないか確認
            if (outerField.canMerge(minoOuterField)) {
                // 有効なおきかた
                ColumnField mergedOuterField = outerField.freeze(bit.height);
                mergedOuterField.merge(minoOuterField);

                ColumnSmallField nextInnerField = new ColumnSmallField(mergedOuterField.getBoard(0) >> bit.maxBit);
                search(inOutPairFields, index + 1, nextInnerField, solutions, bit);
            }
        }
    }

    private static List<InOutPairField> createInOutPairFields(int height, Field initField) {
        Field field = initField.freeze(height);
        InOutPairField pairField1 = parse(field, WIDTH, height);

        field.slideLeft(3);

        InOutPairField pairField2 = parse(field, WIDTH, height);

        field.slideLeft(3);
        for (int y = 0; y < height; y++) {
            for (int x = 4; x < WIDTH * 2; x++) {
                field.setBlock(x, y);
            }
        }

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

    private static String toString(ColumnField field, int maxWidth, int maxHeight) {
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

    private static class Searcher {
        private final List<InOutPairField> inOutPairFields;
        private final BasicSolutions solutions;
        private final Bit bit;
        private final int lastIndex;

        public Searcher(List<InOutPairField> inOutPairFields, BasicSolutions solutions, Bit bit) {
            this.inOutPairFields = inOutPairFields;
            this.solutions = solutions;
            this.bit = bit;
            this.lastIndex = inOutPairFields.size() - 1;
        }

        public void search() {
            ForkJoinPool pool = new ForkJoinPool();
            MyTask myTask = new MyTask(this, inOutPairFields.get(0).getInnerField(), 0);
            Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
            long count = pool.invoke(myTask).count();
            stopwatch.stop();
            System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
            System.out.println(count);
            pool.shutdown();
        }
    }

    private static class MyTask extends RecursiveTask<Stream<Result>> {
        private static final ForkJoinTask<Stream<Result>> EMPTY_TASK = new EmptyMyTask();

        private final Searcher searcher;
        private final ColumnField innerField;
        private final int index;

        public MyTask(Searcher searcher, ColumnField innerField, int index) {
            this.searcher = searcher;
            this.innerField = innerField;
            this.index = index;
        }

        @Override
        protected Stream<Result> compute() {
            if (innerField.getBoard(0) == searcher.bit.fillBoard) {
                // innerFieldが埋まっている
                if (index == searcher.lastIndex) {
                    // 最後の計算
                    ColumnField lastOuterField = searcher.inOutPairFields.get(index).getOuterField();
                    return last(lastOuterField);
                } else {
                    // 途中の計算  // 自分で計算する
                    int nextIndex = index + 1;
                    ColumnField nextInnerField = searcher.inOutPairFields.get(nextIndex).getInnerField();
                    return new MyTask(searcher, nextInnerField, nextIndex).compute();
                }
            } else {
                Set<MinoField> minoFields = searcher.solutions.get(innerField);
                assert minoFields != null : innerField.getBoard(0);

                // innerFieldが埋まっていない
                if (index == searcher.lastIndex) {
                    // 最後の計算
                    return minoFields.stream()
                            .flatMap(this::splitLast);
                } else if (index == 0) {
                    // TODO: 最初のフィールドが埋まっていたりすると分割できないため、フラグを保持し、10以上のときなどに分割する
                    // 最初の計算
                    return minoFields.stream()
                            .map(this::split)
                            .filter(this::isValidTask)
                            .map(ForkJoinTask::fork)
                            .flatMap(ForkJoinTask::join);
                } else {
                    // 途中の計算
                    return minoFields.stream()
                            .map(this::split)
                            .filter(this::isValidTask)
                            .flatMap(ForkJoinTask::invoke);
                }
            }
        }

        private ForkJoinTask<Stream<Result>> split(MinoField minoField) {
            ColumnField outerField = searcher.inOutPairFields.get(index).getOuterField();
            ColumnField minoOuterField = minoField.getOuterField();
            // 注目範囲外outerで重なりがないか確認
            if (outerField.canMerge(minoOuterField)) {
                // 有効なおきかた
                ColumnField mergedOuterField = outerField.freeze(searcher.bit.height);
                mergedOuterField.merge(minoOuterField);

                ColumnSmallField nextInnerField = new ColumnSmallField(mergedOuterField.getBoard(0) >> searcher.bit.maxBit);
                return new MyTask(searcher, nextInnerField, index + 1);
            }
            return EMPTY_TASK;
        }

        private boolean isValidTask(ForkJoinTask<Stream<Result>> task) {
            return task != EMPTY_TASK;
        }

        private Stream<Result> splitLast(MinoField minoField) {
            ColumnField outerField = searcher.inOutPairFields.get(index).getOuterField();
            ColumnField minoOuterField = minoField.getOuterField();
            // 注目範囲外outerで重なりがないか確認
            if (outerField.canMerge(minoOuterField)) {
                // 有効なおきかた
                ColumnField mergedOuterField = outerField.freeze(searcher.bit.height);
                mergedOuterField.merge(minoOuterField);
                return last(mergedOuterField);
            }
            return Stream.empty();
        }

        private Stream<Result> last(ColumnField lastOuterField) {
            // outerの空きは2パターンしかない
            long board = lastOuterField.getBoard(0) >> searcher.bit.maxBit;
            if (board == searcher.bit.fillBoard) {
                // 最後が埋まっているので、この時点で確定
                return Stream.of(new Result());
            } else if (board == searcher.bit.oneLineEmpty) {
                // 最後1列だけ空いているので、Iを縦にいれて確定
                return Stream.of(new Result());
            }
            System.out.println(Main.toString(lastOuterField, 6, 4));
            assert false : lastOuterField.getBoard(0);
            return Stream.empty();
        }
    }

    private static class EmptyMyTask extends RecursiveTask<Stream<Result>> {
        @Override
        protected Stream<Result> compute() {
            return Stream.empty();
        }
    }

    private static class Result {
    }

}
