package searcher.spins.fill.line;

import common.datastore.Operation;
import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

import java.util.List;

class AddLastLineFillResult implements LineFillResult {
    private final Field usingField;
    private final int remainBlockCount;
    private final PieceCounter remainPieceCounter;

    AddLastLineFillResult(LineFillResult prevResult, List<SimpleOriginalPiece> operations, int usingBlockCount) {
        Field freeze = prevResult.getUsingField().freeze();
        for (SimpleOriginalPiece operation : operations) {
            freeze.merge(operation.getMinoField());
        }
        this.usingField = freeze;

        this.remainBlockCount = prevResult.getRemainBlockCount() - usingBlockCount;
        assert 0 <= remainBlockCount;

        this.remainPieceCounter = prevResult.getRemainPieceCounter().removeAndReturnNew(
                new PieceCounter(operations.stream().map(Operation::getPiece))
        );
    }

    @Override
    public Field getUsingField() {
        return usingField;
    }

    @Override
    public int getRemainBlockCount() {
        return remainBlockCount;
    }

    @Override
    public PieceCounter getRemainPieceCounter() {
        return remainPieceCounter;
    }
}
