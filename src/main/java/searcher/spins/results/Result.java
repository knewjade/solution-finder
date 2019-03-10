package searcher.spins.results;

import common.datastore.BlockField;
import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

import java.util.stream.Stream;

public abstract class Result {
    // BlockField に変換
    public BlockField parseToBlockField() {
        Field field = this.getInitField();
        BlockField blockField = new BlockField(field.getMaxFieldHeight());
        this.operationStream().sequential()
                .forEach(operation -> blockField.merge(operation.getMinoField(), operation.getPiece()));
        return blockField;
    }

    // 設定された初期フィールド
    public abstract Field getInitField();

    // すでに使用されたブロックを表すフィールド
    public abstract Field getUsingField();

    // initField + usingField
    public abstract Field getAllMergedField();

    // 利用可能なミノを取得する
    public abstract PieceCounter getRemainderPieceCounter();

    // すべてのミノを取得する
    public abstract Stream<SimpleOriginalPiece> operationStream();

    // 利用したミノの数
    public abstract int getNumOfUsingPiece();

    // 最終的なフィールドで消去されているライン
    public abstract long getAllMergedFilledLine();

    // ミノが置かれているライン
    public abstract long getUsingKey();

    // 1ミノだけで削除されるライン
    public abstract long getOnePieceFilledKey();
}
