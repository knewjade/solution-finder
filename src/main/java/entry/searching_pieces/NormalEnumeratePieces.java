package entry.searching_pieces;

import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import common.pattern.PiecesGenerator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 通常の組み合わせ
 * PiecesGeneratorから重複を取り除く
 */
public class NormalEnumeratePieces implements EnumeratePiecesCore {
    private final PiecesGenerator generator;
    private final int maxDepth;
    private int counter = -1;

    public NormalEnumeratePieces(PiecesGenerator generator, int maxDepth, boolean isUsingHold) {
        this.generator = generator;
        this.maxDepth = isUsingHold ? maxDepth + 1 : maxDepth;
    }

    @Override
    public Set<Pieces> enumerate() throws IOException {
        assert counter == -1;

        int depth = generator.getDepth();

        AtomicInteger counter = new AtomicInteger();
        HashSet<Pieces> searchingPieces = create(depth, counter);

        this.counter = counter.get();
        return searchingPieces;
    }

    private HashSet<Pieces> create(int depth, AtomicInteger counter) {
        if (maxDepth < depth)
            return createOverMinos(counter);
        else
            return createJustMinos(counter);
    }

    private HashSet<Pieces> createJustMinos(AtomicInteger counter) {
        return generator.stream()
                .peek(pieces -> counter.incrementAndGet())
                .map(Pieces::getBlockStream)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<Pieces> createOverMinos(AtomicInteger counter) {
        return generator.stream()
                .peek(pieces -> counter.incrementAndGet())
                .map(Pieces::getBlockStream)
                .map(stream -> stream.limit(maxDepth))
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public int getCounter() {
        return counter;
    }
}
