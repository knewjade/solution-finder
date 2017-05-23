package _experimental.allcomb.memento;

import _experimental.allcomb.MinoField;

public class AllPassedMementoFilter implements MementoFilter {
    @Override
    public boolean test(MinoFieldMemento memento) {
        return true;
    }

    @Override
    public boolean testLast(MinoFieldMemento memento) {
        return true;
    }

    @Override
    public boolean testMinoField(MinoField minoField) {
        return true;
    }
}
