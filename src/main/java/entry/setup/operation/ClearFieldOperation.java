package entry.setup.operation;

import core.field.Field;

public class ClearFieldOperation implements FieldOperation {
    public ClearFieldOperation() {
    }

    @Override
    public void operate(Field field) {
        field.clearLine();
    }
}
