package searcher.pack.memento;

import common.buildup.BuildUp;
import common.datastore.PieceCounter;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;
import searcher.pack.mino_field.MinoField;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public class UsingBlockAndValidKeySolutionFilter implements SolutionFilter {
    private final Field field;
    private final Set<Long> validBlockCounters;
    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final SizedBit sizedBit;

    public UsingBlockAndValidKeySolutionFilter(Field field, Set<Long> validBlockCounters, ThreadLocal<? extends Reachable> reachableThreadLocal, SizedBit sizedBit) {
        this.field = field;
        this.validBlockCounters = validBlockCounters;
        this.reachableThreadLocal = reachableThreadLocal;
        this.sizedBit = sizedBit;
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
        return BuildUp.checksKeyDirectly(rawOperations, 0L, sizedBit.getHeight());
    }

    private boolean checksValidCounter(PieceCounter counter) {
        return validBlockCounters.contains(counter.getCounter());
    }

    @Override
    public boolean testLast(MinoFieldMemento memento) {
        if (!test(memento))
            return false;

        LinkedList<MinoOperationWithKey> operations = memento
                .getSeparableMinoStream(sizedBit.getWidth())
                .map(SeparableMino::toMinoOperationWithKey)
                .collect(Collectors.toCollection(LinkedList::new));

        Reachable reachable = reachableThreadLocal.get();
        return BuildUp.existsValidBuildPatternDirectly(field, operations, sizedBit.getHeight(), reachable);
    }

    @Override
    public boolean testMinoField(MinoField minoField) {
        return checksValidCounter(minoField.getPieceCounter());
    }
}
