package _experimental.allcomb.memento;

import _experimental.allcomb.MinoField;
import common.buildup.BuildUp;
import common.datastore.OperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;

import java.util.LinkedList;
import java.util.List;

public class NoDeleteLineMementoFilter implements MementoFilter {
    private final Field field;
    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final int height;

    public NoDeleteLineMementoFilter(Field field, ThreadLocal<? extends Reachable> reachableThreadLocal, int height) {
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
        if (!containsDeleteLineKey(memento.getRawOperations()))
            return false;

        // 手順のkeyに矛盾がないかを確認
        LinkedList<OperationWithKey> rawOperations = memento.getRawOperations();
        return BuildUp.checksKeyDirectly(rawOperations, 0L, height);
    }

    private boolean containsDeleteLineKey(List<OperationWithKey> operations) {
        return operations.stream().anyMatch(key -> key.getNeedDeletedKey() != 0L);
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
    public boolean testMinoField(MinoField minoField) {
        return !containsDeleteLineKey(minoField.getOperations());
    }
}
