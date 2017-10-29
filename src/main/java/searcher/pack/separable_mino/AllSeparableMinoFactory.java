package searcher.pack.separable_mino;

import common.datastore.MinoOperationWithKey;
import common.parser.AbstractAllOperationFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;

public class AllSeparableMinoFactory extends AbstractAllOperationFactory<SeparableMino> {
    public AllSeparableMinoFactory(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int fieldHeight, long deleteKeyMask) {
        super(minoFactory, minoShifter, fieldWidth, fieldHeight, deleteKeyMask);
    }

    @Override
    protected SeparableMino parseOperation(MinoOperationWithKey operationWithKey, int upperY, int fieldHeight) {
        return SeparableMino.create(operationWithKey, upperY, fieldHeight);
    }
}
