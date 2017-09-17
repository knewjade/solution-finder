package entry.path.output;

import common.datastore.OperationWithKey;
import core.field.Field;

import java.util.List;

public interface TetfuParser {
    String parse(List<OperationWithKey> operations, Field field, int maxClearLine);
}
