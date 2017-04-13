package misc.iterable;

import java.util.*;

/**
 * original by Apache Commons Collections 4.1 API
 */
public class PermutationIterable<T> implements Iterable<List<T>> {
    private static class PermutationIterator<E> implements Iterator<List<E>> {
        private final Iterator<? extends List<E>> combination;
        private Iterator<? extends List<E>> permutation;

        PermutationIterator(Collection<E> coll, int popCount) {
            this.combination = new CombinationIterable<>(coll, popCount).iterator();
            if (combination.hasNext())
                this.permutation = new AllPermutationIterable<>(combination.next()).iterator();
            else
                this.permutation = null;
        }

        public boolean hasNext() {
            return combination.hasNext() || permutation.hasNext();
        }

        public List<E> next() {
            if (permutation.hasNext())
                return permutation.next();

            if (!combination.hasNext())
                throw new NoSuchElementException();

            this.permutation = new AllPermutationIterable<>(combination.next()).iterator();
            return next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final Collection<T> coll;
    private final int popCount;

    public PermutationIterable(Collection<T> coll, int popCount) {
        this.coll = coll;
        this.popCount = popCount;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new PermutationIterator<>(coll, popCount);
    }
}
