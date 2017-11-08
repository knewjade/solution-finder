package module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import core.action.candidate.LockedCandidate;
import core.action.candidate.LockedNeighborCandidate;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.neighbor.Neighbors;
import core.neighbor.OriginalPieceFactory;
import core.srs.MinoRotation;

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
    Neighbors provideNeighbors(MinoFactory minoFactory, MinoRotation minoRotation, OriginalPieceFactory pieceFactory) {
        return new Neighbors(minoFactory, minoRotation, pieceFactory);
    }
}
