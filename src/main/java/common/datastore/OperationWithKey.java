package common.datastore;

import core.mino.Mino;

public interface OperationWithKey extends Operation {
    Mino getMino();

    long getNeedDeletedKey();

    long getUsingKey();
}
