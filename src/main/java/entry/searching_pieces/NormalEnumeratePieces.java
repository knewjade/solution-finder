package entry.searching_pieces;

import core.mino.Block;
import misc.PiecesGenerator;
import misc.SafePieces;
import tree.VisitedTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通常の組み合わせ
 * PiecesGeneratorから重複を取り除く
 */
public class NormalEnumeratePieces implements EnumeratePiecesCore {
    private final PiecesGenerator generator;
    private final int combinationPopCount;
    private int duplicate = -1;

    public NormalEnumeratePieces(PiecesGenerator generator, int maxDepth, boolean isUsingHold) {
        int piecesDepth = generator.getDepth();

        int combinationPopCount = isUsingHold ? maxDepth + 1 : maxDepth;
        if (piecesDepth < combinationPopCount)
            combinationPopCount = piecesDepth;

        this.generator = generator;
        this.combinationPopCount = combinationPopCount;
    }

    @Override
    public List<List<Block>> enumerate() throws IOException {
        int counter = 0;
        List<List<Block>> searchingPieces = new ArrayList<>();
        VisitedTree duplicateCheckTree = new VisitedTree();
        boolean isOverPieces = combinationPopCount < generator.getDepth();

        // 組み合わせの列挙
        for (SafePieces pieces : generator) {
            counter++;
            List<Block> blocks = pieces.getBlocks();
            if (isOverPieces)
                blocks = blocks.subList(0, combinationPopCount);

            // 重複を削除
            if (!duplicateCheckTree.isVisited(blocks)) {
                searchingPieces.add(blocks);
                duplicateCheckTree.success(blocks);
            }
        }

        this.duplicate = counter;

        return searchingPieces;
    }

    public int getCounter() {
        return duplicate;
    }
}
