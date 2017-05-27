package searcher.pack.memento;

import searcher.pack.MinoField;

// マルチスレッドに対応していなければならない
public interface MementoFilter {
    // memento が有効な場合は true を返却する
    boolean test(MinoFieldMemento memento);

    boolean testLast(MinoFieldMemento memento);

    boolean testMinoField(MinoField minoField);
}
