package entry.path.output;

import common.datastore.MinoOperationWithKey;
import core.field.Field;

import java.util.List;

public interface FumenParser {
    String parse(List<MinoOperationWithKey> operations, Field field, int maxClearLine);
}
