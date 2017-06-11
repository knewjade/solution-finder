package searcher.pack.task;

import searcher.pack.MinoField;
import searcher.pack.InOutPairField;
import searcher.pack.SizedBit;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.memento.MinoFieldMemento;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;

import java.util.List;
import java.util.stream.Stream;

public class MinoPackingTask {
    private static final MinoPackingTask EMPTY_TASK = null;

    private final PackSearcher searcher;
    private final ColumnField innerField;
    private final MinoFieldMemento memento;
    private final int index;

    public MinoPackingTask(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index) {
        this.searcher = searcher;
        this.innerField = innerField;
        this.memento = memento;
        this.index = index;
    }

    public Stream<Result> compute() {
        if (innerField.getBoard(0) == searcher.getSizedBit().getFillBoard()) {
            // innerFieldが埋まっている
            List<InOutPairField> inOutPairFields = searcher.getInOutPairFields();
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                ColumnField lastOuterField = inOutPairFields.get(index).getOuterField();
                MinoFieldMemento nextMemento = memento.skip();
                return searcher.getTaskResultHelper().fixResult(searcher, lastOuterField, nextMemento);
            } else {
                // 途中の計算  // 自分で計算する
                int nextIndex = index + 1;
                ColumnField nextInnerField = inOutPairFields.get(nextIndex).getInnerField();
                MinoFieldMemento nextMemento = memento.skip();
                return createTask(searcher, nextInnerField, nextMemento, nextIndex).compute();
            }
        } else {
            List<MinoField> minoFields = searcher.getSolutions().parse(innerField);

            // innerFieldが埋まっていない
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                return minoFields.parallelStream()
                        .flatMap(this::splitAndFixResult);
            } else {
                // 途中の計算
                return minoFields.parallelStream()
                        .map(this::split)
                        .filter(this::isValidTask)
                        .flatMap(MinoPackingTask::compute);
            }
        }
    }

    private MinoPackingTask createTask(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index) {
        return new MinoPackingTask(searcher, innerField, memento, index);
    }

    private MinoPackingTask split(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            SizedBit sizedBit = searcher.getSizedBit();
            ColumnField mergedOuterField = outerField.freeze(sizedBit.getHeight());
            mergedOuterField.merge(minoOuterField);

            ColumnSmallField nextInnerField = new ColumnSmallField(mergedOuterField.getBoard(0) >> sizedBit.getMaxBitDigit());
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return checkAndCreateTask(nextInnerField, nextMemento, index + 1);
        }

        return EMPTY_TASK;
    }

    private MinoPackingTask checkAndCreateTask(ColumnField innerField, MinoFieldMemento memento, int index) {
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        if (solutionFilter.test(memento))
            return createTask(searcher, innerField, memento, index);
        return EMPTY_TASK;
    }

    private Stream<Result> splitAndFixResult(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            ColumnField mergedOuterField = outerField.freeze(searcher.getSizedBit().getHeight());
            mergedOuterField.merge(minoOuterField);
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return searcher.getTaskResultHelper().fixResult(searcher, mergedOuterField, nextMemento);
        }

        return Stream.empty();
    }

    private boolean isValidTask(MinoPackingTask task) {
        return task != EMPTY_TASK;
    }

    public MinoFieldMemento getMemento() {
        return memento;
    }
}
