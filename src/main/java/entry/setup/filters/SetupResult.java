package entry.setup.filters;

import common.datastore.MinoOperationWithKey;
import core.field.Field;

import java.util.List;

public class SetupResult {
    private final List<MinoOperationWithKey> operationWithKeys;
    private final Field rawField;
    private final Field testField;

    public SetupResult(List<MinoOperationWithKey> operationWithKeys, Field rawField, Field testField) {
        this.operationWithKeys = operationWithKeys;
        this.rawField = rawField;
        this.testField = testField;
    }

    public Field getRawField() {
        return rawField;
    }

    Field getTestField() {
        return testField;
    }

    public List<MinoOperationWithKey> getOperationWithKeys() {
        return operationWithKeys;
    }
}