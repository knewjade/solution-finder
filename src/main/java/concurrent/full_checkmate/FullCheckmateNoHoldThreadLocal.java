package concurrent.full_checkmate;

import core.mino.MinoFactory;
import common.datastore.action.Action;
import searcher.full_checkmate.FullCheckmate;
import searcher.full_checkmate.FullCheckmateNoHold;

public class FullCheckmateNoHoldThreadLocal<T extends Action> extends ThreadLocal<FullCheckmate<T>> {
    @Override
    protected FullCheckmate<T> initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        return new FullCheckmateNoHold<>(minoFactory);
    }
}