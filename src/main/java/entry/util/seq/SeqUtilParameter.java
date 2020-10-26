package entry.util.seq;

import common.datastore.MinoOperationWithKey;
import core.field.Field;

import java.util.Collections;
import java.util.List;

public class SeqUtilParameter {
    private final String input;
    private final String data;
    private final Field field;
    private final List<MinoOperationWithKey> operationList;

    SeqUtilParameter(String input, String data, Field field, List<MinoOperationWithKey> operationList) {
        this.input = input;
        this.data = data;
        this.field = field;
        this.operationList = operationList;
    }

    String getInput() {
        return input;
    }

    String getData() {
        return data;
    }

    Field getField() {
        return field;
    }

    List<MinoOperationWithKey> getOperationList() {
        return Collections.unmodifiableList(operationList);
    }
}
