package misc;

import core.mino.Block;
import misc.iterable.PermutationIterable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PiecesGenerator implements Iterable<SafePieces> {
    private static final Map<String, Block> nameToBlock = new HashMap<>();

    static {
        for (Block block : Block.values())
            nameToBlock.put(block.getName(), block);
    }

    private final String pattern;

    public PiecesGenerator(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Iterator<SafePieces> iterator() {
        return new PiecesGenerator2(pattern);
    }

    public int getDepth() {
        return new PiecesGenerator2(pattern).getDepths();
    }

    private static class PiecesGenerator2 implements Iterator<SafePieces> {
        private final List<Iterable<List<Block>>> iterables = new ArrayList<>();
        private final List<Integer> depths = new ArrayList<>();
        private final List<Iterator<List<Block>>> iterators = new ArrayList<>();
        private final ArrayList<Integer> startIndexes = new ArrayList<>();
        private ArrayList<Block> blocks = new ArrayList<>();

        PiecesGenerator2(String pattern) {
            String trim = pattern.toUpperCase().replaceAll(" ", "");
            String[] split = trim.split(",");

            for (String pat : split) {
                int popCount;
                List<Block> collect;

                if (pat.equals("*")) {
                    popCount = 1;
                    collect = Arrays.asList(Block.values());
                } else if (pat.startsWith("*P")) {
                    popCount = Integer.parseInt(pat.substring(pat.indexOf("P") + 1, pat.length()));
                    collect = Arrays.asList(Block.values());
                } else if (pat.contains("[") && pat.contains("]")) {

                    if (pat.contains("P")) {
                        popCount = Integer.parseInt(pat.substring(pat.indexOf("P") + 1, pat.length()));
                    } else if (pat.startsWith("[") && pat.endsWith("]")) {
                        popCount = 1;
                    } else {
                        throw new IllegalStateException();
                    }

                    String substring = pat.substring(pat.indexOf("[") + 1, pat.indexOf("]"));
                    collect = Stream.of(substring.split("")).map(nameToBlock::get).collect(Collectors.toList());

                } else {
                    if (!nameToBlock.containsKey(pat))
                        throw new IllegalStateException();
                    popCount = 1;
                    collect = Collections.singletonList(nameToBlock.get(pat));
                }

                Iterable<List<Block>> iterable = new PermutationIterable<>(collect, popCount);
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

        private int getDepths() {
            assert startIndexes.size() == depths.size();
            int lastIndex = startIndexes.size() - 1;
            return this.startIndexes.get(lastIndex) + depths.get(lastIndex);
        }
    }
}