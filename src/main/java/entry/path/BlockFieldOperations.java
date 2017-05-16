package entry.path;

import common.datastore.BlockField;
import common.datastore.Operations;
import core.field.Field;
import core.mino.Block;

public class BlockFieldOperations implements Comparable<BlockFieldOperations> {
    private final BlockField blockField;
    private final Operations operations;

    BlockFieldOperations(BlockField blockField, Operations operations) {
        assert blockField != null;
        assert operations != null;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockFieldOperations that = (BlockFieldOperations) o;
        return blockField.equals(that.blockField);
    }

    @Override
    public int hashCode() {
        return blockField.hashCode();
    }

    @Override
    public int compareTo(BlockFieldOperations o) {
        return blockField.compareTo(o.blockField);
    }
}
