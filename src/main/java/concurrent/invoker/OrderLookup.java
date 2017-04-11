package concurrent.invoker;

import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

public class OrderLookup {
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
}
