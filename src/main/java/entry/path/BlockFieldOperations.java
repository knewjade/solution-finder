package entry.path;

import core.field.Field;
import core.mino.Block;
import common.datastore.BlockField;
import common.datastore.Operations;

public class BlockFieldOperations implements Comparable<BlockFieldOperations> {
    private final BlockField blockField;
    private final Operations operations;

    BlockFieldOperations(BlockField blockField, Operations operations) {
        this.blockField = blockField;
        this.operations = operations;
    }

    void merge(Field field, Block block) {
        blockField.merge(field, block);
    }

    public BlockField getBlockField() {
        return blockField;
    }

    public Operations getOperations() {
        return operations;
    }

    @Override
    public int compareTo(BlockFieldOperations o) {
        return blockField.compareTo(o.blockField);
    }
}
