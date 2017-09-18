package entry.path;

import common.buildup.BuildUp;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.pattern.BlocksGenerator;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_field.MinoField;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ForPathSolutionFilter implements SolutionFilter {
    private final List<BlockCounter> validBlockCounters;
    private final int height;

    public ForPathSolutionFilter(BlocksGenerator generator, int height) {
        this.height = height;
        this.validBlockCounters = generator.blockCountersStream()
                .distinct()
                .collect(Collectors.toList());
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

    private boolean checksValidCounter(BlockCounter counter) {
        return validBlockCounters.stream()
                .anyMatch(blockCounter -> blockCounter.containsAll(counter));
    }

    // 回転入れで入るかどうかの確認は後で行うため、この段階ではしない
    @Override
    public boolean testLast(MinoFieldMemento memento) {
        return test(memento);
    }

    @Override
    public boolean testMinoField(MinoField minoField) {
        return checksValidCounter(minoField.getBlockCounter());
    }
}