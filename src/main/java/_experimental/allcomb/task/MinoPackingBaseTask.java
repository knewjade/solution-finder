package _experimental.allcomb.task;

import _experimental.allcomb.*;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Stream;

public abstract class MinoPackingBaseTask extends MinoPackingTask {
    private static final MinoPackingTask EMPTY_TASK = new EmptyMinoPackingTask();
    private static final int SPLIT_THRESHOLD_SIZE = 16;

    protected final ListUpSearcher searcher;
    protected final MementoFilter mementoFilter;
    private final ColumnField innerField;
    private final MinoFieldMemento memento;
    private final int index;

    MinoPackingBaseTask(ListUpSearcher searcher, MementoFilter mementoFilter, ColumnField innerField, MinoFieldMemento memento, int index) {
        this.searcher = searcher;
        this.mementoFilter = mementoFilter;
        this.innerField = innerField;
        this.memento = memento;
        this.index = index;
    }

    @Override
    protected Stream<Result> compute() {
        if (innerField.getBoard(0) == searcher.getBit().fillBoard) {
            // innerFieldが埋まっている
            List<InOutPairField> inOutPairFields = searcher.getInOutPairFields();
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                ColumnField lastOuterField = inOutPairFields.get(index).getOuterField();
                MinoFieldMemento nextMemento = memento.skip();
                return fixResult(lastOuterField, nextMemento);
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
                return minoFields.stream()
                        .flatMap(this::splitAndFixResult);
            } else if (SPLIT_THRESHOLD_SIZE <= minoFields.size()) {
                // 途中の計算 (分割)
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

    protected abstract MinoPackingBaseTask createTask(ListUpSearcher searcher, MementoFilter mementoFilter, ColumnField innerField, MinoFieldMemento memento, int index);

    protected abstract Stream<Result> fixResult(ColumnField lastOuterField, MinoFieldMemento nextMemento);

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
            return fixResult(mergedOuterField, nextMemento);
        }

        return Stream.empty();
    }

    private boolean isValidTask(MinoPackingTask task) {
        return task != EMPTY_TASK;
    }

    protected Result createResult(MinoFieldMemento memento) {
        return new Result(memento);
    }

    @Override
    public MinoFieldMemento getMemento() {
        return memento;
    }
}
