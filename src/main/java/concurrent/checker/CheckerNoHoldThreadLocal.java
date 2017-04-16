package concurrent.checker;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.checker.Checker;
import searcher.checker.CheckerNoHold;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

public class CheckerNoHoldThreadLocal<T extends Action> extends ThreadLocal<Checker<T>> {
    @Override
    protected Checker<T> initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        return new CheckerNoHold<>(minoFactory, validator);
    }
}