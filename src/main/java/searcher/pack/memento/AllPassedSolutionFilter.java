package searcher.pack.memento;

import searcher.pack.IMinoField;

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
    public boolean testMinoField(IMinoField minoField) {
        return true;
    }
}
