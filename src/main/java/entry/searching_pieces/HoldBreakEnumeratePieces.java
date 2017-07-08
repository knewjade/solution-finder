package entry.searching_pieces;

import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import common.order.ForwardOrderLookUp;
import common.pattern.PiecesGenerator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ホールドありの組み合わせから複数のホールドなしの組み合わせに分解し、重複を取り除く
 */
public class HoldBreakEnumeratePieces implements EnumeratePiecesCore {
    private final PiecesGenerator generator;
    private final int maxDepth;
    private int counter = -1;

    HoldBreakEnumeratePieces(PiecesGenerator generator, int maxDepth) {
        assert maxDepth <= generator.getDepth();
        this.generator = generator;
        this.maxDepth = maxDepth;
    }

    @Override
    public Set<Pieces> enumerate() throws IOException {
        assert counter == -1;

        int depth = generator.getDepth();
        ForwardOrderLookUp forwardOrderLookUp = new ForwardOrderLookUp(maxDepth, depth);

        AtomicInteger counter = new AtomicInteger();
        HashSet<Pieces> searchingPieces = create(depth, forwardOrderLookUp, counter);

        this.counter = counter.get();
        return searchingPieces;
    }

    private HashSet<Pieces> create(int depth, ForwardOrderLookUp forwardOrderLookUp, AtomicInteger counter) {
        if (maxDepth < depth)
            return createOverMinos(forwardOrderLookUp, counter);
        else
            return createJustMinos(forwardOrderLookUp, counter);
    }

    private HashSet<Pieces> createJustMinos(ForwardOrderLookUp forwardOrderLookUp, AtomicInteger counter) {
        return generator.stream()
                .peek(pieces -> counter.incrementAndGet())
                .map(Pieces::getBlocks)
                .flatMap(forwardOrderLookUp::parse)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<Pieces> createOverMinos(ForwardOrderLookUp forwardOrderLookUp, AtomicInteger counter) {
        return generator.stream()
                .peek(pieces -> counter.incrementAndGet())
                .map(Pieces::getBlocks)
                .map(blocks -> blocks.subList(0, maxDepth + 1))  // ホールドありなので+1ミノ分使用する
                .flatMap(forwardOrderLookUp::parse)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public int getCounter() {
        return counter;
    }
}
