package common.pattern;

import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.iterable.PermutationIterable;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

class PiecesStreamBuilder {
    private final List<Integer> depths = new ArrayList<>();
    private final List<List<List<Block>>> combinations = new ArrayList<>();
    private final int lastIndex;
    private Stream.Builder<Blocks> builder = null;

    PiecesStreamBuilder(String pattern) {
        String[] splits = pattern.split(",");

        for (String split : splits) {
            Optional<PatternElement> optional = PatternElement.parseWithoutCheck(split);

            assert optional.isPresent();

            PatternElement element = optional.get();

            int popCount = element.getPopCount();
            depths.add(popCount);


            List<Block> blocks = element.getBlocks();
            ArrayList<List<Block>> combination = new ArrayList<>();
            for (List<Block> blockList : new PermutationIterable<>(blocks, popCount))
                combination.add(blockList);

            combinations.add(combination);
        }

        this.lastIndex = combinations.size() - 1;
    }

    int getDepths() {
        return depths.stream().mapToInt(Integer::intValue).sum();
    }

    Stream<Blocks> stream() {
        this.builder = Stream.builder();
        if (!combinations.isEmpty())
            enumerate(new LongBlocks(), 0);
        return builder.build();
    }

    private void enumerate(Blocks blocks, int index) {
        for (List<Block> combination : combinations.get(index)) {
            Blocks newBlocks = blocks.addAndReturnNew(combination);
            if (index == lastIndex) {
                this.builder.accept(newBlocks);
            } else {
                enumerate(newBlocks, index + 1);
            }
        }
    }
}
