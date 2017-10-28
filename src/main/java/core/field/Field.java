package core.field;

import core.mino.Mino;
import core.mino.piece.OriginalPiece;

public interface Field extends Comparable<Field> {
    // フィールドの最大高さを返却
    int getMaxFieldHeight();

    // 指定した位置にブロックをおく
    void setBlock(int x, int y);

    // 指定した位置からブロックを取り除く
    void removeBlock(int x, int y);

    // 指定した位置にミノの形にブロックをおく
    void put(Mino mino, int x, int y);

    // 指定した位置にピースの形にブロックをおく
    void put(OriginalPiece piece);

    // 指定した位置にピースをおく
    boolean canPut(OriginalPiece piece);

    // 指定した位置のミノの形でブロックを消す
    void remove(Mino mino, int x, int y);

    // 指定した位置のピースの形でブロックを消す
    void remove(OriginalPiece piece);

    // 指定した位置からミノをharddropしたとき、接着するyを返却
    int getYOnHarddrop(Mino mino, int x, int y);

    // 一番上からharddropで指定した位置を通過するとき true を返却
    boolean canReachOnHarddrop(Mino mino, int x, int y);

    // 一番上からharddropで指定した位置を通過するとき true を返却
    boolean canReachOnHarddrop(OriginalPiece piece);

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
    boolean canPut(Mino mino, int x, int y);

    // 指定した位置のミノが接着できるとき true を返却
    boolean isOnGround(Mino mino, int x, int y);

    // x列上で、maxY行より下にあるブロックの個数を返却 （maxY行上のブロックは対象に含まない）
    int getBlockCountBelowOnX(int x, int maxY);

    // y行上にあるブロックの個数を返却
    int getBlockCountOnY(int y);

    // すべてのブロックの個数を返却
    int getNumOfAllBlocks();

    // ブロックがそろった行を削除し、削除した行数を返却
    int clearLine();

    // ブロックがそろった行を削除し、削除した行を表すマスクを返却
    long clearLineReturnKey();

    // ブロックがそろった行を埋めた状態で復元する
    // deleteKeyは以下のビット位置に、対応する行が揃っているときフラグをたてる
    //       5.******** 最上位
    //       4.********
    //       39********
    //       28********
    //       17********
    // 最下位 06********
    void insertBlackLineWithKey(long deleteKey);

    // ブロックがそろった行を空白の状態で復元する
    void insertWhiteLineWithKey(long deleteKey);

    // 6列分のフィールドを表現するボードの個数を返却
    int getBoardCount();

    // 指定した番号の6列分のフィールドを表現するボードを返却（0が最下層）
    long getBoard(int index);

    // 現在のフィールドのコピーを返却
    Field freeze(int maxHeight);

    // 指定したフィールドのブロックを重ね合せる
    void merge(Field field);

    // 指定したフィールドのブロックを取り除く
    void reduce(Field field);

    // 指定したフィールドのブロックが重ならないときfalseを返却
    boolean canMerge(Field field);

    // フィールド内には必ず4ブロックだけ存在している前提のもと、最も高い位置にあるブロックのY座標を取得
    int getUpperYWith4Blocks();

    // 最も低い位置にあるブロックのY座標を取得
    int getLowerY();

    // フィールドを左に指定したブロック分スライドさせる
    void slideLeft(int slide);

    // フィールドを右に指定したブロック分スライドさせる
    void slideRight(int slide);

    // childの全てのブロックが、フィールド内の同じ位置にブロックがあればtrue
    boolean contains(Field child);

    // ブロックと空白を反転させる
    void inverse();
}
