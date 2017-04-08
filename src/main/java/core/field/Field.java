package core.field;

import core.mino.Mino;

public interface Field {
    // フィールドの最大高さを返却
    int getMaxFieldHeight();

    // 指定した位置にブロックをおく
    void setBlock(int x, int y);

    // 指定した位置にミノをおく
    void putMino(Mino mino, int x, int y);

    // 指定した位置のブロックを消す
    void removeMino(Mino mino, int x, int y);

    // 指定した位置からミノをharddropしたとき、接着するyを返却
    int getYOnHarddrop(Mino mino, int x, int y);

    // 一番上からharddropで指定した位置を通過するとき true を返却
    boolean canReachOnHarddrop(Mino mino, int x, int y);

    // 指定した位置にブロックがないとき true を返却
    boolean isEmpty(int x, int y);

    // y行以上にブロックがあるとき true を返却（y行上のブロックも対象に含む）
    boolean existsAbove(int y);

    // フィールド内に1つもブロックがないとき true を返却
    boolean isPerfect();

    // x列上で、maxY行より下がすべてブロックで埋まっているとき true を返却
    boolean isFilledInColumn(int x, int maxY);

    // x列とその左の列の間が壁（隙間がない）とき true を返却。1 <= xであること
    boolean isWallBetweenLeft(int x, int maxY);

    // 指定した位置にミノを置くことができるとき true を返却
    boolean canPutMino(Mino mino, int x, int y);

    // 指定した位置のミノが接着できるとき true を返却
    boolean isOnGround(Mino mino, int x, int y);

    // x列上で、maxY行より下にあるブロックの個数を返却 （maxY行上のブロックは対象に含まない）
    int getBlockCountBelowOnX(int x, int maxY);

    // すべてのブロックの個数を返却
    int getAllBlockCount();

    // ブロックがそろった行を削除し、削除した行数を返却
    int clearLine();

    // ブロックがそろった行を削除し、削除した行を表すマスクを返却
    int clearLineReturnIndex();

    // 6列分のフィールドを表現するボードの個数を返却
    int getBoardCount();

    // 指定した番号の6列分のフィールドを表現するボードを返却（0が最下層）
    long getBoard(int index);

    // 現在のフィールドのコピーを返却
    Field freeze(int maxHeight);
}
