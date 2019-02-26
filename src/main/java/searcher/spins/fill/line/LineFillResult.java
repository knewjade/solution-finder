package searcher.spins.fill.line;

import common.datastore.PieceCounter;
import core.field.Field;

public interface LineFillResult {
    int getRemainBlockCount();

    PieceCounter getRemainPieceCounter();

    Field getUsingField();
}
