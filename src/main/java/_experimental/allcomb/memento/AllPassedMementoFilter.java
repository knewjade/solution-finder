package _experimental.allcomb.memento;

import java.util.concurrent.atomic.AtomicInteger;

public class AllPassedMementoFilter implements MementoFilter {
    private AtomicInteger counter = new AtomicInteger();

    @Override
    public boolean test(MinoFieldMemento memento) {
        return true;
    }

    @Override
    public boolean testLast(MinoFieldMemento memento) {
        return true;
    }
}
