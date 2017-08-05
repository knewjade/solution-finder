package common.pattern;

import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import common.iterable.PermutationIterable;
import core.mino.Block;
import lib.MyIterables;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class PiecesStreamBuilder {
    private final List<Integer> depths;
    private final List<List<List<Block>>> combinations;
    private final int lastIndex;
    private Stream.Builder<Pieces> builder = null;

    PiecesStreamBuilder(String pattern) {
        int length = pattern.split(",").length;

        // String -> PatternElement
        List<PatternElement> elements = Arrays.stream(pattern.split(","))
                .map(PatternElement::parseWithoutCheck)
                .map(Optional::get)
                .collect(Collectors.toList());
        assert elements.size() == length;

        this.depths = elements.stream()
                .map(PatternElement::getPopCount)
                .collect(Collectors.toList());
        assert depths.size() == length;

        this.combinations = IntStream.range(0, length)
                .mapToObj(index -> {
                    PatternElement element = elements.get(index);
                    Integer popCount = depths.get(index);
                    List<Block> blocks = element.getBlocks();
                    Iterable<List<Block>> iterable = new PermutationIterable<>(blocks, popCount);
                    return MyIterables.toList(iterable);
                })
                .collect(Collectors.toList());
        assert depths.size() == length;

        this.lastIndex = length - 1;
    }

    int getDepths() {
        return depths.stream().mapToInt(Integer::intValue).sum();
    }

    Stream<Pieces> stream() {
        this.builder = Stream.builder();
        if (!combinations.isEmpty())
            enumerate(new LongPieces(), 0);
        return builder.build();
    }

    private void enumerate(Pieces pieces, int index) {
        for (List<Block> combination : combinations.get(index)) {
            Pieces newPieces = pieces.addAndReturnNew(combination);
            if (index == lastIndex) {
                this.builder.accept(newPieces);
            } else {
                enumerate(newPieces, index + 1);
            }
        }
    }
}
