package searcher.pack.solutions.serialize;

import common.datastore.OperationWithKey;
import common.datastore.SimpleOperationWithKey;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;

import java.io.Serializable;

class SerializableOperationWithKey implements Serializable {
    private static final long serialVersionUID = 934636440235125L;

    private final Block block;
    private final Rotate rotate;
    private final int x;
    private final int y;
    private final long deletedKey;
    private final long usingKey;

    SerializableOperationWithKey(OperationWithKey operation) {
        Mino mino = operation.getMino();
        this.block = mino.getBlock();
        this.rotate = mino.getRotate();
        this.x = operation.getX();
        this.y = operation.getY();
        this.deletedKey = operation.getNeedDeletedKey();
        this.usingKey = operation.getUsingKey();
    }

    OperationWithKey toOperationWithKey() {
        return new SimpleOperationWithKey(new Mino(block, rotate), x, y, deletedKey, usingKey);
    }
}
