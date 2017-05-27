import common.datastore.NumberPieces;
import common.datastore.Pieces;
import common.iterable.PermutationIterable;
import common.pattern.PatternElement;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PiecesStreamBuilder {
    //    private final List<Iterable<List<Block>>> iterables = new ArrayList<>();
    private final List<Integer> depths = new ArrayList<>();
    private final List<List<List<Block>>> combinations = new ArrayList<>();
    private final int lastIndex;
    private Stream.Builder<Pieces> builder = null;

//    private final List<Iterator<List<Block>>> iterators = new ArrayList<>();
//    private final ArrayList<Integer> startIndexes = new ArrayList<>();
//    private ArrayList<Block> blocks = new ArrayList<>();

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

    Stream<Pieces> stream() {
        this.builder = Stream.builder();
        if (!combinations.isEmpty())
            enumerate(new NumberPieces(), 0);
        return builder.build();
    }

    private void enumerate(Pieces pieces, int index) {
        for (List<Block> combination : combinations.get(index)) {
            Pieces newPieces = pieces.add(combination);
            if (index == lastIndex) {
                this.builder.accept(newPieces);
            } else {
                enumerate(newPieces, index + 1);
            }
        }
    }
}
