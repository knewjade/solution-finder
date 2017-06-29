package common.order;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: unittest テストを強化する
// TODO: ListPiecesをlong実装にする
public class OrderLookup {
    // 他のミノ列からホールドを利用して指定したミノ列にできるとき、その他のミノ列をすべて逆算して列挙
    public static ArrayList<StackOrder<Block>> reverseBlocks(List<Block> blocks, int fromDepth) {
        assert blocks.size() <= fromDepth;
        ArrayList<StackOrder<Block>> candidates = new ArrayList<>();
        StackOrder<Block> e = new LongStackOrder();
        candidates.add(e);

        for (int depth = 0; depth < fromDepth; depth++) {
            Block block = depth < blocks.size() ? blocks.get(depth) : null;
            int size = candidates.size();
            if (depth < fromDepth - 1) {
                for (int index = 0; index < size; index++) {
                    StackOrder<Block> pieces = candidates.get(index);
                    StackOrder<Block> freeze = pieces.freeze();

                    pieces.addLast(block);
                    freeze.stock(block);

                    candidates.add(freeze);
                }
            } else {
                for (StackOrder<Block> pieces : candidates)
                    pieces.stock(block);
            }
        }

        return candidates.stream()
                .map(StackOrder::fix)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // 指定したミノ列からホールドを利用して並び替えられるミノ列をすべて列挙
    public static ArrayList<StackOrder<Block>> forwardBlocks2(List<Block> blocks, int toDepth) {
        assert 1 < toDepth && toDepth <= blocks.size();

        ArrayList<StackOrder<Block>> candidates = new ArrayList<>();
        StackOrder<Block> e = new LongStackOrder();
        e.addLast(blocks.get(0));
        e.addLast(blocks.get(1));
        candidates.add(e);

        StackOrder<Block> e2 = new LongStackOrder();
        e2.addLast(blocks.get(1));
        e2.addLast(blocks.get(0));
        candidates.add(e2);

        for (int depth = 2; depth < toDepth; depth++) {
            Block block = blocks.get(depth);
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                StackOrder<Block> pieces = candidates.get(index);
                StackOrder<Block> freeze = pieces.freeze();

                pieces.addLastTwo(block);  // おく
                freeze.addLast(block);  // holdする

                candidates.add(freeze);
            }
        }

        if (toDepth < blocks.size()) {
            Block block = blocks.get(toDepth);
            int size = candidates.size();
            for (int index = 0; index < size; index++) {
                StackOrder<Block> pieces = candidates.get(index);
                StackOrder<Block> freeze = pieces.freeze();

                pieces.addLastTwoAndRemoveLast(block);  // おく

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
