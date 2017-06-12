package searcher.pack.memento;

public class MinoFieldMementoFactory {
    public static MinoFieldMemento create() {
        return new EmptyMinoFieldMemento();
    }
}
