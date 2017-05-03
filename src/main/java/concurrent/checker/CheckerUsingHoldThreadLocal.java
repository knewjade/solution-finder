package concurrent.checker;

import core.mino.MinoFactory;
import searcher.checker.CheckerUsingHold;
import searcher.checker.Checker;
import common.datastore.action.Action;
import searcher.common.validator.PerfectValidator;

public class CheckerUsingHoldThreadLocal<T extends Action> extends ThreadLocal<Checker<T>> {
    @Override
    protected Checker<T> initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        return new CheckerUsingHold<>(minoFactory, validator);
    }
}