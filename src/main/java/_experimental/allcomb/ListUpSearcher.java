package _experimental.allcomb;

import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;
import _experimental.allcomb.task.MinoPackingTask;
import _experimental.allcomb.task.TaskResultHelper;
import common.OperationWithKeyHelper;
import common.Stopwatch;
import common.datastore.IOperationWithKey;
import core.mino.Mino;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class ListUpSearcher {
    private static final Comparator<IOperationWithKey> OPERATION_WITH_KEY_COMPARATOR = (o1, o2) -> {
        Mino mino1 = o1.getMino();
        Mino mino2 = o2.getMino();

        int compareBlock = mino1.getBlock().compareTo(mino2.getBlock());
        if (compareBlock != 0)
            return compareBlock;

        int compareRotate = mino1.getRotate().compareTo(mino2.getRotate());
        if (compareRotate != 0)
            return compareRotate;

        int compareX = Integer.compare(o1.getX(), o2.getX());
        if (compareX != 0)
            return compareX;

        int compareY = Integer.compare(o1.getY(), o2.getY());
        if (compareY != 0)
            return compareY;

        return Long.compare(o1.getNeedDeletedKey(), o2.getNeedDeletedKey());
    };

    private final List<InOutPairField> inOutPairFields;
    private final BasicSolutions solutions;
    private final Bit bit;
    private final int lastIndex;
    private final MementoFilter mementoFilter;
    private final TaskResultHelper resultHelper;

    public ListUpSearcher(List<InOutPairField> inOutPairFields, BasicSolutions solutions, Bit bit, MementoFilter mementoFilter, TaskResultHelper resultHelper) {
        this.inOutPairFields = inOutPairFields;
        this.solutions = solutions;
        this.bit = bit;
        this.lastIndex = inOutPairFields.size() - 1;
        this.mementoFilter = mementoFilter;
        this.resultHelper = resultHelper;
    }

    public void search() throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MinoFieldMemento memento = new MinoFieldMemento();
        ColumnField innerField = inOutPairFields.get(0).getInnerField();
        MinoPackingTask task = new MinoPackingTask(this, mementoFilter, innerField, memento, 0);

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./listup", false), StandardCharsets.UTF_8))) {
            forkJoinPool.submit(() -> {
                // Streamは終端操作を実行するまで実際には計算を行わない
                // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要 (Pool内ではStream自体の作成をして終了する)
                long count = task.compute()
//                        .filter(result -> result.getMemento().getRawOperations().stream().allMatch(key -> key.getNeedDeletedKey() == 0L))
                        .map(result -> {
                            LinkedList<IOperationWithKey> operations = result.getMemento().getOperations();
                            operations.sort(OPERATION_WITH_KEY_COMPARATOR);
                            return OperationWithKeyHelper.parseToString(operations);
                        })
                        .sorted().sequential()
                        .peek(System.out::println)
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
                        .count();
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

    public Bit getBit() {
        return bit;
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
        return resultHelper;
    }
}
