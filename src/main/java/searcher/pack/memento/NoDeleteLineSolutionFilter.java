package searcher.pack.memento;

import searcher.pack.MinoField;
import common.buildup.BuildUp;
import common.datastore.OperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoDeleteLineSolutionFilter implements SolutionFilter {
    private final Field field;
    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final int height;

    public NoDeleteLineSolutionFilter(Field field, ThreadLocal<? extends Reachable> reachableThreadLocal, int height) {
        this.field = field;
        this.reachableThreadLocal = reachableThreadLocal;
        this.height = height;
    }

    @Override
    public boolean test(MinoFieldMemento memento) {
        // 手順を連結していない場合は、チェックせずに有効とする
        if (!memento.isConcat())
            return true;

        // ライン削除がないことを確認
        if (!containsDeleteLineKey(memento.getRawOperationsStream()))
            return false;

        // 手順のkeyに矛盾がないかを確認
        LinkedList<OperationWithKey> rawOperations = memento.getRawOperationsStream().collect(Collectors.toCollection(LinkedList::new));
        return BuildUp.checksKeyDirectly(rawOperations, 0L, height);
    }

    @Override
    public boolean testLast(MinoFieldMemento memento) {
        if (!test(memento))
            return false;

        LinkedList<OperationWithKey> operations = memento.getOperationsStream().collect(Collectors.toCollection(LinkedList::new));
        Reachable reachable = reachableThreadLocal.get();
        return BuildUp.existsValidBuildPatternDirectly(field, operations, height, reachable);
    }

    @Override
    public boolean testMinoField(MinoField minoField) {
        return containsDeleteLineKey(minoField.getOperationsStream());
    }
    
    private boolean containsDeleteLineKey(Stream<OperationWithKey> operationsStream) {
        return operationsStream.anyMatch(key -> key.getNeedDeletedKey() != 0L);
    }
}
