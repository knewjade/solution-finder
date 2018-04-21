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

        // 最後まで到達した場合は成功扱い
        if (first == null)
            return true;

        // 再帰的に確認してOKだったときは成功
        return checksChildWithoutHold(piecesList, first);
    }

    private boolean checksChildWithoutHold(LinkedList<Piece> piecesList, Piece piece) {
        if (!existsChild(piece))
            return false;

        SuccessTreeLink successTreeLink = children.get(piece);
        return successTreeLink == TERMINATION || successTreeLink.checksWithoutHold(piecesList);
    }

    // ホールドありで成功か
    boolean checksWithHold(LinkedList<Piece> piecesList) {
        Piece first = piecesList.pollFirst();

        // 最後まで到達した場合は成功扱い
        if (first == null)
            return true;

        // 再帰的に確認してOKだったときは成功
        if (checksChildWithHold(piecesList, first)) {
            return true;
        }

        // うまくいかなかったときはHoldする
        Piece second = piecesList.pollFirst();
        piecesList.addFirst(first);

        // ラスト1ミノだった場合
        if (second == null) {
            // 最後に次の子で置けるか直接確かめる
            for (SuccessTreeLink link : children.values()) {
                // 子がある場合は、そこで使えるか確認する
                if (link != TERMINATION && link.existsChild(first))
                    return true;
            }
            return false;
        }

        // 再帰的に確認してOKだったときは成功
        return checksChildWithHold(piecesList, second);
    }

    private boolean checksChildWithHold(LinkedList<Piece> piecesList, Piece piece) {
        if (!existsChild(piece))
            return false;

        SuccessTreeLink successTreeLink = children.get(piece);
        return successTreeLink == TERMINATION || successTreeLink.checksWithHold(piecesList);
    }

    private boolean existsChild(Piece first) {
        return children.containsKey(first);
    }
}