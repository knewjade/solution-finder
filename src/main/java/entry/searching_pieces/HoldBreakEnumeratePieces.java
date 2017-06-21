package entry.searching_pieces;

import common.datastore.pieces.Pieces;
import common.order.OrderLookup;
import common.order.StackOrder;
import common.pattern.PiecesGenerator;
import common.tree.VisitedTree;
import core.mino.Block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ホールドありの組み合わせから複数のホールドなしの組み合わせに分解し、重複を取り除く
 */
public class HoldBreakEnumeratePieces implements EnumeratePiecesCore {
    private final PiecesGenerator generator;
    private final int maxDepth;
    private final int combinationPopCount;
    private int duplicate = -1;

    public HoldBreakEnumeratePieces(PiecesGenerator generator, int maxDepth) {
        int piecesDepth = generator.getDepth();

        int combinationPopCount = maxDepth + 1;
        if (piecesDepth < combinationPopCount)
            combinationPopCount = piecesDepth;

        this.generator = generator;
        this.maxDepth = maxDepth;
        this.combinationPopCount = combinationPopCount;
    }

    @Override
    public List<List<Block>> enumerate() throws IOException {
        int counter = 0;
        List<List<Block>> searchingPieces = new ArrayList<>();
        VisitedTree duplicateCheckTree = new VisitedTree();
        boolean isOverPieces = maxDepth < combinationPopCount;

        // 組み合わせの列挙
        for (Pieces pieces : generator) {
            counter++;
            List<Block> blocks = pieces.getBlocks();

            // ホールドありパターンから複数のホールドなしに分解
            List<StackOrder<Block>> forward = OrderLookup.forwardBlocks(blocks, combinationPopCount);

            for (StackOrder<Block> piecesWithNoHold : forward) {
                List<Block> blocksWithNoHold = piecesWithNoHold.toList();
                if (isOverPieces)
                    blocksWithNoHold = blocksWithNoHold.subList(0, maxDepth);

                // 重複するホールドなしパターンを除去
                if (!duplicateCheckTree.isVisited(blocksWithNoHold)) {
                    searchingPieces.add(blocksWithNoHold);
                    duplicateCheckTree.success(blocksWithNoHold);
                }
            }
        }

        this.duplicate = counter;

        return searchingPieces;
    }

    @Override
    public int getCounter() {
        return duplicate;
    }
}
