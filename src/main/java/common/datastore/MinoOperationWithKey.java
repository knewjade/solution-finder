package common.datastore;

import core.mino.Mino;

public interface MinoOperationWithKey extends OperationWithKey {
    Mino getMino();
}
