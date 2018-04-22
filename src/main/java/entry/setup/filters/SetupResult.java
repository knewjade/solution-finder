package entry.setup.filters;

import core.field.Field;
import searcher.pack.task.Result;

public class SetupResult {
    private final Result result;
    private final Field rawField;
    private final Field testField;

    public SetupResult(Result result, Field rawField, Field testField) {
        this.result = result;
        this.rawField = rawField;
        this.testField = testField;
    }

    public Result getResult() {
        return result;
    }

    public Field getRawField() {
        return rawField;
    }

    Field getTestField() {
        return testField;
    }
}