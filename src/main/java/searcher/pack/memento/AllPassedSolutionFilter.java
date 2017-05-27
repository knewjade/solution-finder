package searcher.pack.memento;

import searcher.pack.MinoField;

public class AllPassedSolutionFilter implements SolutionFilter {
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
