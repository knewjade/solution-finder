package searcher.pack.separable_mino;

import common.datastore.OperationWithKey;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import common.parser.AbstractAllOperationFactory;

public class AllSeparableMinoFactory extends AbstractAllOperationFactory<SeparableMino> {
    public AllSeparableMinoFactory(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int fieldHeight, long deleteKeyMask) {
        super(minoFactory, minoShifter, fieldWidth, fieldHeight, deleteKeyMask);
    }

    @Override
    protected SeparableMino parseOperation(OperationWithKey operationWithKey, int upperY, int fieldHeight) {
        return SeparableMino.create(operationWithKey, upperY, fieldHeight);
    }
}
