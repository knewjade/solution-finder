package concurrent.checker;

import core.mino.MinoFactory;
import searcher.checker.Checker;
import searcher.checker.CheckerNoHold;
import common.datastore.action.Action;
import searcher.common.validator.PerfectValidator;

public class CheckerNoHoldThreadLocal<T extends Action> extends ThreadLocal<Checker> {
    @Override
    protected Checker initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        return new CheckerNoHold(minoFactory, validator);
    }
}