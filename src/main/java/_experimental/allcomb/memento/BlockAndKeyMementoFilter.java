package _experimental.allcomb.memento;

import common.buildup.BuildUp;
import common.datastore.OperationWithKey;

import java.util.LinkedList;
import java.util.Set;

public class BlockAndKeyMementoFilter implements MementoFilter {
    private final Set<Long> validBlockCounters;
    private final int height;

    public BlockAndKeyMementoFilter(Set<Long> validBlockCounters, int height) {
        this.validBlockCounters = validBlockCounters;
        this.height = height;
    }

    @Override
    public boolean test(MinoFieldMemento memento) {
        // 手順を連結していない場合は、チェックせずに有効とする
        if (!memento.isConcat())
            return true;

        // ブロックの使用状況を確認
        long counter = memento.getSumBlockCounter();
        if (!validBlockCounters.contains(counter))
            return false;

        // 手順のkeyに矛盾がないかを確認
        LinkedList<OperationWithKey> rawOperations = memento.getRawOperations();
        return BuildUp.checksKey(rawOperations, 0L, height);
    }
}
