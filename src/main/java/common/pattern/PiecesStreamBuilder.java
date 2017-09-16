package common.pattern;

import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.iterable.CombinationIterable;
import common.iterable.PermutationIterable;
import core.mino.Block;
import lib.MyIterables;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class PiecesStreamBuilder {
    private final List<PatternElement> elements;
    private final int lastIndex;

    PiecesStreamBuilder(String pattern) {
        this.elements = Arrays.stream(pattern.split(","))
                .map(PatternElement::parseWithoutCheck)
                .map(Optional::get)
                .collect(Collectors.toList());

        this.lastIndex = elements.size() - 1;
    }

    PiecesStreamBuilder(List<PatternElement> elements) {
        this.elements = elements;
        this.lastIndex = elements.size() - 1;
    }

    int getDepths() {
        return elements.stream()
                .mapToInt(PatternElement::getPopCount)
                .sum();
    }

    Stream<Blocks> blocksStream() {
        List<List<List<Block>>> combinations = createCombinations();
        Stream.Builder<Blocks> builder = Stream.builder();
        if (!combinations.isEmpty())
            enumerate(combinations, builder, new LongBlocks(), 0);
        return builder.build();
    }

    private List<List<List<Block>>> createCombinations() {
        return elements.stream()
                .map(element -> {
                    int popCount = element.getPopCount();
                    List<Block> blocks = element.getBlocks();
                    Iterable<List<Block>> iterable = new PermutationIterable<>(blocks, popCount);
                    return MyIterables.toList(iterable);
                })
                .collect(Collectors.toList());
    }

    private void enumerate(List<List<List<Block>>> combinations, Stream.Builder<Blocks> builder, Blocks blocks, int index) {
        for (List<Block> combination : combinations.get(index)) {
            Blocks newBlocks = blocks.addAndReturnNew(combination);
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
                .map(element -> {
                    int popCount = element.getPopCount();
                    List<Block> blocks = element.getBlocks();
                    Iterable<List<Block>> iterable = new CombinationIterable<>(blocks, popCount);
                    return StreamSupport.stream(iterable.spliterator(), false)
                            .map(BlockCounter::new)
                            .collect(Collectors.toList());
                })
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
