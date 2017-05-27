package searcher.pack;

import searcher.pack.memento.MementoFilter;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.solutions.BasicSolutions;
import searcher.pack.task.MinoPackingTask;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;
import common.Stopwatch;
import core.column_field.ColumnField;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PackSearcher {
    private final List<InOutPairField> inOutPairFields;
    private final BasicSolutions solutions;
    private final SizedBit sizedBit;
    private final int lastIndex;
    private final MementoFilter mementoFilter;
    private final TaskResultHelper taskResultHelper;

    public PackSearcher(List<InOutPairField> inOutPairFields, BasicSolutions solutions, SizedBit sizedBit, MementoFilter mementoFilter, TaskResultHelper taskResultHelper) {
        this.inOutPairFields = inOutPairFields;
        this.solutions = solutions;
        this.sizedBit = sizedBit;
        this.lastIndex = inOutPairFields.size() - 1;
        this.mementoFilter = mementoFilter;
        this.taskResultHelper = taskResultHelper;
    }

    public List<Result> toList() throws InterruptedException, ExecutionException {
        // 探索準備
        MinoFieldMemento emptyMemento = new MinoFieldMemento();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        MinoPackingTask task = new MinoPackingTask(this, innerField, emptyMemento, 0);

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

    public void forEach(Consumer<Result> callback) throws InterruptedException, ExecutionException {
        // 探索準備
        MinoFieldMemento emptyMemento = new MinoFieldMemento();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        MinoPackingTask task = new MinoPackingTask(this, innerField, emptyMemento, 0);

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

    public void search() throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MinoFieldMemento memento = new MinoFieldMemento();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        MinoPackingTask task = new MinoPackingTask(this, innerField, memento, 0);

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./listup/output", false), StandardCharsets.UTF_8))) {
            forkJoinPool.submit(() -> {
                // Streamは終端操作を実行するまで実際には計算を行わない
                // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要 (Pool内ではStream自体の作成をして終了する)
                long count = task.compute()
                        .count();
//                        .filter(result -> result.getMemento().getRawOperations().stream().allMatch(key -> key.getNeedDeletedKey() == 0L))
//                        .map(result -> {
//                            LinkedList<OperationWithKey> operations = result.getMemento().getOperations();
//                            operations.sort(OPERATION_WITH_KEY_COMPARATOR);
//                            return OperationWithKeyHelper.parseToString(operations);
//                        })
//                        .peek(str -> {
//                            singleThreadExecutor.submit(() -> {
//                                try {
//                                    writer.write(str);
//                                    writer.newLine();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            });
//                        })
                System.out.println(count);
            });

            forkJoinPool.shutdown();
            forkJoinPool.awaitTermination(7L, TimeUnit.DAYS);  // 十分に長い時間待つ

            stopwatch.stop();
            System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

            singleThreadExecutor.shutdown();
            singleThreadExecutor.awaitTermination(1L, TimeUnit.HOURS);  // 十分に長い時間待つ

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SizedBit getSizedBit() {
        return sizedBit;
    }

    public List<InOutPairField> getInOutPairFields() {
        return inOutPairFields;
    }

    public BasicSolutions getSolutions() {
        return solutions;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public TaskResultHelper getTaskResultHelper() {
        return taskResultHelper;
    }

    public MementoFilter getMementoFilter() {
        return mementoFilter;
    }
}
