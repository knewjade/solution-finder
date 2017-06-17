package searcher.pack.task;

import core.column_field.ColumnField;
import searcher.pack.InOutPairField;
import searcher.pack.SizedBit;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.MinoFieldMementoFactory;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.solutions.BasicSolutions;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackSearcher {
    private final List<InOutPairField> inOutPairFields;
    private final BasicSolutions solutions;
    private final SizedBit sizedBit;
    private final int lastIndex;
    private final SolutionFilter solutionFilter;
    private final TaskResultHelper taskResultHelper;

    public PackSearcher(List<InOutPairField> inOutPairFields, BasicSolutions solutions, SizedBit sizedBit, SolutionFilter solutionFilter, TaskResultHelper taskResultHelper) {
        this.inOutPairFields = inOutPairFields;
        this.solutions = solutions;
        this.sizedBit = sizedBit;
        this.lastIndex = inOutPairFields.size() - 1;
        this.solutionFilter = solutionFilter;
        this.taskResultHelper = taskResultHelper;
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
                return new MinoPackingTaskWidth2(this, innerField, emptyMemento, 0);
            case 3:
                return new MinoPackingTaskWidth3(this, innerField, emptyMemento, 0);
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

    public <R> R callback(Function<Stream<Result>, R> callback) throws InterruptedException, ExecutionException {
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

            return callback.apply(task.compute().parallel());
        });

        // 結果を取得するまで待つ
        R result = submitTask.get();
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

            return task.compute().parallel().count();
        });

        // 結果を取得するまで待つ
        Long result = submitTask.get();
        assert result != null;

        // 終了処理
        forkJoinPool.shutdown();

        return result;
    }

    SizedBit getSizedBit() {
        return sizedBit;
    }

    List<InOutPairField> getInOutPairFields() {
        return inOutPairFields;
    }

    BasicSolutions getSolutions() {
        return solutions;
    }

    int getLastIndex() {
        return lastIndex;
    }

    TaskResultHelper getTaskResultHelper() {
        return taskResultHelper;
    }

    SolutionFilter getSolutionFilter() {
        return solutionFilter;
    }
}
