package common.tree;

import common.datastore.blocks.Pieces;
import core.mino.Piece;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * マルチスレッド非対応
 * 同じミノを入れたとき親の要素は複数回分追加される
 */
public class AnalyzeTree {
    private static class Element {
        private final EnumMap<Piece, Element> current = new EnumMap<>(Piece.class);
        private int allCounter = 0;
        private int successCounter = 0;

        private void success(List<Piece> pieces) {
            allCounter += 1;
            successCounter += 1;
            if (1 <= pieces.size()) {
                Element element = current.computeIfAbsent(pieces.get(0), k -> new Element());
                element.success(pieces.subList(1, pieces.size()));
            }
        }

        private void fail(List<Piece> pieces) {
            allCounter += 1;
            if (1 <= pieces.size()) {
                Element element = current.computeIfAbsent(pieces.get(0), k -> new Element());
                element.fail(pieces.subList(1, pieces.size()));
            }
        }

        private double getSuccessPercent() {
            return (double) successCounter / allCounter;
        }

        private boolean isVisited(List<Piece> pieces, int depth) {
            Piece piece = pieces.get(depth);
            if (depth < pieces.size() - 1) {
                return current.containsKey(piece) && current.get(piece).isVisited(pieces, depth + 1);
            } else {
                return current.containsKey(piece);
            }
        }

        private boolean isSuccess(List<Piece> pieces, int depth) {
            Piece piece = pieces.get(depth);
            if (depth < pieces.size() - 1) {
                return current.containsKey(piece) && current.get(piece).isSuccess(pieces, depth + 1);
            } else {
                assert current.containsKey(piece) && (current.get(piece).successCounter == 0 || current.get(piece).successCounter == current.get(piece).allCounter);
                return current.containsKey(piece) && 0 < current.get(piece).successCounter;
            }
        }
    }

    private final Element rootElement = new Element();

    public void success(List<Piece> pieces) {
        rootElement.success(pieces);
    }

    // TODO: write unittest
    public void success(Pieces pieces) {
        rootElement.success(pieces.getPieces());
    }

    public void fail(List<Piece> pieces) {
        rootElement.fail(pieces);
    }

    // TODO: write unittest
    public void fail(Pieces pieces) {
        rootElement.fail(pieces.getPieces());
    }

    public String show() {
        return String.format("success = %.2f%% (%d/%d)", rootElement.getSuccessPercent() * 100, rootElement.successCounter, rootElement.allCounter);
    }

    public void set(boolean result, List<Piece> pieces) {
        if (result)
            success(pieces);
        else
            fail(pieces);
    }

    // TODO: write unittest
    public void set(boolean result, Pieces pieces) {
        if (result)
            success(pieces);
        else
            fail(pieces);
    }

    public double getSuccessPercent() {
        return rootElement.getSuccessPercent();
    }

    // TODO: write unittest
    public boolean isVisited(List<Piece> pieces) {
        return rootElement.isVisited(pieces, 0);
    }

    // TODO: write unittest
    public boolean isVisited(Pieces pieces) {
        return rootElement.isVisited(pieces.getPieces(), 0);
    }

    public boolean isSucceed(List<Piece> pieces) {
        return rootElement.isSuccess(pieces, 0);
    }

    // TODO: write unittest
    public boolean isSucceed(Pieces pieces) {
        return rootElement.isSuccess(pieces.getPieces(), 0);
    }

    public String tree(int maxDepth) {
        String str = "";
        if (1 < rootElement.current.size()) {
            double successPercent = getSuccessPercent();
            str += String.format("%s -> %.2f %%%n", "*", successPercent * 100);
        }
        return str + tree(rootElement, new LinkedList<>(), maxDepth);
    }

    private String tree(Element element, LinkedList<Piece> stack, int maxDepth) {
        int depth = stack.size();

        if (0 <= maxDepth && maxDepth <= depth)
            return "";

        StringBuilder str = new StringBuilder();
        for (Map.Entry<Piece, Element> entry : element.current.entrySet()) {
            stack.addLast(entry.getKey());
            Element value = entry.getValue();
            str.append(String.format("%s∟ %s -> %.2f %%%n", repeat("  ", depth), toString(stack), value.getSuccessPercent() * 100));
            if (1 <= value.current.size()) {
                str.append(tree(value, stack, maxDepth));
            }
            stack.pollLast();
        }

        return str.toString();
    }

    private String repeat(String str, int maxCount) {
        StringBuilder builder = new StringBuilder();
        for (int count = 0; count < maxCount; count++)
            builder.append(str);
        return builder.toString();
    }

    private String toString(LinkedList<Piece> stack) {
        return String.join("", stack.stream().map(Piece::name).collect(Collectors.toList()));
    }
}
