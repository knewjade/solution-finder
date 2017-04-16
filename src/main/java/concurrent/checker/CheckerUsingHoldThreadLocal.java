package concurrent.checker;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.checker.CheckerUsingHold;
import searcher.checker.Checker;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

public class CheckerUsingHoldThreadLocal<T extends Action> extends ThreadLocal<Checker<T>> {
    @Override
    protected Checker<T> initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        PerfectValidator validator = new PerfectValidator();
        return new CheckerUsingHold<>(minoFactory, minoShifter, validator);
    }
}