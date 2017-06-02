package searcher.pack;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import pack.separable_mino.SeparableMino;

public class ColumnFieldConnection {
    private final SeparableMino mino;
    private final ColumnField innerField;
    private final ColumnField outerField;

    public ColumnFieldConnection(SeparableMino mino, ColumnField freeze, SizedBit sizedBit) {
        assert 0 < sizedBit.getHeight() && sizedBit.getHeight() <= 10;
        long fillBoard = sizedBit.getFillBoard();

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
