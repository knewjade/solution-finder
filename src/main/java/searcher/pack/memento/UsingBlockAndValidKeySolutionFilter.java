package searcher.pack.memento;

import searcher.pack.IMinoField;
import common.buildup.BuildUp;
import common.datastore.OperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;

import java.util.LinkedList;
import java.util.Set;

public class UsingBlockAndValidKeySolutionFilter implements SolutionFilter {
    private final Field field;
    private final Set<Long> validBlockCounters;
    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final int height;

    public UsingBlockAndValidKeySolutionFilter(Field field, Set<Long> validBlockCounters, ThreadLocal<? extends Reachable> reachableThreadLocal, int height) {
        this.field = field;
        this.validBlockCounters = validBlockCounters;
        this.reachableThreadLocal = reachableThreadLocal;
        this.height = height;
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
        LinkedList<OperationWithKey> rawOperations = memento.getRawOperations();
        return BuildUp.checksKeyDirectly(rawOperations, 0L, height);
    }

    private boolean checksValidCounter(long counter) {
        return validBlockCounters.contains(counter);
    }

    @Override
    public boolean testLast(MinoFieldMemento memento) {
        if (!test(memento))
            return false;

        LinkedList<OperationWithKey> operations = memento.getOperations();
        Reachable reachable = reachableThreadLocal.get();
        return BuildUp.existsValidBuildPatternDirectly(field, operations, height, reachable);
    }

    @Override
    public boolean testMinoField(IMinoField minoField) {
        return checksValidCounter(minoField.getBlockCounter().getCounter());
    }
}
