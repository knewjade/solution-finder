package _experimental.allcomb.memento;

import common.buildup.BuildUp;
import common.datastore.IOperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;

import java.util.LinkedList;
import java.util.Set;

public class UsingBlockAndValidKeyMementoFilter implements MementoFilter {
    private final Field field;
    private final Set<Long> validBlockCounters;
    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final int height;

    public UsingBlockAndValidKeyMementoFilter(Field field, Set<Long> validBlockCounters, ThreadLocal<? extends Reachable> reachableThreadLocal, int height) {
        this.field = field;
        this.validBlockCounters = validBlockCounters;
        this.reachableThreadLocal = reachableThreadLocal;
        this.height = height;
    }

    @Override
    public boolean test(MinoFieldMemento memento) {
        // TODO: 基本パターンを事前に判定し、このチェックを連結後に移動する
        // ブロックの使用状況を確認
        long counter = memento.getSumBlockCounter();
        if (!validBlockCounters.contains(counter))
            return false;

        // 手順を連結していない場合は、チェックせずに有効とする
        if (!memento.isConcat())
            return true;

        // 手順のkeyに矛盾がないかを確認
        LinkedList<IOperationWithKey> rawOperations = memento.getRawOperations();
        return BuildUp.checksKeyDirectly(rawOperations, 0L, height);
    }

    @Override
    public boolean testLast(MinoFieldMemento memento) {
        if (!test(memento))
            return false;

        LinkedList<IOperationWithKey> operations = memento.getOperations();
        Reachable reachable = reachableThreadLocal.get();
        return BuildUp.existsValidBuildPatternDirectly(field, operations, height, reachable);
    }
}
