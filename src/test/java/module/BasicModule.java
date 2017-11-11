package module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import concurrent.LockedCandidateThreadLocal;
import concurrent.LockedReachableThreadLocal;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.neighbor.Neighbors;
import core.neighbor.OriginalPieceFactory;
import core.srs.MinoRotation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasicModule extends AbstractModule {
    private final int maxClearLine;

    public BasicModule() {
        this(4);
    }

    public BasicModule(int maxClearLine) {
        this.maxClearLine = maxClearLine;
    }

    @Override
    protected void configure() {
    }

    @Provides
    OriginalPieceFactory provideOriginalPieceFactory() {
        return new OriginalPieceFactory(maxClearLine + 3);
    }

    @Provides
    Neighbors provideNeighbors(
            MinoFactory minoFactory,
            MinoRotation minoRotation,
            OriginalPieceFactory pieceFactory
    ) {
        return new Neighbors(minoFactory, minoRotation, pieceFactory);
    }

    @Provides
    ExecutorService provideExecutorService() {
        int core = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(core);
    }

    @Provides
    LockedCandidateThreadLocal provideLockedCandidateThreadLocal() {
        return new LockedCandidateThreadLocal(maxClearLine);
    }

    @Provides
    LockedReachableThreadLocal provideLockedReachableThreadLocal(
            MinoFactory minoFactory,
            MinoShifter minoShifter,
            MinoRotation minoRotation
    ) {
        return new LockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, maxClearLine);
    }
}
