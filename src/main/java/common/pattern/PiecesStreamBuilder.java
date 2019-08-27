package common.pattern;

import common.datastore.PieceCounter;
import common.datastore.blocks.LongLongPieces;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PiecesStreamBuilder {
    private final List<Element> elements;
    private final int lastIndex;
    private final int depth;

    PiecesStreamBuilder(List<Element> elements, int depth) {
        this.elements = elements;
        this.lastIndex = elements.size() - 1;
        this.depth = depth;
        assert 0 <= depth && depth <= 44 : depth;
    }

    Stream<Pieces> blocksStream() {
        List<List<Pieces>> combinations = createPermutations();
        Stream.Builder<Pieces> builder = Stream.builder();
        if (!combinations.isEmpty())
            enumerate(combinations, builder, depth < 22 ? new LongPieces() : new LongLongPieces(), 0);
        return builder.build();
    }

    private List<List<Pieces>> createPermutations() {
        return elements.stream()
                .map(Element::getPermutationBlocks)
                .collect(Collectors.toList());
    }

    private void enumerate(List<List<Pieces>> combinations, Stream.Builder<Pieces> builder, Pieces pieces, int index) {
        for (Pieces combination : combinations.get(index)) {
            Pieces newPieces = pieces.addAndReturnNew(combination.blockStream());
            if (index == lastIndex) {
                builder.accept(newPieces);
            } else {
                enumerate(combinations, builder, newPieces, index + 1);
            }
        }
    }

    Stream<PieceCounter> blockCountersStream() {
        List<List<PieceCounter>> blockCounters = createBlockCounters();
        Stream.Builder<PieceCounter> builder = Stream.builder();
        if (!blockCounters.isEmpty())
            enumerate(blockCounters, builder, new PieceCounter(), 0);
        return builder.build();
    }

    private List<List<PieceCounter>> createBlockCounters() {
        return elements.stream()
                .map(Element::getPieceCounters)
                .collect(Collectors.toList());
    }

    private void enumerate(List<List<PieceCounter>> blockCounters, Stream.Builder<PieceCounter> builder, PieceCounter accumulate, int index) {
        for (PieceCounter pieceCounter : blockCounters.get(index)) {
            PieceCounter newPieceCounter = accumulate.addAndReturnNew(pieceCounter);
            if (index == lastIndex) {
                builder.accept(newPieceCounter);
            } else {
                enumerate(blockCounters, builder, newPieceCounter, index + 1);
            }
        }
    }
}
