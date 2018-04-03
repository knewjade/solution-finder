package searcher.pack.task;

import core.column_field.ColumnField;
import core.field.Field;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.MinoFieldMementoFactory;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.task.setup.MinoSetupTaskWidthForWidth2;
import searcher.pack.task.setup.MinoSetupTaskWidthForWidth3;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetupPackSearcher implements PackSearcher {
    private final List<InOutPairField> inOutPairFields;
    private final List<BasicSolutions> solutions;
    private final SizedBit sizedBit;
    private final int lastIndex;
    private final SolutionFilter solutionFilter;
    private final TaskResultHelper taskResultHelper;
    private final List<ColumnField> needFillFields;
    private final Field needFilledField;
    private final SeparableMinos separableMinos;

    public SetupPackSearcher(List<InOutPairField> inOutPairFields, List<BasicSolutions> solutions, SizedBit sizedBit, SolutionFilter solutionFilter, TaskResultHelper taskResultHelper, List<ColumnField> needFillFields, SeparableMinos separableMinos, Field needFilledField) {
        this.needFilledField = needFilledField;
        assert inOutPairFields.size() + 1 == solutions.size();
        assert inOutPairFields.size() + 1 == needFillFields.size();
        this.inOutPairFields = inOutPairFields;
        this.solutions = solutions;
        this.sizedBit = sizedBit;
        this.lastIndex = inOutPairFields.size() - 1;
        this.solutionFilter = solutionFilter;
        this.taskResultHelper = taskResultHelper;
        this.separableMinos = separableMinos;
        this.needFillFields = needFillFields;
    }

    public List<Result> toList() throws InterruptedException, ExecutionException {
        // 探索準備
        MinoFieldMemento emptyMemento = MinoFieldMementoFactory.create();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        PackingTask task = createPackingTask(sizedBit, emptyMemento, innerField);

        // 探索
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        ForkJoinTask<List<Result>> submitTask = forkJoinPool.submit(() -> {
            // Streamは終端操作を実行するまで実際には計算を行わない
            // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要
            // (終端操作をしなかった場合、Pool内ではStream自体の作成をして終了する)
            return task.compute().parallel().collect(Collectors.toList());
        });

        // 結果を取得するまで待つ
        List<Result> results = submitTask.get();

        // 終了処理
        forkJoinPool.shutdown();

        return results;
    }

    private PackingTask createPackingTask(SizedBit sizedBit, MinoFieldMemento emptyMemento, ColumnField innerField) {
        switch (sizedBit.getWidth()) {
            case 2:
                return new MinoSetupTaskWidthForWidth2(this, innerField, emptyMemento, 0, separableMinos, needFilledField);
            case 3:
                return new MinoSetupTaskWidthForWidth3(this, innerField, emptyMemento, 0, separableMinos, needFilledField);
        }
        throw new UnsupportedOperationException("No support: should be width 2 or 3");
    }

    public void forEach(Consumer<Result> callback) throws InterruptedException, ExecutionException {
        // 探索準備
        MinoFieldMemento emptyMemento = MinoFieldMementoFactory.create();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        PackingTask task = createPackingTask(sizedBit, emptyMemento, innerField);

        // 探索
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        ForkJoinTask<Boolean> submitTask = forkJoinPool.submit(() -> {
            // Streamは終端操作を実行するまで実際には計算を行わない
            // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要
            // (終端操作をしなかった場合、Pool内ではStream自体の作成をして終了する)
            task.compute().parallel().forEach(callback);

            // 計算を最後まで行ったことを伝えるため true を返却
            return true;
        });

        // 結果を取得するまで待つ
        Boolean result = submitTask.get();
        assert result;

        // 終了処理
        forkJoinPool.shutdown();
    }

    public <A, R> R collect(Collector<? super Result, A, R> callback) throws InterruptedException, ExecutionException {
        // 探索準備
        MinoFieldMemento emptyMemento = MinoFieldMementoFactory.create();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        PackingTask task = createPackingTask(sizedBit, emptyMemento, innerField);

        // 探索
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        ForkJoinTask<R> submitTask = forkJoinPool.submit(() -> {
            // Streamは終端操作を実行するまで実際には計算を行わない
            // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要
            // (終端操作をしなかった場合、Pool内ではStream自体の作成をして終了する)

            return task.compute().parallel().collect(callback);
        });

        // 結果を取得するまで待つ
        R result = submitTask.get();
        assert result != null;

        // 終了処理
        forkJoinPool.shutdown();

        return result;
    }

    public Optional<Result> findAny() throws InterruptedException, ExecutionException {
        // 探索準備
        MinoFieldMemento emptyMemento = MinoFieldMementoFactory.create();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        PackingTask task = createPackingTask(sizedBit, emptyMemento, innerField);

        // 探索
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        ForkJoinTask<Optional<Result>> submitTask = forkJoinPool.submit(() -> {
            // Streamは終端操作を実行するまで実際には計算を行わない
            // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要
            // (終端操作をしなかった場合、Pool内ではStream自体の作成をして終了する)

            return task.compute().parallel().findAny();
        });

        // 結果を取得するまで待つ
        Optional<Result> result = submitTask.get();
        assert result != null;

        // 終了処理
        forkJoinPool.shutdown();

        return result;
    }

    public <T> T stream(Function<Stream<Result>, T> callback) throws InterruptedException, ExecutionException {
        // 探索準備
        MinoFieldMemento emptyMemento = MinoFieldMementoFactory.create();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        PackingTask task = createPackingTask(sizedBit, emptyMemento, innerField);

        // 探索
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        ForkJoinTask<T> submitTask = forkJoinPool.submit(() -> {
            // Streamは終端操作を実行するまで実際には計算を行わない
            // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要
            // (終端操作をしなかった場合、Pool内ではStream自体の作成をして終了する)

            return callback.apply(task.compute().parallel());
        });

        // 結果を取得するまで待つ
        T result = submitTask.get();
        assert result != null;

        // 終了処理
        forkJoinPool.shutdown();

        return result;
    }

    public Long count() throws InterruptedException, ExecutionException {
        // 探索準備
        MinoFieldMemento emptyMemento = MinoFieldMementoFactory.create();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        PackingTask task = createPackingTask(sizedBit, emptyMemento, innerField);

        // 探索
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        ForkJoinTask<Long> submitTask = forkJoinPool.submit(() -> {
            // Streamは終端操作を実行するまで実際には計算を行わない
            // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要
            // (終端操作をしなかった場合、Pool内ではStream自体の作成をして終了する)

            return task.compute().parallel()
                    .count();
        });

        // 結果を取得するまで待つ
        Long result = submitTask.get();
        assert result != null;

        // 終了処理
        forkJoinPool.shutdown();

        return result;
    }

    @Override
    public SizedBit getSizedBit() {
        return sizedBit;
    }

    @Override
    public List<InOutPairField> getInOutPairFields() {
        return inOutPairFields;
    }

    @Override
    public BasicSolutions getSolutions(int index) {
        return solutions.get(index);
    }

    @Override
    public int getLastIndex() {
        return lastIndex;
    }

    @Override
    public TaskResultHelper getTaskResultHelper() {
        return taskResultHelper;
    }

    @Override
    public SolutionFilter getSolutionFilter() {
        return solutionFilter;
    }

    @Override
    public boolean isFilled(ColumnField columnField, int index) {
        ColumnField innerField = needFillFields.get(index);
        long needFillBoard = innerField.getBoard(0);
        return (columnField.getBoard(0) & needFillBoard) == needFillBoard;
    }

    @Override
    public boolean contains(ColumnField columnField, int index) {
        ColumnField innerField = needFillFields.get(index);
        long needFillBoard = innerField.getBoard(0);
        return (columnField.getBoard(0) & needFillBoard) != 0L;
    }
}
