package _experimental.allcomb.task;

import _experimental.allcomb.*;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class MinoPackingTask {
    private static final MinoPackingTask EMPTY_TASK = null;

    private final ListUpSearcher searcher;
    private final MementoFilter mementoFilter;
    private final ColumnField innerField;
    private final MinoFieldMemento memento;
    private final int index;

    public MinoPackingTask(ListUpSearcher searcher, MementoFilter mementoFilter, ColumnField innerField, MinoFieldMemento memento, int index) {
        this.searcher = searcher;
        this.mementoFilter = mementoFilter;
        this.innerField = innerField;
        this.memento = memento;
        this.index = index;
    }

    public Stream<Result> compute() {
        if (innerField.getBoard(0) == searcher.getBit().fillBoard) {
            // innerFieldが埋まっている
            List<InOutPairField> inOutPairFields = searcher.getInOutPairFields();
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                ColumnField lastOuterField = inOutPairFields.get(index).getOuterField();
                MinoFieldMemento nextMemento = memento.skip();
                return searcher.getTaskResultHelper().fixResult(searcher, mementoFilter, lastOuterField, nextMemento);
            } else {
                // 途中の計算  // 自分で計算する
                int nextIndex = index + 1;
                ColumnField nextInnerField = inOutPairFields.get(nextIndex).getInnerField();
                MinoFieldMemento nextMemento = memento.skip();
                return createTask(searcher, mementoFilter, nextInnerField, nextMemento, nextIndex).compute();
            }
        } else {
            Set<MinoField> minoFields = searcher.getSolutions().get(innerField);
            assert minoFields != null : innerField.getBoard(0);

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

    private MinoPackingTask createTask(ListUpSearcher searcher, MementoFilter mementoFilter, ColumnField innerField, MinoFieldMemento memento, int index) {
        return new MinoPackingTask(searcher, mementoFilter, innerField, memento, index);
    }

    private MinoPackingTask split(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            Bit bit = searcher.getBit();
            ColumnField mergedOuterField = outerField.freeze(bit.height);
            mergedOuterField.merge(minoOuterField);

            ColumnSmallField nextInnerField = new ColumnSmallField(mergedOuterField.getBoard(0) >> bit.maxBit);
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return checkAndCreateTask(nextInnerField, nextMemento, index + 1);
        }

        return EMPTY_TASK;
    }

    private MinoPackingTask checkAndCreateTask(ColumnField innerField, MinoFieldMemento memento, int index) {
        if (mementoFilter.test(memento))
            return createTask(searcher, mementoFilter, innerField, memento, index);
        return EMPTY_TASK;
    }

    private Stream<Result> splitAndFixResult(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            ColumnField mergedOuterField = outerField.freeze(searcher.getBit().height);
            mergedOuterField.merge(minoOuterField);
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return searcher.getTaskResultHelper().fixResult(searcher, mementoFilter, mergedOuterField, nextMemento);
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
