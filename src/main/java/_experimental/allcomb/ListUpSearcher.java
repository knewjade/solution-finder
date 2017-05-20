package _experimental.allcomb;

import _experimental.allcomb.memento.AllPassedMementoFilter;
import _experimental.allcomb.memento.MementoFilter;
import _experimental.allcomb.memento.MinoFieldMemento;
import _experimental.allcomb.task.Field4x10MinoPackingTask;
import common.Stopwatch;
import common.iterable.CombinationIterable;
import core.mino.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class ListUpSearcher {
    private final List<InOutPairField> inOutPairFields;
    private final BasicSolutions solutions;
    private final Bit bit;
    private final int lastIndex;

    public ListUpSearcher(List<InOutPairField> inOutPairFields, BasicSolutions solutions, Bit bit) {
        this.inOutPairFields = inOutPairFields;
        this.solutions = solutions;
        this.bit = bit;
        this.lastIndex = inOutPairFields.size() - 1;
    }

    public void search() {
        // TODO: ミノの制限をちゃんとする
        HashSet<Long> validBlockCounters = new HashSet<>();
        List<Block> usingBlocks = Arrays.asList(Block.values());
        for (int size = 0; size < usingBlocks.size(); size++) {
            CombinationIterable<Block> combinationIterable = new CombinationIterable<>(usingBlocks, size);
            for (List<Block> blocks : combinationIterable) {
                BlockCounter counter = new BlockCounter(blocks);
                validBlockCounters.add(counter.getCounter());
            }
        }
//        MementoFilter mementoFilter = new BlockAndKeyMementoFilter(validBlockCounters, bit.height);
        MementoFilter mementoFilter = new AllPassedMementoFilter();

        ForkJoinPool pool = new ForkJoinPool();
        MinoFieldMemento memento = new MinoFieldMemento();
//        BasicPackingTask myTask = new BasicPackingTask(this, inOutPairFields.get(0).getInnerField(), memento, taskFilter, 0);
        Field4x10MinoPackingTask task = new Field4x10MinoPackingTask(this, mementoFilter, inOutPairFields.get(0).getInnerField(), memento, 0);

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();
        long count = pool.invoke(task).count();
        stopwatch.stop();
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));
        System.out.println(count);
        pool.shutdown();
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
}
