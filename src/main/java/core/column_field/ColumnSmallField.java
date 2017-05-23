package core.column_field;

import common.comparator.ColumnFieldComparator;

/**
 * heightの値はメモリ節約のためインスタンス内で保持しない (ただしheight <= 10を想定)
 * 必要な場合はインスタンス外で記録しておくこと
 * 幅は最大6
 */
public class ColumnSmallField implements ColumnField {
    private long board = 0L;

    public ColumnSmallField() {
    }

    public ColumnSmallField(long board) {
        this.board = board;
    }

    private ColumnSmallField(ColumnSmallField src) {
        this.board = src.board;
    }

    @Override
    public void setBlock(int x, int y, int height) {
        board |= getYMask(x, y, height);
    }

    private long getYMask(int x, int y, int height) {
        return 1L << y + x * height;
    }

    @Override
    public boolean isEmpty(int x, int y, int height) {
        return (board & getYMask(x, y, height)) == 0L;
    }

    @Override
    public long getBoard(int index) {
        return board;
    }

    @Override
    public int getBoardCount() {
        return 1;
    }

    @Override
    public void merge(ColumnField other) {
        board |= other.getBoard(0);
    }

    @Override
    public void reduce(ColumnField other) {
        board &= ~other.getBoard(0);
    }

    @Override
    public boolean canMerge(ColumnField other) {
        return (board & other.getBoard(0)) == 0L;
    }

    @Override
    public ColumnField freeze(int maxHeight) {
        return new ColumnSmallField(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnSmallField that = (ColumnSmallField) o;
        return board == that.board;
    }

    @Override
    public int hashCode() {
        return (int) (board ^ (board >>> 32));
    }

    @Override
    public int compareTo(ColumnField o) {
        return ColumnFieldComparator.compareColumnField(this, o);
    }
}
