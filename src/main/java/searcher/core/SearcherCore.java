package searcher.core;

import core.action.candidate.Candidate;
import core.mino.Piece;

public interface SearcherCore<T, O> {
    // 通常操作 & ホールド操作
    void stepWithNext(Candidate<T> candidate, Piece drawn, O order, boolean isLast);

    // 通常操作のみ
    void stepWithNextNoHold(Candidate<T> candidate, Piece drawn, O order, boolean isLast);

    // ホールド操作のみ  // ホールドありの最後のケース
    void stepWhenNoNext(Candidate<T> candidate, O order, boolean isLast);
}
