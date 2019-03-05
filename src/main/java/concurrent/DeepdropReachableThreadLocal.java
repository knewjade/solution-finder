package concurrent;

import core.action.reachable.DeepdropReachable;

public class DeepdropReachableThreadLocal extends ThreadLocal<DeepdropReachable> {
    @Override
    protected DeepdropReachable initialValue() {
        return new DeepdropReachable();
    }
}
