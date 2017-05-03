package common.order;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

// TODO: unittest テストを強化する
public class OrderLookup {
    // 他のミノ列からホールドを利用して指定したミノ列にできるとき、その他のミノ列をすべて逆算して列挙
    public static ArrayList<Pieces> reverse(List<Block> blocks, int maxDepth) {
        ArrayList<Pieces> candidates = new ArrayList<>();
        candidates.add(new ListPieces());

        for (int depth = 0; depth < maxDepth; depth++) {
            Block block = depth < blocks.size() ? blocks.get(depth) : null;
            int size = candidates.size();
            if (depth < maxDepth - 1) {
                for (int index = 0; index < size; index++) {
                    Pieces pieces = candidates.get(index);
                    Pieces freeze = pieces.freeze();

                    pieces.addLast(block);
                    freeze.stock(block);

                    candidates.add(freeze);
                }
            } else {
                for (Pieces pieces : candidates)
                    pieces.stock(block);
            }
        }

        return candidates;
    }

    // 指定したミノ列からホールドを利用して並び替えられるミノ列をすべて列挙
    public static ArrayList<Pieces> forward(List<Block> blocks, int maxDepth) {
        assert 1 < maxDepth && maxDepth <= blocks.size();

        ArrayList<Pieces> candidates = new ArrayList<>();
        ListPieces e = new ListPieces();
        e.addLast(blocks.get(0));
        e.addLast(blocks.get(1));
        candidates.add(e);

        ListPieces e2 = new ListPieces();
        e2.addLast(blocks.get(1));
        e2.addLast(blocks.get(0));
        candidates.add(e2);

        for (int depth = 2; depth < maxDepth; depth++) {
            Block block = blocks.get(depth);
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                Pieces pieces = candidates.get(index);
                Pieces freeze = pieces.freeze();

                pieces.addLastTwo(block);  // おく
                freeze.addLast(block);  // holdする

                candidates.add(freeze);
            }
        }

        return candidates;
    }

    private OrderLookup() {
    }
}
