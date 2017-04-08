package misc.iterable;

import java.util.*;

/**
 * original by Apache Commons Collections 4.1 API
 */
public class PermutationIterable<T> implements Iterable<List<T>> {
    private static class PermutationIterator<E> implements Iterator<List<E>> {
        private final int[] keys;  // Permutation check done on theses keys to handle equal objects.
        private final List<? extends E> objectMap; // Mapping between keys and objects.
        private final boolean[] direction;  // Direction table  // false=left, true=right

        private List<E> nextPermutation;

        PermutationIterator(Collection<? extends E> coll) {
            int[] keys = new int[coll.size()];
            for (int index = 0; index < keys.length; index++)
                keys[index] = index;
            this.keys = keys;

            this.direction = new boolean[coll.size()];
            this.objectMap = new ArrayList<>(coll);
            this.nextPermutation = new ArrayList<>(coll);
        }

        public boolean hasNext() {
            return nextPermutation != null;
        }

        public List<E> next() {
            if (!hasNext())
                throw new NoSuchElementException();

            // find the largest mobile integer k
            int indexOfLargestMobileInteger = -1;
            int largestKey = -1;
            for (int i = 0; i < keys.length; i++) {
                if ((direction[i] && i < keys.length - 1 && keys[i] > keys[i + 1]) ||
                        (!direction[i] && i > 0 && keys[i] > keys[i - 1])) {
                    if (keys[i] > largestKey) {
                        largestKey = keys[i];
                        indexOfLargestMobileInteger = i;
                    }
                }
            }

            if (largestKey == -1) {
                List<E> toReturn = nextPermutation;
                nextPermutation = null;
                return toReturn;
            }

            // swap k and the adjacent integer it check looking at
            int offset = direction[indexOfLargestMobileInteger] ? 1 : -1;

            int tmpKey = keys[indexOfLargestMobileInteger];
            keys[indexOfLargestMobileInteger] = keys[indexOfLargestMobileInteger + offset];
            keys[indexOfLargestMobileInteger + offset] = tmpKey;

            boolean tmpDirection = direction[indexOfLargestMobileInteger];
            direction[indexOfLargestMobileInteger] = direction[indexOfLargestMobileInteger + offset];
            direction[indexOfLargestMobileInteger + offset] = tmpDirection;

            // reverse the direction of checkmate integers larger than k and build the result
            List<E> next = new ArrayList<>();
            for (int index = 0; index < keys.length; index++) {
                if (largestKey < keys[index])
                    direction[index] = !direction[index];
                next.add(objectMap.get(keys[index]));
            }

            List<E> result = nextPermutation;
            nextPermutation = next;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final Collection<? extends T> coll;

    public PermutationIterable(Collection<? extends T> coll) {
        this.coll = coll;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new PermutationIterator<>(coll);
    }
}
