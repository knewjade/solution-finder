package entry.path;

import common.buildup.BuildUp;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.datastore.pieces.Blocks;
import common.iterable.CombinationIterable;
import common.pattern.PiecesGenerator;
import core.mino.Block;
import searcher.pack.mino_field.MinoField;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ForPathSolutionFilter implements SolutionFilter {
    private final Set<Long> validBlockCounters;
    private final int height;

    public ForPathSolutionFilter(List<String> patterns,int height) {
        this.height = height;

        PiecesGenerator pieces = new PiecesGenerator(patterns);
        HashSet<BlockCounter> counters = StreamSupport.stream(pieces.spliterator(), true)
                .map(Blocks::getBlocks)
                .map(BlockCounter::new)
                .collect(Collectors.toCollection(HashSet::new));

        validBlockCounters = new HashSet<>();

        for (BlockCounter counter : counters) {
            List<Block> usingBlocks = counter.getBlocks();
            for (int size = 1; size <= usingBlocks.size(); size++) {
                CombinationIterable<Block> combinationIterable = new CombinationIterable<>(usingBlocks, size);
                for (List<Block> blocks : combinationIterable) {
                    BlockCounter newCounter = new BlockCounter(blocks);
                    validBlockCounters.add(newCounter.getCounter());
                }
            }
        }
    }

    @Override
    public boolean test(MinoFieldMemento memento) {
        // 手順を連結していない場合は、チェックせずに有効とする
        if (!memento.isConcat())
            return true;

        // ブロックの使用状況を確認
        if (!checksValidCounter(memento.getSumBlockCounter()))
            return false;

        // 手順のkeyに矛盾がないかを確認
        LinkedList<OperationWithKey> rawOperations = memento.getRawOperationsStream().collect(Collectors.toCollection(LinkedList::new));
        return BuildUp.checksKeyDirectly(rawOperations, 0L, height);
    }

    private boolean checksValidCounter(long counter) {
        return validBlockCounters.contains(counter);
    }

    // 回転入れで入るかどうかの確認は後で行うため、この段階ではしない
    @Override
    public boolean testLast(MinoFieldMemento memento) {
        return test(memento);
    }

    @Override
    public boolean testMinoField(MinoField minoField) {
        return checksValidCounter(minoField.getBlockCounter().getCounter());
    }
}
