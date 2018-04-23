package entry.setup.filters;

import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;

import java.util.List;

public class SetupTemp {
    private final BlockField keyField;
    private final List<MinoOperationWithKey> solution;
    private final List<MinoOperationWithKey> key;

    public SetupTemp(List<MinoOperationWithKey> solution, int maxHeight) {
        this(null, solution, maxHeight);
    }

    public SetupTemp(List<MinoOperationWithKey> keyOperations, List<MinoOperationWithKey> solution, int maxHeight) {
        this.key = keyOperations != null ? keyOperations : solution;
        this.solution = solution;

        {
            BlockField blockField = new BlockField(maxHeight);
            for (MinoOperationWithKey operation : key) {
                Field field = FieldFactory.createField(maxHeight);
                Mino mino = operation.getMino();
                field.put(mino, operation.getX(), operation.getY());
                field.insertWhiteLineWithKey(operation.getNeedDeletedKey());
                blockField.merge(field, mino.getPiece());
            }
            this.keyField = blockField;
        }
    }

    public List<MinoOperationWithKey> getSolution() {
        return solution;
    }

    public BlockField getKeyField() {
        return keyField;
    }
}
