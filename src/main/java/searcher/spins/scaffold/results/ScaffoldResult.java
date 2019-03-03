package searcher.spins.scaffold.results;

import common.datastore.PieceCounter;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ScaffoldResult {
    // 利用可能なミノを取得する
    public abstract PieceCounter getReminderPieceCounter();

    // すでに使われているミノの数を取得する
    public abstract int getNumOfUsingPiece();

    // 消去ライン数を記録する
//    public abstract int getClearedLine();

    // おくことができるか
    public abstract boolean canPut(SimpleOriginalPiece piece);

    // 使用されているミノをIndexKeyに変換
    public abstract Stream<Long> toKeyStream();

    public Set<Long> toKeys() {
        return toKeyStream().collect(Collectors.toSet());
    }

    // ひとつ前のResultを返却
    public abstract Result getLastResult();

    // すべてのミノが地面 or 他のミノの上にあるか
    public abstract boolean existsAllOnGround();

    // すべてのミノが地面 or 他のミノの上にあるか
    public abstract List<SimpleOriginalPiece> getAirOperations();

    // 足場を置くべきミノの一覧
    // 使用されているすべてのミノではなく、探索範囲に限られたミノだけが含まれる
    public abstract Stream<SimpleOriginalPiece> targetOperationStream();
}
