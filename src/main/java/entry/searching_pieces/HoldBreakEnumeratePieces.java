package entry.searching_pieces;

import concurrent.checker.invoker.OrderLookup;
import concurrent.checker.invoker.Pieces;
import core.mino.Block;
import misc.pattern.PiecesGenerator;
import misc.pieces.SafePieces;
import misc.tree.VisitedTree;

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
        for (SafePieces pieces : generator) {
            counter++;
            List<Block> blocks = pieces.getBlocks();

            // ホールドありパターンから複数のホールドなしに分解
            List<Pieces> forward = OrderLookup.forward(blocks, combinationPopCount);

            for (Pieces piecesWithNoHold : forward) {
                List<Block> blocksWithNoHold = piecesWithNoHold.getBlocks();
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
