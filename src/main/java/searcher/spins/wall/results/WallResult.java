package searcher.spins.wall.results;

import core.field.Field;
import searcher.spins.results.Result;

import java.util.stream.Stream;

public abstract class WallResult {
    // ひとつ前のResultを返却
    public abstract Result getLastResult();

    // 次にミノをおく場所をすべて訪れた
    public abstract boolean isVisitedAll();

    // 使用されているミノをIndexKeyに変換
    public abstract Stream<Long> toKeyStream();

    // 次にミノをおく場所を表すフィールド
    // `remain` は置く可能性がある場所で、はみ出ても問題ない
    public abstract Field getRemain();

    // おいてはいけないフィールド
    public abstract Field getNotAllowed();
}
