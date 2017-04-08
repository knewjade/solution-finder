package misc.iterable;

import java.util.*;

/**
 * original by Apache Commons Collections 4.1 API
 */
public class CombinationIterable<T> implements Iterable<List<T>> {
    private static class CombinationIterator<T> implements Iterator<List<T>> {
        private final int[] keys;  // Permutation check done on theses keys to handle equal objects.
        private final List<? extends T> objectMap; // Mapping between keys and objects.
        private final boolean isReverse;

        private List<T> nextCombination;

        public CombinationIterator(Collection<? extends T> coll, int popCount) {
            this(coll, popCount < coll.size() - popCount ? popCount : coll.size() - popCount, coll.size() - popCount < popCount);
        }

        private CombinationIterator(Collection<? extends T> coll, int popCount, boolean isReverse) {
            this.objectMap = new ArrayList<>(coll);
            this.isReverse = isReverse;

            int[] keys = new int[popCount];
            for (int index = 0; index < keys.length; index++)
                keys[index] = index;
            this.keys = keys;

            if (popCount == 0)
                this.nextCombination = new ArrayList<>(coll);
            else if (isReverse)
                this.nextCombination = getReverseList(keys);
            else
                this.nextCombination = getList(keys);
        }

        public boolean hasNext() {
            return nextCombination != null;
        }

        public List<T> next() {
            if (!hasNext())
                throw new NoSuchElementException();

            int size = objectMap.size();
            int max = keys.length;
            int index = max - 1;
            while (0 <= index) {
                keys[index] += 1;
                if (size <= keys[index]) {
                    index -= 1;
                    continue;
                }
                if (index < max - 1)
                    keys[index + 1] = keys[index];
                else
                    break;

                index += 1;
            }

            if (keys.length == 0 || size <= keys[0]) {
                List<T> toReturn = nextCombination;
                nextCombination = null;
                return toReturn;
            }

            List<T> result = nextCombination;
            if (isReverse)
                nextCombination = getReverseList(keys);
            else
                nextCombination = getList(keys);
            return result;
        }

        private List<T> getList(int[] keys) {
            List<T> next = new ArrayList<>();
            for (int key : keys)
                next.add(objectMap.get(key));
            return next;
        }

        private List<T> getReverseList(int[] keys) {
            ArrayList<Integer> keysList = new ArrayList<>();
            for (int key : keys)
                keysList.add(key);
            keysList.sort(Integer::compareTo);

            List<T> next = new ArrayList<>(objectMap);
            for (int index = keysList.size() - 1; 0 <= index; index--) {
                int reIndex = keysList.get(index);
                next.remove(reIndex);
            }
            return next;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final Collection<? extends T> coll;
    private final int popCount;

    public CombinationIterable(Collection<? extends T> coll, int popCount) {
        this.coll = coll;
        this.popCount = popCount;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new CombinationIterator<>(coll, popCount);
    }
}
