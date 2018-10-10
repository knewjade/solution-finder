package entry.setup.filters;

import common.datastore.MinoOperationWithKey;
import core.field.Field;

import java.util.List;

public class SetupResult {
    private final List<MinoOperationWithKey> solution;
    private final Field rawField;
    private final Field testField;

    public SetupResult(List<MinoOperationWithKey> solution, Field rawField, Field testField) {
        this.solution = solution;
        this.rawField = rawField;
        this.testField = testField;
    }

    public Field getRawField() {
        return rawField;
    }

    Field getTestField() {
        return testField;
    }

    public List<MinoOperationWithKey> getSolution() {
        return solution;
    }
}