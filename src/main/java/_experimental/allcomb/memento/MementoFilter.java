package _experimental.allcomb.memento;

// マルチスレッドに対応していなければならない
public interface MementoFilter {
    // memento が有効な場合は true を返却する
    boolean test(MinoFieldMemento memento);

    boolean testLast(MinoFieldMemento memento);
}
