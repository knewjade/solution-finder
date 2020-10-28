package entry.cover;

import common.datastore.MinoOperationWithKey;
import common.tetfu.Tetfu;
import core.field.Field;

import java.util.Collections;
import java.util.List;

public class CoverParameter {
    private final Field field;
    private final List<MinoOperationWithKey> operationList;
    private final String label;
    private final String url;

    CoverParameter(
            Field field, List<MinoOperationWithKey> operationList, String input, boolean isMirror
    ) {
        assert Tetfu.isDataLater115(input);

        this.field = field;
        this.operationList = operationList;

        if (isMirror) {
            this.label = String.format("%s#mirror", input);
            this.url = String.format("http://fumen.zui.jp/?%s (mirror)", input);
        } else {
            this.label = input;
            this.url = String.format("http://fumen.zui.jp/?%s", input);
        }
    }

    Field getField() {
        return field;
    }

    List<MinoOperationWithKey> getOperationList() {
        return Collections.unmodifiableList(operationList);
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }
}
