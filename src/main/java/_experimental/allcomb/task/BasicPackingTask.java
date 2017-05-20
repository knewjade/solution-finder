package _experimental.allcomb.task;

import _experimental.allcomb.*;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Stream;

public class BasicPackingTask extends MinoPackingTask {
    private static final MinoPackingTask EMPTY_TASK = new EmptyMinoPackingTask();

    private final ListUpSearcher searcher;
    private final ColumnField innerField;
    private final MinoFieldMemento memento;
    private final MementoFilter mementoFilter;
    private final int index;

    public BasicPackingTask(ListUpSearcher searcher, ColumnField innerField, MinoFieldMemento memento, MementoFilter mementoFilter, int index) {
        this.searcher = searcher;
        this.innerField = innerField;
        this.memento = memento;
        this.mementoFilter = mementoFilter;
        this.index = index;
    }

    @Override
    protected Stream<Result> compute() {
        if (innerField.getBoard(0) == searcher.getBit().fillBoard) {
            List<InOutPairField> inOutPairFields = searcher.getInOutPairFields();
            // innerFieldが埋まっている
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                ColumnField lastOuterField = inOutPairFields.get(index).getOuterField();
                return last(lastOuterField, memento.skip());
            } else {
                // 途中の計算  // 自分で計算する
                int nextIndex = index + 1;
                ColumnField nextInnerField = inOutPairFields.get(nextIndex).getInnerField();
                MinoFieldMemento nextMemento = memento.skip();
                return new BasicPackingTask(searcher, nextInnerField, nextMemento, mementoFilter, nextIndex).compute();
            }
        } else {
            Set<MinoField> minoFields = searcher.getSolutions().get(innerField);
            assert minoFields != null : innerField.getBoard(0);

            // innerFieldが埋まっていない
            if (index == searcher.getLastIndex()) {
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
            return new BasicPackingTask(searcher, nextInnerField, nextMemento, mementoFilter, index + 1);
        }

        return EMPTY_TASK;
    }

    private boolean isValidTask(MinoPackingTask task) {
        return task != EMPTY_TASK && mementoFilter.test(task.getMemento());
    }

    private Stream<Result> splitLast(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            ColumnField mergedOuterField = outerField.freeze(searcher.getBit().height);
            mergedOuterField.merge(minoOuterField);
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return last(mergedOuterField, nextMemento);
        }

        return Stream.empty();
    }

    private Stream<Result> last(ColumnField lastOuterField, MinoFieldMemento nextMemento) {
        Bit bit = searcher.getBit();
        long board = lastOuterField.getBoard(0) >> bit.maxBit;
        if (board == bit.fillBoard) {
            if (mementoFilter.test(nextMemento))
                return Stream.of(new Result(nextMemento));
            return Stream.empty();
        } else {
            ColumnSmallField nextInnerField = new ColumnSmallField(board);
            Set<MinoField> minoFields = searcher.getSolutions().get(nextInnerField);

            return minoFields.stream()
                    .map(nextMemento::concat)
                    .filter(mementoFilter::test)
                    .map(Result::new);
        }
    }

    @Override
    public MinoFieldMemento getMemento() {
        return memento;
    }
}
