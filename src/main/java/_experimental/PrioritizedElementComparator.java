package _experimental;

import java.util.Comparator;

public class PrioritizedElementComparator implements Comparator<PrioritizedElement> {
    @Override
    public int compare(PrioritizedElement o1, PrioritizedElement o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority());
    }
}
