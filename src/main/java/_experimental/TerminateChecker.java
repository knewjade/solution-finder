package _experimental;

import core.mino.Block;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class TerminateChecker implements IPatternTree {
    private final AtomicBoolean isPossible = new AtomicBoolean(false);

    @Override
    public void build(List<Block> blocks, int depth, Function<List<Block>, IPatternTree> terminate) {
    }

    @Override
    public boolean get(List<Block> blocks, int depth) {
        return isPossible();
    }

    @Override
    public boolean run(TreeVisitor visitor, int depth) {
        boolean result = visitor.execute(depth);

        if (result)
            this.success();

        return result;
    }

    @Override
    public void success() {
        isPossible.set(true);
    }

    @Override
    public boolean isPossible() {
        return isPossible.get();
    }
}
