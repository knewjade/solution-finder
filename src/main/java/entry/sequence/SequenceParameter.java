package entry.sequence;

import common.datastore.MinoOperationWithKey;
import core.field.Field;

import java.util.Collections;
import java.util.List;

public class SequenceParameter {
    private final String input;
    private final String data;
    private final Field field;
    private final List<MinoOperationWithKey> operationList;
    private final int start;
    private final int end;

    SequenceParameter(
            String input, String data, Field field, List<MinoOperationWithKey> operationList, int start, int end
    ) {
        this.input = input;
        this.data = data;
        this.field = field;
        this.operationList = operationList;
        this.start = start;
        this.end = end;
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

    int getStart() {
        return start;
    }

    int getEnd() {
        return end;
    }
}
