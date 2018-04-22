package entry.setup.operation;

import core.field.Field;

public class SetBlockFieldOperation implements FieldOperation {
    private final int x;
    private final int y;

    public SetBlockFieldOperation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void operate(Field field) {
        field.setBlock(x, y);
    }
}
