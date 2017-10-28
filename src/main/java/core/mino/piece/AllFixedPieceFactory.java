package core.mino.piece;

import common.datastore.OperationWithKey;
import common.datastore.SimpleOperationWithKey;
import common.parser.AbstractAllOperationFactory;

public class AllFixedPieceFactory extends AbstractAllOperationFactory<Piece> {
    @Override
    protected Piece parseOperation(OperationWithKey operationWithKey, int upperY, int fieldHeight) {
        new SimpleOperationWithKey();
    }
}
