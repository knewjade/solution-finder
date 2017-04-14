package tree;

import core.mino.Block;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentVisitedTree {
    public static final short NO_RESULT = -1;
    public static final short SUCCEED = 0;
    public static final short FAILED = 1;

    private static class Element {
        private final EnumMap<Block, Element> current = new EnumMap<>(Block.class);
        private int isSucceed = NO_RESULT;

        private void success(List<Block> blocks, int depth) {
            if (depth < blocks.size()) {
                Block currentBlock = blocks.get(depth);
                if (currentBlock != null) {
                    Element element = current.computeIfAbsent(currentBlock, k -> new Element());
                    element.success(blocks, depth + 1);
                } else {
                    for (Block block : Block.values()) {
                        Element element = current.computeIfAbsent(block, k -> new Element());
                        element.success(blocks, depth + 1);
                    }
                }
            } else {
                assert isSucceed != FAILED;
                isSucceed = SUCCEED;
            }
        }

        private void fail(List<Block> blocks, int depth) {
            if (depth < blocks.size()) {
                Block currentBlock = blocks.get(depth);
                if (currentBlock != null) {
                    Element element = current.computeIfAbsent(currentBlock, k -> new Element());
                    element.fail(blocks, depth + 1);
                } else {
                    for (Block block : Block.values()) {
                        Element element = current.computeIfAbsent(block, k -> new Element());
                        element.fail(blocks, depth + 1);
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
        synchronized (rootElement) {
            rootElement.success(blocks, 0);
        }
    }

    public void fail(List<Block> blocks) {
        synchronized (rootElement) {
            rootElement.fail(blocks, 0);
        }
    }

    public void set(boolean result, List<Block> blocks) {
        if (result)
            success(blocks);
        else
            fail(blocks);
    }

    public int isSucceed(List<Block> blocks) {
        synchronized (rootElement) {
            return rootElement.isSucceed(blocks, 0);
        }
    }
}
