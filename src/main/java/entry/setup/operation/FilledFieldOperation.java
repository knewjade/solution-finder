package entry.setup.operation;

import core.field.Field;

public class FilledFieldOperation implements FieldOperation {
    private final int y;

    public FilledFieldOperation(int y) {
        this.y = y;
    }

    @Override
    public void operate(Field field) {
        field.fillLine(y);
    }
}
