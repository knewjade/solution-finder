package misc.pattern;

import core.mino.Block;
import misc.iterable.PermutationIterable;
import misc.pieces.SafePieces;

import java.util.*;

class PiecesIterator implements Iterator<SafePieces> {
    private static final Map<String, Block> nameToBlock = new HashMap<>();

    static {
        for (Block block : Block.values())
            nameToBlock.put(block.getName(), block);
    }

    private final List<Iterable<List<Block>>> iterables = new ArrayList<>();
    private final List<Integer> depths = new ArrayList<>();
    private final List<Iterator<List<Block>>> iterators = new ArrayList<>();
    private final ArrayList<Integer> startIndexes = new ArrayList<>();
    private ArrayList<Block> blocks = new ArrayList<>();

    PiecesIterator(String pattern) {
        String[] splits = pattern.split(",");

        for (String split : splits) {
            Optional<PatternElement> optional = PatternElement.parseWithoutCheck(split);

            if (!optional.isPresent())
                throw new IllegalStateException();

            PatternElement element = optional.get();

            List<Block> blocks = element.getBlocks();
            int popCount = element.getPopCount();
            Iterable<List<Block>> iterable = new PermutationIterable<>(blocks, popCount);
            iterables.add(iterable);
            depths.add(popCount);
        }

        for (Iterable<List<Block>> iterable : iterables)
            this.iterators.add(iterable.iterator());

        int lastIndex = 0;
        for (Integer depth : depths) {
            this.startIndexes.add(lastIndex);
            lastIndex += depth;
        }

        // First Blocks
        for (Iterator<List<Block>> iterator : iterators) {
            if (!iterator.hasNext())
                throw new IllegalStateException();
            blocks.addAll(iterator.next());
        }
    }

    private void addAll(List<Block> blocks, int startIndex, int depth, List<Block> adding) {
        for (int count = 0; count < depth; count++) {
            int index = startIndex + count;
            blocks.set(index, adding.get(count));
        }
    }

    @Override
    public boolean hasNext() {
        return blocks != null;
    }

    @Override
    public SafePieces next() {
        if (!hasNext())
            throw new NoSuchElementException();

        SafePieces nextSafePieces = new SafePieces(blocks);

        // 次の要素を探索
        int index = iterators.size() - 1;
        while (0 <= index) {
            Iterator<List<Block>> iterator = iterators.get(index);
            if (!iterator.hasNext()) {
                index -= 1;
                continue;
            }

            // みつかったとき
            addAll(blocks, startIndexes.get(index), depths.get(index), iterators.get(index).next());
            for (int i = index + 1; i < iterators.size(); i++) {
                Iterator<List<Block>> newIterator = iterables.get(i).iterator();
                iterators.set(i, newIterator);
                addAll(blocks, startIndexes.get(i), depths.get(i), newIterator.next());
            }

            return nextSafePieces;
        }

        blocks = null;

        return nextSafePieces;
    }

    int getDepths() {
        assert startIndexes.size() == depths.size();
        int lastIndex = startIndexes.size() - 1;
        return this.startIndexes.get(lastIndex) + depths.get(lastIndex);
    }
}
