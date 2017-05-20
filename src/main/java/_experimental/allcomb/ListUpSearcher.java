package _experimental.allcomb;

import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;
import _experimental.allcomb.task.MinoPackingTask;
import _experimental.allcomb.task.Result;
import _experimental.allcomb.task.TaskResultHelper;
import common.Stopwatch;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ListUpSearcher {
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

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        ForkJoinTask<Boolean> submit = forkJoinPool.submit(() -> {
            // Streamは終端操作を実行するまで実際には計算を行わない
            // そのため、終端操作をPool内でしなければ、Pool外のスレッド場で動くため注意が必要 (Pool内ではStream自体の作成をして終了する)
            long count = task.compute().count();
            System.out.println(count);
            return true;
        });
        submit.get();

        stopwatch.stop();
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
        forkJoinPool.shutdown();
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
