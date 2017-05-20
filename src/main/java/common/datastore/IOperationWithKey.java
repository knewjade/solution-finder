package common.datastore;

import core.mino.Mino;

public interface IOperationWithKey {
    Mino getMino();

    int getX();

    int getY();

    long getNeedDeletedKey();

    long getUsingKey();
}
