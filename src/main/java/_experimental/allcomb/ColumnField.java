package _experimental.allcomb;

public interface ColumnField extends Comparable<ColumnField> {
    // 指定した位置にブロックをおく
    void setBlock(int x, int y, int height);

    // 指定した位置にブロックがないとき true を返却
    boolean isEmpty(int x, int y, int height);

    // 指定した番号の6列分のフィールドを表現するボードを返却（0が最下層）
    long getBoard(int index);

    // 6列分のフィールドを表現するボードの個数を返却
    int getBoardCount();

    // 指定したフィールドのブロックを重ね合せる
    void merge(ColumnField field);

    // 指定したフィールドのブロックを取り除く
    void reduce(ColumnField field);

    // 指定したフィールドのブロックが重ならないときfalseを返却
    boolean canMerge(ColumnField field);

    // 現在のフィールドのコピーを返却
    ColumnField freeze(int maxHeight);
}
