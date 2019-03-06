package searcher.spins;

import common.datastore.PieceCounter;
import concurrent.RotateReachableThreadLocal;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.MinoRotation;
import searcher.spins.candidates.Candidate;
import searcher.spins.fill.FillRunner;
import searcher.spins.fill.line.LineFillRunner;
import searcher.spins.fill.line.spot.LinePools;
import searcher.spins.fill.line.spot.MinoDiff;
import searcher.spins.fill.line.spot.PieceBlockCount;
import searcher.spins.fill.results.FillResult;
import searcher.spins.pieces.*;
import searcher.spins.pieces.bits.BitBlocks;
import searcher.spins.results.EmptyResult;
import searcher.spins.results.Result;
import searcher.spins.roof.RoofRunner;
import searcher.spins.roof.Roofs;
import searcher.spins.roof.results.RoofResult;
import searcher.spins.scaffold.ScaffoldRunner;
import searcher.spins.wall.WallRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainRunner {
    private final SimpleOriginalPieceFactory factory;
    private final int maxTargetHeight;
    private final int fieldHeight;
    private final SimpleOriginalPieces simpleOriginalPieces;
    private final LinePools pools;
    private final RotateReachableThreadLocal rotateReachableThreadLocal;

    MainRunner(int maxTargetHeight, int fieldHeight) {
        this.maxTargetHeight = maxTargetHeight;
        this.fieldHeight = fieldHeight;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        this.pools = LinePools.create(minoFactory, minoShifter);

        this.factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, maxTargetHeight);
        AllSimpleOriginalPieces allPieces = factory.createAllPieces();

        this.simpleOriginalPieces = SimpleOriginalPieces.create(allPieces);

        MinoRotation minoRotation = new MinoRotation();
        this.rotateReachableThreadLocal = new RotateReachableThreadLocal(minoFactory, minoShifter, minoRotation, maxTargetHeight);
    }

    public Stream<RoofResult> search(Field initField, PieceCounter pieceCounter, int minClearedLine) {
        Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs = pools.getPieceBlockCountToMinoDiffs();
        Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts = pools.getPieceToPieceBlockCounts();

        int maxPieceNum = pieceCounter.getBlocks().size();
        LineFillRunner lineFillRunner = new LineFillRunner(pieceBlockCountToMinoDiffs, pieceToPieceBlockCounts, simpleOriginalPieces, maxPieceNum, maxTargetHeight, fieldHeight);

        MinimalSimpleOriginalPieces minimalPieces = factory.createMinimalPieces(initField);
        Scaffolds scaffolds = Scaffolds.create(minimalPieces);
        ScaffoldRunner scaffoldRunner = new ScaffoldRunner(scaffolds);
        FillRunner fillRunner = new FillRunner(lineFillRunner, maxTargetHeight);

        BitBlocks bitBlocks = BitBlocks.create(minimalPieces);
        WallRunner wallRunner = WallRunner.create(bitBlocks, scaffoldRunner, maxTargetHeight);

        Roofs roofs = new Roofs(minimalPieces);

        RoofRunner roofRunner = new RoofRunner(roofs, rotateReachableThreadLocal, maxTargetHeight);

        return search(initField, fillRunner, scaffoldRunner, wallRunner, roofRunner, pieceCounter, minClearedLine);
    }

    private Stream<RoofResult> search(
            Field initField, FillRunner fillRunner, ScaffoldRunner scaffoldRunner,
            WallRunner wallRunner, RoofRunner roofRunner, PieceCounter pieceCounter,
            int minClearedLine
    ) {
        EmptyResult emptyResult = new EmptyResult(initField, pieceCounter, fieldHeight);

        return fillRunner.search(emptyResult).parallel()
                .filter(FillResult::containsT)
                .flatMap(fillResult -> {
                    Result lastResult = fillResult.getLastResult();
                    long filledLine = lastResult.getAllMergedFilledLine();

                    return fillResult.tOperationStream()
                            .filter(tOperation -> {
                                long usingKey = tOperation.getUsingKey();
                                return minClearedLine <= Long.bitCount(filledLine & usingKey);
                            })
                            .flatMap(tOperation -> {
                                List<SimpleOriginalPiece> targetOperations = fillResult.operationStream()
                                        .filter(operation -> operation != tOperation)
                                        .collect(Collectors.toList());

                                return scaffoldRunner.build(lastResult, tOperation, targetOperations)
                                        .flatMap(scaffoldResult -> {
                                            Candidate candidate = new Candidate(scaffoldResult.getLastResult(), tOperation);
                                            return wallRunner.search(candidate);
                                        });
                            });
                })
                .flatMap(roofRunner::search);
    }
}