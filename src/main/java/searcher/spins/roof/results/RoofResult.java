package searcher.spins.roof.results;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.results.Result;

import java.util.stream.Stream;

public abstract class RoofResult {
    // ひとつ前のResultを返却
    public abstract Result getLastResult();

    // すでに使われているミノの数を取得する
    public abstract int getNumOfUsingPiece();

    // 足場を置くべきミノの一覧
    // 使用されているすべてのミノではなく、探索範囲に限られたミノだけが含まれる
    public abstract Stream<SimpleOriginalPiece> targetOperationStream();

    // Tミノを取得
    public abstract SimpleOriginalPiece getOperationT();

    // Tミノを除いたフィールド
    public abstract Field getAllMergedFieldWithoutT();

    // 指定されたブロックを置くことができないところ
    // Mask + Tミノ
    public abstract Field getNotAllowedWithT();

    // 使用されているミノをKeyに変換
    public abstract Stream<Long> toKeyStream();
}
