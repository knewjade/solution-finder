package searcher.pack.memento;

import searcher.pack.MinoField;
import common.buildup.BuildUp;
import common.datastore.OperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class SRSValidSolutionFilter implements SolutionFilter {
    private final Field field;
    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final int height;

    public SRSValidSolutionFilter(Field field, ThreadLocal<? extends Reachable> reachableThreadLocal, int height) {
        this.field = field;
        this.reachableThreadLocal = reachableThreadLocal;
        this.height = height;
    }

    @Override
    public boolean test(MinoFieldMemento memento) {
        // 手順を連結していない場合は、チェックせずに有効とする
        if (!memento.isConcat())
            return true;

        // 手順のkeyに矛盾がないかを確認
        LinkedList<OperationWithKey> rawOperations = memento.getRawOperationsStream().collect(Collectors.toCollection(LinkedList::new));
        return BuildUp.checksKey(rawOperations, 0L, height);
    }

    @Override
    public boolean testLast(MinoFieldMemento memento) {
        if (!test(memento))
            return false;

        LinkedList<OperationWithKey> operations = memento.getOperationsStream().collect(Collectors.toCollection(LinkedList::new));
        Reachable reachable = reachableThreadLocal.get();
        return BuildUp.existsValidBuildPattern(field, operations, height, reachable);
    }

    @Override
    public boolean testMinoField(MinoField minoField) {
        return true;
    }
}
