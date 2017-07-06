package entry.searching_pieces;

import common.datastore.pieces.Pieces;
import common.pattern.PiecesGenerator;
import common.tree.VisitedTree;
import core.mino.Block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Set<Pieces> enumerate() throws IOException {
        int counter = 0;
        HashSet<Pieces> uniquePieces = new HashSet<>();

        boolean isOverPieces = combinationPopCount < generator.getDepth();
        if (isOverPieces) {
            // generatorが必要なミノ数以上に生成するとき

        } else {
            return generator.stream().collect(Collectors.toCollection(HashSet::new));
        }


        List<List<Block>> searchingPieces = new ArrayList<>();
        VisitedTree duplicateCheckTree = new VisitedTree();

        // 組み合わせの列挙
        for (Pieces pieces : generator) {
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

        return uniquePieces;
    }

    @Override
    public int getCounter() {
        return duplicate;
    }
}
