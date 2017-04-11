package tree;

import core.mino.Block;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CheckerTree {
    private static class Element {
        private final EnumMap<Block, Element> current = new EnumMap<>(Block.class);
        private int allCounter = 0;
        private int successCounter = 0;

        private void success(List<Block> blocks) {
            allCounter += 1;
            successCounter += 1;
            if (1 <= blocks.size()) {
                Element element = current.computeIfAbsent(blocks.get(0), k -> new Element());
                element.success(blocks.subList(1, blocks.size()));
            }
        }

        private void fail(List<Block> blocks) {
            allCounter += 1;
            if (1 <= blocks.size()) {
                Element element = current.computeIfAbsent(blocks.get(0), k -> new Element());
                element.fail(blocks.subList(1, blocks.size()));
            }
        }

        private double getSuccessPercent() {
            return (double) successCounter / allCounter;
        }

        private boolean isVisited(List<Block> blocks, int depth) {
            Block block = blocks.get(depth);
            if (depth < blocks.size() - 1) {
                return current.containsKey(block) && current.get(block).isVisited(blocks, depth + 1);
            } else {
                return current.containsKey(block);
            }
        }
    }

    private final Element rootElement = new Element();

    public void success(List<Block> blocks) {
        rootElement.success(blocks);
    }

    public void fail(List<Block> blocks) {
        rootElement.fail(blocks);
    }

    public String show() {
        return String.format("success = %.2f%% (%d/%d)", rootElement.getSuccessPercent() * 100, rootElement.successCounter, rootElement.allCounter);
    }

    public void set(boolean result, List<Block> blocks) {
        if (result)
            success(blocks);
        else
            fail(blocks);
    }

    public double getSuccessPercent() {
        return rootElement.getSuccessPercent();
    }

    public boolean isVisited(List<Block> blocks) {
        return rootElement.isVisited(blocks, 0);
    }

    void tree() {
        tree(-1);
    }

    public String tree(int maxDepth) {
        String str = "";
        if (1 < rootElement.current.size())
            str += String.format("%s -> %.1f %%%n", "*", getSuccessPercent() * 100);
        return str + tree(rootElement, new LinkedList<>(), maxDepth);
    }

    private String tree(Element element, LinkedList<Block> stack, int maxDepth) {
        int depth = stack.size();

        if (0 <= maxDepth && maxDepth <= depth)
            return "";

        String str = "";
        for (Map.Entry<Block, Element> entry : element.current.entrySet()) {
            stack.addLast(entry.getKey());
            Element value = entry.getValue();
            str += String.format("%s∟ %s -> %.1f %%%n", repeat("  ", depth), toString(stack), value.getSuccessPercent() * 100);
            if (1 <= value.current.size()) {
                str += tree(value, stack, maxDepth);
            }
            stack.pollLast();
        }

        return str;
    }

    private String repeat(String str, int maxCount) {
        StringBuilder builder = new StringBuilder();
        for (int count = 0; count < maxCount; count++)
            builder.append(str);
        return builder.toString();
    }

    private String toString(LinkedList<Block> stack) {
        return String.join("", stack.stream().map(Block::name).collect(Collectors.toList()));
    }
}
