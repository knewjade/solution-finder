package searcher.spins.fill.line;

import common.datastore.PieceCounter;
import core.field.Field;

class EmptyLineFillResult implements LineFillResult {
    private final Field initField;
    private final PieceCounter remainPieceCounter;
    private final int remainBlockCount;

    EmptyLineFillResult(Field initField, PieceCounter remainPieceCounter, int remainBlockCount) {
        this.initField = initField;
        this.remainPieceCounter = remainPieceCounter;
        this.remainBlockCount = remainBlockCount;
    }

    @Override
    public int getRemainBlockCount() {
        return remainBlockCount;
    }

    @Override
    public PieceCounter getRemainPieceCounter() {
        return remainPieceCounter;
    }

    @Override
    public Field getUsingField() {
        return initField;
    }
}
