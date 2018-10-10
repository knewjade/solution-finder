package entry.setup;

import common.datastore.BlockField;
import entry.setup.filters.SetupResult;

import java.util.List;
import java.util.Map;

public class SetupResults {
    private final Map<BlockField, List<SetupResult>> resultMap;

    public SetupResults(Map<BlockField, List<SetupResult>> resultMap) {
        this.resultMap = resultMap;
    }

    public Map<BlockField, List<SetupResult>> getResultMap() {
        return resultMap;
    }
}
