package entry.path;

import common.buildup.BuildUpStream;
import core.action.reachable.Reachable;

public class BuildUpListUpThreadLocal extends ThreadLocal<BuildUpStream> {
    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final int height;

    public BuildUpListUpThreadLocal(ThreadLocal<? extends Reachable> reachableThreadLocal, int height) {
        this.reachableThreadLocal = reachableThreadLocal;
        this.height = height;
    }

    @Override
    protected BuildUpStream initialValue() {
        return new BuildUpStream(reachableThreadLocal.get(), height);
    }
}