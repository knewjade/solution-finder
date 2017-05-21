package _experimental.allcomb.memento;

import common.buildup.BuildUp;
import common.datastore.IOperationWithKey;
import core.action.reachable.Reachable;
import core.field.Field;

import java.util.LinkedList;

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
        // TODO: 基本パターンを事前に判定し、このチェックを連結後に移動する
        // ライン削除がないことを確認
        boolean noDeleted = memento.getRawOperations().stream().allMatch(key -> key.getNeedDeletedKey() == 0L);
        if (!noDeleted)
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
