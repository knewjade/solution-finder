package concurrent;

import core.mino.MinoFactory;
import searcher.checker.Checker;
import searcher.common.action.Action;
import searcher.common.validator.PerfectValidator;

public class CheckerThreadLocal<T extends Action> extends ThreadLocal<Checker<T>> {
    @Override
    protected Checker<T> initialValue() {
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        return new Checker<>(minoFactory, validator);
    }
}