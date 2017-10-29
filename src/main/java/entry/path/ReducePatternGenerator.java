package entry.path;

import common.datastore.PieceCounter;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.iterable.CombinationIterable;
import common.pattern.PatternGenerator;
import core.mino.Piece;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReducePatternGenerator implements PatternGenerator {
    private final PatternGenerator blocksGenerator;
    private final int maxDepth;

    public ReducePatternGenerator(PatternGenerator blocksGenerator, int maxDepth) {
        this.blocksGenerator = blocksGenerator;
        this.maxDepth = maxDepth;
    }

    @Override
    public int getDepth() {
        int depth = blocksGenerator.getDepth();
        if (maxDepth < depth)
            return maxDepth;
        return depth;
    }

    public Stream<Pieces> blocksStream() {
        if (blocksGenerator.getDepth() <= maxDepth)
            return blocksGenerator.blocksStream();
        else
            return blocksGenerator.blocksStream()
                    .map(Pieces::getPieces)
                    .map(blocks -> blocks.subList(0, maxDepth))
                    .map(LongPieces::new);
    }

    public Stream<Pieces> blocksParallelStream() {
        return blocksStream().parallel();
    }

    public Stream<PieceCounter> blockCountersStream() {
        if (blocksGenerator.getDepth() <= maxDepth)
            return blocksGenerator.blockCountersStream();
        else
            return blocksGenerator.blockCountersStream()
                    .map(PieceCounter::getBlocks)
                    .flatMap(blocks -> {
                        CombinationIterable<Piece> lists = new CombinationIterable<>(blocks, maxDepth);
                        return StreamSupport.stream(lists.spliterator(), false);
                    })
                    .map(PieceCounter::new);
    }

    public Stream<PieceCounter> blockCountersParallelStream() {
        return blockCountersStream().parallel();
    }
}
