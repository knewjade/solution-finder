package common.tree;

import core.mino.Piece;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

class SuccessTreeLink {
    private static final SuccessTreeLink TERMINATION = new SuccessTreeLink();
    private final EnumMap<Piece, SuccessTreeLink> children = new EnumMap<>(Piece.class);

    // 成功するミノ順を登録
    void register(LinkedList<Piece> piecesList) {
        Piece key = piecesList.pollFirst();
        if (piecesList.isEmpty()) {
            children.computeIfAbsent(key, this::createTail);
        } else {
            SuccessTreeLink link = children.computeIfAbsent(key, this::createLink);
            link.register(piecesList);
        }
    }

    // ふたつの木をマージする
    void merge(SuccessTreeLink other) {
        Set<Map.Entry<Piece, SuccessTreeLink>> entries = other.children.entrySet();
        for (Map.Entry<Piece, SuccessTreeLink> entry : entries) {
            SuccessTreeLink value = entry.getValue();
            Piece key = entry.getKey();
            if (value == TERMINATION) {
                children.computeIfAbsent(key, this::createTail);
            } else {
                SuccessTreeLink link = children.computeIfAbsent(key, this::createLink);
                link.merge(value);
            }
        }
    }

    private SuccessTreeLink createTail(Piece key) {
        return TERMINATION;
    }

    private SuccessTreeLink createLink(Piece key) {
        return new SuccessTreeLink();
    }

    // ホールドなしで成功か
    boolean checksWithoutHold(LinkedList<Piece> piecesList) {
        Piece first = piecesList.pollFirst();
        if (children.containsKey(first)) {
            if (checksChildWithoutHold(piecesList, first))
                return true;
        }
        piecesList.addFirst(first);
        return false;
    }

    private boolean checksChildWithoutHold(LinkedList<Piece> piecesList, Piece first) {
        if (!children.containsKey(first))
            return false;

        SuccessTreeLink successTreeLink = children.get(first);
        return successTreeLink == TERMINATION || successTreeLink.checksWithoutHold(piecesList);
    }

    // ホールドありで成功か
    boolean checksWithHold(LinkedList<Piece> piecesList) {
        Piece first = piecesList.pollFirst();
        if (checksChildWithHold(piecesList, first))
            return true;

        Piece second = piecesList.pollFirst();
        piecesList.addFirst(first);

        if (checksChildWithHold(piecesList, second))
            return true;

        piecesList.add(1, second);

        return false;
    }

    private boolean checksChildWithHold(LinkedList<Piece> piecesList, Piece first) {
        if (!children.containsKey(first))
            return false;

        SuccessTreeLink successTreeLink = children.get(first);
        return successTreeLink == TERMINATION || successTreeLink.checksWithHold(piecesList);
    }
}