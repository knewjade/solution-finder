package _experimental.allcomb.memento;

public class AllPassedMementoFilter implements MementoFilter {
    @Override
    public boolean test(MinoFieldMemento memento) {
        return true;
    }
}
