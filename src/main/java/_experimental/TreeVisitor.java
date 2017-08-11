package _experimental;

import core.mino.Block;

public interface TreeVisitor {
    void visit(int depth, Block block);

    boolean execute(int depth);
}
