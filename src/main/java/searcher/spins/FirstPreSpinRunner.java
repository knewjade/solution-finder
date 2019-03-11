package searcher.spins;

import concurrent.RotateReachableThreadLocal;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import searcher.spins.fill.line.spot.LinePools;
import searcher.spins.pieces.AllSimpleOriginalPieces;
import searcher.spins.pieces.SimpleOriginalPieceFactory;
import searcher.spins.pieces.SimpleOriginalPieces;

public class FirstPreSpinRunner {
    private final SimpleOriginalPieceFactory factory;
    private final int allowFillMaxHeight;
    private final int maxTargetHeight;
    private final int fieldHeight;
    private final int allowFillMinY;
    private final SimpleOriginalPieces simpleOriginalPieces;
    private final LinePools pools;
    private final RotateReachableThreadLocal rotateReachableThreadLocal;

    FirstPreSpinRunner(int allowFillMaxHeight, int fieldHeight) {
        this(new MinoFactory(), new MinoShifter(), allowFillMaxHeight, fieldHeight);
    }

    private FirstPreSpinRunner(MinoFactory minoFactory, MinoShifter minoShifter, int allowFillMaxHeight, int fieldHeight) {
        this(minoFactory, minoShifter, 0, allowFillMaxHeight, allowFillMaxHeight + 2, fieldHeight);
    }

    public FirstPreSpinRunner(
            MinoFactory minoFactory, MinoShifter minoShifter,
            int allowFillMinY, int allowFillMaxHeight, int maxTargetHeight, int fieldHeight
    ) {
        assert allowFillMaxHeight + 2 <= maxTargetHeight;
        assert maxTargetHeight <= fieldHeight;

        this.allowFillMaxHeight = allowFillMaxHeight;
        this.maxTargetHeight = maxTargetHeight;
        this.fieldHeight = fieldHeight;
        this.allowFillMinY = allowFillMinY;

        this.pools = LinePools.create(minoFactory, minoShifter);

        this.factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, this.maxTargetHeight);
        AllSimpleOriginalPieces allPieces = factory.createAllPieces();

        this.simpleOriginalPieces = SimpleOriginalPieces.create(allPieces);

        MinoRotation minoRotation = new MinoRotation();
        this.rotateReachableThreadLocal = new RotateReachableThreadLocal(minoFactory, minoShifter, minoRotation, fieldHeight);
    }

    SimpleOriginalPieceFactory getFactory() {
        return factory;
    }

    int getAllowFillMaxHeight() {
        return allowFillMaxHeight;
    }

    int getMaxTargetHeight() {
        return maxTargetHeight;
    }

    int getFieldHeight() {
        return fieldHeight;
    }

    int getAllowFillMinY() {
        return allowFillMinY;
    }

    SimpleOriginalPieces getSimpleOriginalPieces() {
        return simpleOriginalPieces;
    }

    LinePools getPools() {
        return pools;
    }

    RotateReachableThreadLocal getRotateReachableThreadLocal() {
        return rotateReachableThreadLocal;
    }
}
