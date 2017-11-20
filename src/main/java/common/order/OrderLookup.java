package common.order;

import core.mino.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderLookup {
    // 他のミノ列からホールドを利用して指定したミノ列にできるとき、その他のミノ列をすべて逆算して列挙
    // ??? -- [hold] --> blocks
    // fromDepth: ???の深さ
    public static ArrayList<StackOrder<Piece>> reverseBlocks(List<Piece> blocks, int fromDepth) {
        assert blocks.size() <= fromDepth;
        ArrayList<StackOrder<Piece>> candidates = new ArrayList<>();
        StackOrder<Piece> e = new LongStackOrder();
        candidates.add(e);

        for (int depth = 0; depth < fromDepth; depth++) {
            Piece piece = depth < blocks.size() ? blocks.get(depth) : null;
            int size = candidates.size();
            if (depth < fromDepth - 1) {
                for (int index = 0; index < size; index++) {
                    StackOrder<Piece> pieces = candidates.get(index);
                    StackOrder<Piece> freeze = pieces.freeze();

                    pieces.addLast(piece);
                    freeze.stock(piece);

                    candidates.add(freeze);
                }
            } else {
                for (StackOrder<Piece> pieces : candidates)
                    pieces.stock(piece);
            }
        }

        return candidates.stream()
                .map(StackOrder::fix)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // 指定したミノ列からホールドを利用して並び替えられるミノ列をすべて列挙
    // blocks -- [hold] --> ???
    // toDepth: ???の深さ
    public static ArrayList<StackOrder<Piece>> forwardBlocks(List<Piece> blocks, int toDepth) {
        assert 1 < toDepth && toDepth <= blocks.size(): toDepth;

        ArrayList<StackOrder<Piece>> candidates = new ArrayList<>();
        StackOrder<Piece> e = new LongStackOrder();
        e.addLast(blocks.get(0));
        e.addLast(blocks.get(1));
        candidates.add(e);

        StackOrder<Piece> e2 = new LongStackOrder();
        e2.addLast(blocks.get(1));
        e2.addLast(blocks.get(0));
        candidates.add(e2);

        for (int depth = 2; depth < toDepth; depth++) {
            Piece piece = blocks.get(depth);
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                StackOrder<Piece> pieces = candidates.get(index);
                StackOrder<Piece> freeze = pieces.freeze();

                pieces.addLastTwo(piece);  // おく
                freeze.addLast(piece);  // holdする

                candidates.add(freeze);
            }
        }

        if (toDepth < blocks.size()) {
            Piece piece = blocks.get(toDepth);
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                StackOrder<Piece> pieces = candidates.get(index);
                StackOrder<Piece> freeze = pieces.freeze();

                pieces.addLastTwoAndRemoveLast(piece);  // おく

                candidates.add(freeze);
            }
        }

        return candidates.stream()
                .map(StackOrder::fix)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private OrderLookup() {
    }
}
