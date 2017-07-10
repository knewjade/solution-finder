package lib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CombineComparator<T> implements Comparator<T> {
    public static <T> CombineComparator<T> first(Comparator<T> first) {
        return new CombineComparator<>(first);
    }

    private final List<Comparator<T>> comparators = new ArrayList<>();

    private CombineComparator(Comparator<T> first) {
        comparators.add(first);
    }

    public CombineComparator<T> andThen(Comparator<T> next) {
        comparators.add(next);
        return this;
    }

    @Override
    public int compare(T o1, T o2) {
        for (Comparator<T> comparator : comparators) {
            int compare = comparator.compare(o1, o2);
            if (compare != 0)
                return compare;
        }
        return 0;
    }
}
