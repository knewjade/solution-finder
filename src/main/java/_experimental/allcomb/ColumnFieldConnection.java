package _experimental.allcomb;

public class ColumnFieldConnection {
    public static final int WIDTH = 3;
    private final SeparableMino mino;
    private final ColumnField innerField;
    private final ColumnField outerField;

    public ColumnFieldConnection(SeparableMino mino, ColumnField freeze, int height) {
        assert 0 < height && height <= 10;
        int maxBit = height * WIDTH;
        long fillBoard = (1L << maxBit) - 1L;

        long board = freeze.getBoard(0);
        ColumnField innerField = new ColumnSmallField(board & fillBoard);
        ColumnField outerField = new ColumnSmallField(board & ~fillBoard);

        this.mino = mino;
        this.innerField = innerField;
        this.outerField = outerField;
    }

    public SeparableMino getMino() {
        return mino;
    }

    public ColumnField getInnerField() {
        return innerField;
    }

    public ColumnField getOuterField() {
        return outerField;
    }
}
