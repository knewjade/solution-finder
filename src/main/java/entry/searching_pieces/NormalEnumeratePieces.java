package entry.searching_pieces;

import common.datastore.pieces.LongBlocks;
import common.datastore.pieces.Blocks;
import common.pattern.IBlocksGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 通常の組み合わせ
 * PiecesGeneratorから重複を取り除く
 */
public class NormalEnumeratePieces implements EnumeratePiecesCore {
    private final IBlocksGenerator generator;
    private final int maxDepth;
    private int counter = -1;

    public NormalEnumeratePieces(IBlocksGenerator generator, int maxDepth, boolean isUsingHold) {
        this.generator = generator;
        this.maxDepth = isUsingHold ? maxDepth + 1 : maxDepth;
    }

    @Override
    public Set<LongBlocks> enumerate() {
        assert counter == -1;

        int depth = generator.getDepth();

        AtomicInteger counter = new AtomicInteger();
        HashSet<LongBlocks> searchingPieces = create(depth, counter);

        this.counter = counter.get();
        return searchingPieces;
    }

    private HashSet<LongBlocks> create(int depth, AtomicInteger counter) {
        if (maxDepth < depth)
            return createOverMinos(counter);
        else
            return createJustMinos(counter);
    }

    private HashSet<LongBlocks> createJustMinos(AtomicInteger counter) {
        return generator.blocksStream()
                .peek(pieces -> counter.incrementAndGet())
                .map(Blocks::blockStream)
                .map(LongBlocks::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<LongBlocks> createOverMinos(AtomicInteger counter) {
        return generator.blocksStream()
                .peek(pieces -> counter.incrementAndGet())
                .map(Blocks::blockStream)
                .map(stream -> stream.limit(maxDepth))
                .map(LongBlocks::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public int getCounter() {
        return counter;
    }
}
