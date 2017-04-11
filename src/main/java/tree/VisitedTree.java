package tree;

import core.mino.Block;

import java.util.EnumMap;
import java.util.List;

public class VisitedTree {
    public static final short NO_RESULT = -1;
    public static final short SUCCEED = 0;
    public static final short FAILED = 1;

    private static class Element {
        private final EnumMap<Block, Element> current = new EnumMap<>(Block.class);
        private int isSucceed = NO_RESULT;

        private void success(List<Block> blocks) {
            if (1 <= blocks.size()) {
                Block currentBlock = blocks.get(0);
                if (currentBlock != null) {
                    Element element = current.computeIfAbsent(currentBlock, k -> new Element());
                    element.success(blocks.subList(1, blocks.size()));
                } else {
                    for (Block block : Block.values()) {
                        Element element = current.computeIfAbsent(block, k -> new Element());
                        element.success(blocks.subList(1, blocks.size()));
                    }
                }
            } else {
                assert isSucceed != FAILED;
                isSucceed = SUCCEED;
            }
        }

        private void fail(List<Block> blocks) {
            if (1 <= blocks.size()) {
                Block currentBlock = blocks.get(0);
                if (currentBlock != null) {
                    Element element = current.computeIfAbsent(currentBlock, k -> new Element());
                    element.fail(blocks.subList(1, blocks.size()));
                } else {
                    for (Block block : Block.values()) {
                        Element element = current.computeIfAbsent(block, k -> new Element());
                        element.fail(blocks.subList(1, blocks.size()));
                    }
                }
            } else {
                assert isSucceed != SUCCEED;
                isSucceed = FAILED;
            }
        }

        private boolean isVisited(List<Block> blocks, int depth) {
            Block block = blocks.get(depth);
            if (depth + 1 < blocks.size()) {
                return current.containsKey(block) && current.get(block).isVisited(blocks, depth + 1);
            } else {
                return current.containsKey(block);
            }
        }

        private int isSucceed(List<Block> blocks, int depth) {
            if (blocks.size() <= depth)
                return isSucceed;

            Block block = blocks.get(depth);
            if (!current.containsKey(block))
                return NO_RESULT;

            return current.get(block).isSucceed(blocks, depth + 1);
        }
    }

    private final Element rootElement = new Element();

    public void success(List<Block> blocks) {
        rootElement.success(blocks);
    }

    public void fail(List<Block> blocks) {
        rootElement.fail(blocks);
    }

    public void set(boolean result, List<Block> blocks) {
        if (result)
            success(blocks);
        else
            fail(blocks);
    }

    public boolean isVisited(List<Block> blocks) {
        return rootElement.isVisited(blocks, 0);
    }

    public int isSucceed(List<Block> blocks) {
        return rootElement.isSucceed(blocks, 0);
    }
}
