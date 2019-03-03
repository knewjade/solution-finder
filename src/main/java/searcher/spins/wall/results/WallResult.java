package searcher.spins.wall.results;

import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

import java.util.Set;
import java.util.stream.Collectors;
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

    /**
     // Tスピンができているなら `true` を返却
    public abstract boolean canSpin();

    // Tスピンするミノ
    public abstract SimpleOriginalPiece getOperationT();



    // Tを除いたフィールドを返却
    public abstract Field getUsingWithoutT();

    public Field freezeUsingWithoutT() {
        return getUsingWithoutT().freeze();
    }

    // Tを除いた `initField + usingField` を返却
    public abstract Field getUsingAndInitFieldWithoutT();

    public Field freezeUsingAndInitFieldWithoutT() {
        return getUsingWithoutT().freeze();
    }

    // `initField + usingField` を返却
    public abstract Field getUsingAndInitField();

    public Field freezeUsingAndInitField() {
        return getUsingAndInitField().freeze();
    }



    // 消去ライン数を記録する
    public abstract int getClearedLine();



    // 利用可能なミノを取得する
    public abstract PieceCounter getRemainderPieceCounter();


    // おくことができるか
    public abstract boolean canPut(SimpleOriginalPiece piece);


    // 探索中に追加されたミノ
    public abstract Stream<SimpleOriginalPiece> addedOperationStream();
     */
}
