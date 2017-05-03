package concurrent.checkmate;

import core.mino.MinoFactory;
import searcher.checkmate.Checkmate;
import searcher.checkmate.CheckmateUsingHoldReuse;
import common.datastore.action.Action;
import searcher.common.validator.PerfectValidator;

public class CheckmateUsingHoldThreadLocal<T extends Action> extends ThreadLocal<Checkmate<T>> {
    @Override
    protected Checkmate<T> initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        return new CheckmateUsingHoldReuse<T>(minoFactory, validator);
    }
}