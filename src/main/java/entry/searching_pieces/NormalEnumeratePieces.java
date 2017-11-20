package entry.searching_pieces;

import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.pattern.PatternGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 通常の組み合わせ
 * PiecesGeneratorから重複を取り除く
 */
public class NormalEnumeratePieces implements EnumeratePiecesCore {
    private final PatternGenerator generator;
    private final int maxDepth;
    private int counter = -1;

    public NormalEnumeratePieces(PatternGenerator generator, int maxDepth, boolean isUsingHold) {
        this.generator = generator;
        this.maxDepth = isUsingHold ? maxDepth + 1 : maxDepth;
    }

    @Override
    public Set<LongPieces> enumerate() {
        assert counter == -1;

        int depth = generator.getDepth();

        AtomicInteger counter = new AtomicInteger();
        HashSet<LongPieces> searchingPieces = create(depth, counter);

        this.counter = counter.get();
        return searchingPieces;
    }

    private HashSet<LongPieces> create(int depth, AtomicInteger counter) {
        if (maxDepth < depth)
            return createOverMinos(counter);
        else
            return createJustMinos(counter);
    }

    private HashSet<LongPieces> createJustMinos(AtomicInteger counter) {
        return generator.blocksStream()
                .peek(pieces -> counter.incrementAndGet())
                .map(Pieces::blockStream)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<LongPieces> createOverMinos(AtomicInteger counter) {
        return generator.blocksStream()
                .peek(pieces -> counter.incrementAndGet())
                .map(Pieces::blockStream)
                .map(stream -> stream.limit(maxDepth))
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public int getCounter() {
        return counter;
    }
}
