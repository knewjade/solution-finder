package searcher.spins;

import common.datastore.FullOperationWithKey;
import common.parser.AbstractAllOperationFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.neighbor.SimpleOriginalPiece;

public class AllSimpleOriginalPieces extends AbstractAllOperationFactory<SimpleOriginalPiece> {
    public AllSimpleOriginalPieces(MinoFactory minoFactory, MinoShifter minoShifter, int fieldHeight) {
        this(minoFactory, minoShifter, 10, fieldHeight);
    }

    public AllSimpleOriginalPieces(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int fieldHeight) {
        super(minoFactory, minoShifter, fieldWidth, fieldHeight);
    }

    @Override
    protected SimpleOriginalPiece parseOperation(FullOperationWithKey operationWithKey, int upperY, int fieldHeight) {
        return new SimpleOriginalPiece(operationWithKey, fieldHeight);
    }
}
