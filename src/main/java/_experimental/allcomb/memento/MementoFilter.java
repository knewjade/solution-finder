package _experimental.allcomb.memento;

public interface MementoFilter {
    // memento が有効な場合は true を返却する
    boolean test(MinoFieldMemento memento);
}
