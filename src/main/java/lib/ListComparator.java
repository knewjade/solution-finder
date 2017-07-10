package lib;

import java.util.Comparator;
import java.util.List;

public class ListComparator<T> implements Comparator<List<T>> {
    private final Comparator<T> elementComparator;

    public ListComparator(Comparator<T> elementComparator) {
        this.elementComparator = elementComparator;
    }

    @Override
    public int compare(List<T> o1, List<T> o2) {
        int size = o1.size();
        int compareSize = Integer.compare(size, o2.size());
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < size; index++) {
            int compare = elementComparator.compare(o1.get(index), o2.get(index));
            if (compare != 0)
                return compare;
        }

        return 0;
    }
}
