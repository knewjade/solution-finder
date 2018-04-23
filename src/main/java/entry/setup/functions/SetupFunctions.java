package entry.setup.functions;

import common.datastore.MinoOperationWithKey;
import core.field.Field;
import entry.setup.filters.SetupSolutionFilter;

import java.util.List;
import java.util.function.BiFunction;

public interface SetupFunctions {
    SetupSolutionFilter getSetupSolutionFilter();

    BiFunction<List<MinoOperationWithKey>, Field, String> getNaming();
}
