package searcher.pack.memento;

import searcher.pack.IMinoField;

// マルチスレッドに対応していなければならない
public interface SolutionFilter {
    // memento が有効な場合は true を返却する
    boolean test(MinoFieldMemento memento);

    boolean testLast(MinoFieldMemento memento);

    boolean testMinoField(IMinoField minoField);
}
