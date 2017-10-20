package searcher.pack.task;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import searcher.pack.SizedBit;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_field.MinoField;
import searcher.pack.mino_fields.MinoFields;

import java.util.stream.Stream;

// Width=2のBasicSolutionsのためのタスク
class MinoPackingTaskWidthForWidth2 implements PackingTask {
    private static final PackingTask EMPTY_TASK = null;

    private final PackSearcher searcher;
    private final ColumnField innerField;
    private final ColumnField outerField;
    private final MinoFieldMemento memento;
    private final int index;

    MinoPackingTaskWidthForWidth2(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index) {
        this(searcher, innerField, searcher.getInOutPairFields().get(index).getOuterField(), memento, index);
    }

    private MinoPackingTaskWidthForWidth2(PackSearcher searcher, ColumnField innerField, ColumnField outerField, MinoFieldMemento memento, int index) {
        this.searcher = searcher;
        this.innerField = innerField;
        this.outerField = outerField;
        this.memento = memento;
        this.index = index;
    }

    @Override
    public Stream<Result> compute() {
        if (searcher.isFilled(innerField, index)) {
            // innerFieldが埋まっている
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                SizedBit sizedBit = searcher.getSizedBit();
                long innerFieldBoard = outerField.getBoard(0) >> sizedBit.getMaxBitDigit();
                MinoFieldMemento nextMemento = memento.skip();
                return searcher.getTaskResultHelper().fixResult(searcher, innerFieldBoard, nextMemento);
            } else {
                // 途中の計算  // 自分で計算する
                MinoFieldMemento nextMemento = memento.skip();
                long innerFieldBoard = outerField.getBoard(0) >> searcher.getSizedBit().getMaxBitDigit();
                return createTask(searcher, innerFieldBoard, nextMemento, index + 1).compute();
            }
        } else {
            MinoFields minoFields = searcher.getSolutions(index).parse(innerField);

            // innerFieldが埋まっていない
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                return minoFields.stream().parallel()
                        .flatMap(this::splitAndFixResult);
            } else {
                // 途中の計算
                return minoFields.stream().parallel()
                        .map(this::split)
                        .filter(this::isValidTask)
                        .flatMap(PackingTask::compute);
            }
        }
    }

    private PackingTask createTask(PackSearcher searcher, long innerFieldBoard, MinoFieldMemento memento, int index) {
        long fillBoard = searcher.getSizedBit().getFillBoard();
        ColumnSmallField over = ColumnFieldFactory.createField(innerFieldBoard & ~fillBoard);
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();

        if (over.canMerge(outerField)) {
            over.merge(outerField);
            ColumnSmallField innerField = ColumnFieldFactory.createField(innerFieldBoard & fillBoard);
            return new MinoPackingTaskWidthForWidth2(searcher, innerField, over, memento, index);
        }

        return EMPTY_TASK;
    }

    private PackingTask split(MinoField minoField) {
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            SizedBit sizedBit = searcher.getSizedBit();
            ColumnField mergedOuterField = outerField.freeze(sizedBit.getHeight());
            mergedOuterField.merge(minoOuterField);

            long innerFieldBoard = mergedOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return checkAndCreateTask(innerFieldBoard, nextMemento, index + 1);
        }

        return EMPTY_TASK;
    }

    private PackingTask checkAndCreateTask(long innerFieldBoard, MinoFieldMemento memento, int index) {
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        if (solutionFilter.test(memento))
            return createTask(searcher, innerFieldBoard, memento, index);
        return EMPTY_TASK;
    }

    private Stream<Result> splitAndFixResult(MinoField minoField) {
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            SizedBit sizedBit = searcher.getSizedBit();
            ColumnField mergedOuterField = outerField.freeze(searcher.getSizedBit().getHeight());
            mergedOuterField.merge(minoOuterField);

            long innerFieldBoard = mergedOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return searcher.getTaskResultHelper().fixResult(searcher, innerFieldBoard, nextMemento);
        }

        return Stream.empty();
    }

    private boolean isValidTask(PackingTask task) {
        return task != EMPTY_TASK;
    }
}
