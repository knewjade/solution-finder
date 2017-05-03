package concurrent.checkmate;

import core.mino.MinoFactory;
import searcher.checkmate.Checkmate;
import searcher.checkmate.CheckmateNoHoldReuse;
import common.datastore.action.Action;
import searcher.common.validator.PerfectValidator;

public class CheckmateNoHoldThreadLocal<T extends Action> extends ThreadLocal<Checkmate<T>> {
    @Override
    protected Checkmate<T> initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        return new CheckmateNoHoldReuse<>(minoFactory, validator);
    }
}