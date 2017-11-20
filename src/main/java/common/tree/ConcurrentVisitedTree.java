package common.tree;

import core.mino.Piece;

import java.util.EnumMap;
import java.util.List;

public class ConcurrentVisitedTree {
    public static final short NO_RESULT = -1;
    public static final short SUCCEED = 0;
    public static final short FAILED = 1;

    private static class Element {
        private final EnumMap<Piece, Element> current = new EnumMap<>(Piece.class);
        private int isSucceed = NO_RESULT;

        private void success(List<Piece> pieces, int depth) {
            if (depth < pieces.size()) {
                Piece currentPiece = pieces.get(depth);
                if (currentPiece != null) {
                    Element element = current.computeIfAbsent(currentPiece, k -> new Element());
                    element.success(pieces, depth + 1);
                } else {
                    for (Piece piece : Piece.values()) {
                        Element element = current.computeIfAbsent(piece, k -> new Element());
                        element.success(pieces, depth + 1);
                    }
                }
            } else {
                isSucceed = SUCCEED;
            }
        }

        private void fail(List<Piece> pieces, int depth) {
            if (depth < pieces.size()) {
                Piece currentPiece = pieces.get(depth);
                if (currentPiece != null) {
                    Element element = current.computeIfAbsent(currentPiece, k -> new Element());
                    element.fail(pieces, depth + 1);
                } else {
                    for (Piece piece : Piece.values()) {
                        Element element = current.computeIfAbsent(piece, k -> new Element());
                        element.fail(pieces, depth + 1);
                    }
                }
            } else {
                isSucceed = FAILED;
            }
        }

        private boolean isVisited(List<Piece> pieces, int depth) {
            Piece piece = pieces.get(depth);
            if (depth + 1 < pieces.size()) {
                return current.containsKey(piece) && current.get(piece).isVisited(pieces, depth + 1);
            } else {
                return current.containsKey(piece);
            }
        }

        private int isSucceed(List<Piece> pieces, int depth) {
            if (pieces.size() <= depth)
                return isSucceed;

            Piece piece = pieces.get(depth);
            if (!current.containsKey(piece))
                return NO_RESULT;

            return current.get(piece).isSucceed(pieces, depth + 1);
        }
    }

    private final Element rootElement = new Element();

    public void success(List<Piece> pieces) {
        synchronized (rootElement) {
            rootElement.success(pieces, 0);
        }
    }

    public void fail(List<Piece> pieces) {
        synchronized (rootElement) {
            rootElement.fail(pieces, 0);
        }
    }

    public void set(boolean result, List<Piece> pieces) {
        if (result)
            success(pieces);
        else
            fail(pieces);
    }

    public int isSucceed(List<Piece> pieces) {
        synchronized (rootElement) {
            return rootElement.isSucceed(pieces, 0);
        }
    }
}
