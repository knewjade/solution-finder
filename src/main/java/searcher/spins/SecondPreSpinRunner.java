package searcher.spins;

import common.datastore.PieceCounter;
import concurrent.RotateReachableThreadLocal;
import core.field.Field;
import core.mino.Piece;
import searcher.spins.fill.FillRunner;
import searcher.spins.fill.line.LineFillRunner;
import searcher.spins.fill.line.spot.LinePools;
import searcher.spins.fill.line.spot.MinoDiff;
import searcher.spins.fill.line.spot.PieceBlockCount;
import searcher.spins.pieces.MinimalSimpleOriginalPieces;
import searcher.spins.pieces.Scaffolds;
import searcher.spins.pieces.SimpleOriginalPieceFactory;
import searcher.spins.pieces.SimpleOriginalPieces;
import searcher.spins.pieces.bits.BitBlocks;
import searcher.spins.roof.RoofRunner;
import searcher.spins.roof.Roofs;
import searcher.spins.scaffold.ScaffoldRunner;
import searcher.spins.wall.WallRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SecondPreSpinRunner {
    private final FirstPreSpinRunner firstPreSpinRunner;
    private final Field initField;
    private final PieceCounter pieceCounter;
    private final ScaffoldRunner scaffoldRunner;
    private final FillRunner fillRunner;
    private final WallRunner wallRunner;
    private final RoofRunner roofRunner;

    SecondPreSpinRunner(FirstPreSpinRunner firstPreSpinRunner, Field initField, PieceCounter pieceCounter) {
        this(firstPreSpinRunner, initField, pieceCounter, Integer.MAX_VALUE);
    }

    public SecondPreSpinRunner(
            FirstPreSpinRunner firstPreSpinRunner, Field initField, PieceCounter pieceCounter, int maxRoofNum
    ) {
        this.firstPreSpinRunner = firstPreSpinRunner;
        this.initField = initField;
        this.pieceCounter = pieceCounter;

        LinePools pools = firstPreSpinRunner.getPools();
        SimpleOriginalPieceFactory factory = firstPreSpinRunner.getFactory();
        int maxTargetHeight = firstPreSpinRunner.getMaxTargetHeight();
        SimpleOriginalPieces simpleOriginalPieces = firstPreSpinRunner.getSimpleOriginalPieces();
        int allowFillMinY = firstPreSpinRunner.getAllowFillMinY();
        int allowFillMaxHeight = firstPreSpinRunner.getAllowFillMaxHeight();
        RotateReachableThreadLocal rotateReachableThreadLocal = firstPreSpinRunner.getRotateReachableThreadLocal();

        Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs = pools.getPieceBlockCountToMinoDiffs();
        Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts = pools.getPieceToPieceBlockCounts();

        int fieldHeight = firstPreSpinRunner.getFieldHeight();
        int maxPieceNum = pieceCounter.getBlocks().size();
        LineFillRunner lineFillRunner = new LineFillRunner(pieceBlockCountToMinoDiffs, pieceToPieceBlockCounts, simpleOriginalPieces, maxPieceNum, maxTargetHeight, fieldHeight);

        MinimalSimpleOriginalPieces minimalPieces = factory.createMinimalPieces(initField);
        Scaffolds scaffolds = Scaffolds.create(minimalPieces);
        this.scaffoldRunner = new ScaffoldRunner(scaffolds);
        this.fillRunner = new FillRunner(lineFillRunner, allowFillMinY, allowFillMaxHeight);

        BitBlocks bitBlocks = BitBlocks.create(minimalPieces);
        this.wallRunner = WallRunner.create(bitBlocks, scaffoldRunner, allowFillMaxHeight, fieldHeight);

        Roofs roofs = new Roofs(minimalPieces);

        this.roofRunner = new RoofRunner(roofs, rotateReachableThreadLocal, maxRoofNum, fieldHeight);
    }

    public Field getInitField() {
        return initField;
    }

    public PieceCounter getPieceCounter() {
        return pieceCounter;
    }

    ScaffoldRunner getScaffoldRunner() {
        return scaffoldRunner;
    }

    FillRunner getFillRunner() {
        return fillRunner;
    }

    WallRunner getWallRunner() {
        return wallRunner;
    }

    RoofRunner getRoofRunner() {
        return roofRunner;
    }

    public SimpleOriginalPieceFactory getFactory() {
        return firstPreSpinRunner.getFactory();
    }

    public int getFieldHeight() {
        return firstPreSpinRunner.getFieldHeight();
    }

    RotateReachableThreadLocal getRotateReachableThreadLocal() {
        return firstPreSpinRunner.getRotateReachableThreadLocal();
    }
}
