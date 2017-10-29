package common.tree;

import core.mino.Piece;

import java.util.EnumMap;
import java.util.List;

// TODO: rewrite 結果はEnumにする
public class VisitedTree {
    public static final short NO_RESULT = -1;
    public static final short SUCCEED = 0;
    public static final short FAILED = 1;

    private static class Element {
        private final EnumMap<Piece, Element> current = new EnumMap<>(Piece.class);
        private int isSucceed = NO_RESULT;

        private void success(List<Piece> pieces) {
            if (1 <= pieces.size()) {
                Piece currentPiece = pieces.get(0);
                if (currentPiece != null) {
                    Element element = current.computeIfAbsent(currentPiece, k -> new Element());
                    element.success(pieces.subList(1, pieces.size()));
                } else {
                    for (Piece piece : Piece.values()) {
                        Element element = current.computeIfAbsent(piece, k -> new Element());
                        element.success(pieces.subList(1, pieces.size()));
                    }
                }
            } else {
                assert isSucceed != FAILED;
                isSucceed = SUCCEED;
            }
        }

        private void fail(List<Piece> pieces) {
            if (1 <= pieces.size()) {
                Piece currentPiece = pieces.get(0);
                if (currentPiece != null) {
                    Element element = current.computeIfAbsent(currentPiece, k -> new Element());
                    element.fail(pieces.subList(1, pieces.size()));
                } else {
                    for (Piece piece : Piece.values()) {
                        Element element = current.computeIfAbsent(piece, k -> new Element());
                        element.fail(pieces.subList(1, pieces.size()));
                    }
                }
            } else {
                assert isSucceed != SUCCEED;
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
        rootElement.success(pieces);
    }

    public void fail(List<Piece> pieces) {
        rootElement.fail(pieces);
    }

    public void set(boolean result, List<Piece> pieces) {
        if (result)
            success(pieces);
        else
            fail(pieces);
    }

    public boolean isVisited(List<Piece> pieces) {
        return rootElement.isVisited(pieces, 0);
    }

    public int isSucceed(List<Piece> pieces) {
        return rootElement.isSucceed(pieces, 0);
    }
}
