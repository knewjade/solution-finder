package _experimental;

import core.mino.Block;

import java.util.List;
import java.util.function.Function;

public interface IPatternTree {
    void build(List<Block> blocks, int depth, Function<List<Block>, IPatternTree> terminate);

    boolean get(List<Block> blocks, int depth);

    boolean run(TreeVisitor visitor, int depth);

    void success();

    boolean isPossible();
}
