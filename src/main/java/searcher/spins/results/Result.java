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

    // 設定された初期フィールドのコピー
    public abstract Field getInitField();

    public Field freezeInitField() {
        return getInitField().freeze();
    }

    // すでに使用されたブロックを表すフィールド
    abstract Field getUsingField();

    public Field freezeUsingField() {
        return getUsingField().freeze();
    }

    // initField + usingField
    public abstract Field getAllMergedField();

    public Field freezeAllMergedField() {
        return getAllMergedField().freeze();
    }

    // 利用可能なミノを取得する
    public abstract PieceCounter getRemainderPieceCounter();

    // すべてのミノを取得する
    public abstract Stream<SimpleOriginalPiece> operationStream();

    // 利用したミノの数
    public abstract int getNumOfUsingPiece();

    // 消去されているライン
    public abstract long getAllMergedFilledLine();
}
