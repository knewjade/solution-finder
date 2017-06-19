package common.order;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

// TODO: unittest テストを強化する
// TODO: ListPiecesをlong実装にする
public class OrderLookup {
    // 他のミノ列からホールドを利用して指定したミノ列にできるとき、その他のミノ列をすべて逆算して列挙
    public static ArrayList<ListOrder> reverse(List<Block> blocks, int toDepth) {
        assert blocks.size() <= toDepth;
        ArrayList<ListOrder> candidates = new ArrayList<>();
        candidates.add(new ListOrder());

        for (int depth = 0; depth < toDepth; depth++) {
            Block block = depth < blocks.size() ? blocks.get(depth) : null;
            int size = candidates.size();
            if (depth < toDepth - 1) {
                for (int index = 0; index < size; index++) {
                    ListOrder pieces = candidates.get(index);
                    ListOrder freeze = pieces.freeze();

                    pieces.addLast(block);
                    freeze.stock(block);

                    candidates.add(freeze);
                }
            } else {
                for (ListOrder pieces : candidates)
                    pieces.stock(block);
            }
        }

        return candidates;
    }

    // 指定したミノ列からホールドを利用して並び替えられるミノ列をすべて列挙
    public static ArrayList<ListOrder> forward(List<Block> blocks, int toDepth) {
        assert 1 < toDepth && toDepth <= blocks.size();

        ArrayList<ListOrder> candidates = new ArrayList<>();
        ListOrder e = new ListOrder();
        e.addLast(blocks.get(0));
        e.addLast(blocks.get(1));
        candidates.add(e);

        ListOrder e2 = new ListOrder();
        e2.addLast(blocks.get(1));
        e2.addLast(blocks.get(0));
        candidates.add(e2);

        for (int depth = 2; depth < toDepth; depth++) {
            Block block = blocks.get(depth);
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                ListOrder pieces = candidates.get(index);
                ListOrder freeze = pieces.freeze();

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
