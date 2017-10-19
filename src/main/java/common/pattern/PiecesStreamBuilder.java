package common.pattern;

import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PiecesStreamBuilder {
    private final List<Element> elements;
    private final int lastIndex;

    PiecesStreamBuilder(List<Element> elements) {
        this.elements = elements;
        this.lastIndex = elements.size() - 1;
    }

    Stream<Blocks> blocksStream() {
        List<List<Blocks>> combinations = createPermutations();
        Stream.Builder<Blocks> builder = Stream.builder();
        if (!combinations.isEmpty())
            enumerate(combinations, builder, new LongBlocks(), 0);
        return builder.build();
    }

    private List<List<Blocks>> createPermutations() {
        return elements.stream()
                .map(Element::getPermutationBlocks)
                .collect(Collectors.toList());
    }

    private void enumerate(List<List<Blocks>> combinations, Stream.Builder<Blocks> builder, Blocks blocks, int index) {
        for (Blocks combination : combinations.get(index)) {
            Blocks newBlocks = blocks.addAndReturnNew(combination.blockStream());
            if (index == lastIndex) {
                builder.accept(newBlocks);
            } else {
                enumerate(combinations, builder, newBlocks, index + 1);
            }
        }
    }

    public Stream<BlockCounter> blockCountersStream() {
        List<List<BlockCounter>> blockCounters = createBlockCounters();
        Stream.Builder<BlockCounter> builder = Stream.builder();
        if (!blockCounters.isEmpty())
            enumerate(blockCounters, builder, new BlockCounter(), 0);
        return builder.build();
    }

    private List<List<BlockCounter>> createBlockCounters() {
        return elements.stream()
                .map(Element::getBlockCounters)
                .collect(Collectors.toList());
    }

    private void enumerate(List<List<BlockCounter>> blockCounters, Stream.Builder<BlockCounter> builder, BlockCounter accumulate, int index) {
        for (BlockCounter blockCounter : blockCounters.get(index)) {
            BlockCounter newBlockCounter = accumulate.addAndReturnNew(blockCounter);
            if (index == lastIndex) {
                builder.accept(newBlockCounter);
            } else {
                enumerate(blockCounters, builder, newBlockCounter, index + 1);
            }
        }
    }
}
