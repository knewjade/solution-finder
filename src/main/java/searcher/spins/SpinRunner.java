package searcher.spins;

import common.datastore.PieceCounter;
import concurrent.RotateReachableThreadLocal;
import core.action.reachable.RotateReachable;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.MinoRotation;
import searcher.spins.candidates.Candidate;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.candidates.SimpleCandidate;
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
import searcher.spins.scaffold.ScaffoldRunner;
import searcher.spins.wall.WallRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpinRunner {
    private final SimpleOriginalPieceFactory factory;
    private final int allowFillMaxHeight;
    private final int maxTargetHeight;
    private final int fieldHeight;
    private final int allowFillMinY;
    private final SimpleOriginalPieces simpleOriginalPieces;
    private final LinePools pools;
    private final RotateReachableThreadLocal rotateReachableThreadLocal;

    SpinRunner(int allowFillMaxHeight, int fieldHeight) {
        this(new MinoFactory(), new MinoShifter(), allowFillMaxHeight, fieldHeight);
    }

    private SpinRunner(MinoFactory minoFactory, MinoShifter minoShifter, int allowFillMaxHeight, int fieldHeight) {
        this(minoFactory, minoShifter, 0, allowFillMaxHeight, allowFillMaxHeight + 2, fieldHeight);
    }

    public SpinRunner(MinoFactory minoFactory, MinoShifter minoShifter, int allowFillMinY, int allowFillMaxHeight, int maxTargetHeight, int fieldHeight) {
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

    public Stream<? extends Candidate> search(Field initField, PieceCounter pieceCounter, int minClearedLine, boolean skipRoof, int maxRoofNum) {
        Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs = pools.getPieceBlockCountToMinoDiffs();
        Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts = pools.getPieceToPieceBlockCounts();

        int maxPieceNum = pieceCounter.getBlocks().size();
        LineFillRunner lineFillRunner = new LineFillRunner(pieceBlockCountToMinoDiffs, pieceToPieceBlockCounts, simpleOriginalPieces, maxPieceNum, maxTargetHeight, fieldHeight);

        MinimalSimpleOriginalPieces minimalPieces = factory.createMinimalPieces(initField);
        Scaffolds scaffolds = Scaffolds.create(minimalPieces);
        ScaffoldRunner scaffoldRunner = new ScaffoldRunner(scaffolds);
        FillRunner fillRunner = new FillRunner(lineFillRunner, allowFillMinY, allowFillMaxHeight);

        BitBlocks bitBlocks = BitBlocks.create(minimalPieces);
        WallRunner wallRunner = WallRunner.create(bitBlocks, scaffoldRunner, allowFillMaxHeight, fieldHeight);

        Roofs roofs = new Roofs(minimalPieces);

        RoofRunner roofRunner = new RoofRunner(roofs, rotateReachableThreadLocal, maxRoofNum, fieldHeight);

        return search(initField, fillRunner, scaffoldRunner, wallRunner, roofRunner, pieceCounter, minClearedLine, skipRoof);
    }

    private Stream<? extends Candidate> search(
            Field initField, FillRunner fillRunner, ScaffoldRunner scaffoldRunner,
            WallRunner wallRunner, RoofRunner roofRunner, PieceCounter pieceCounter,
            int minClearedLine, boolean skipRoof
    ) {
        EmptyResult emptyResult = new EmptyResult(initField, pieceCounter, fieldHeight);

        Stream<CandidateWithMask> stream = fillRunner.search(emptyResult).parallel()
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
                                            Candidate candidate = new SimpleCandidate(scaffoldResult.getLastResult(), tOperation);
                                            return wallRunner.search(candidate);
                                        });
                            });
                });

        if (skipRoof) {
            return stream;
        }

        return stream
                .flatMap(roofRunner::search)
                .filter(roofResult -> {
                    Result lastResult = roofResult.getLastResult();
                    SimpleOriginalPiece operationT = roofResult.getOperationT();
                    long filledLineByT = operationT.getUsingKey() & lastResult.getAllMergedFilledLine();

                    // Tミノのライン消去に関わらないミノを抽出
                    List<SimpleOriginalPiece> operations = lastResult.operationStream()
                            .filter(operation -> (filledLineByT & operation.getUsingKey()) == 0L)
                            .collect(Collectors.toList());

                    if (operations.isEmpty()) {
                        return true;
                    }

                    RotateReachable rotateReachable = rotateReachableThreadLocal.get();
                    Field allMergedFieldWithoutT = roofResult.getAllMergedFieldWithoutT();
                    long onePieceFilledKeyWithoutT = roofResult.getOnePieceFilledKeyWithoutT();

                    Mino mino = operationT.getMino();
                    int tx = operationT.getX();
                    int ty = operationT.getY();

                    List<SimpleOriginalPiece> allOperations = lastResult.operationStream().collect(Collectors.toList());
                    return operations.stream()
                            .allMatch(operation -> {
                                // operationを抜く
                                Field fieldWithout = allMergedFieldWithoutT.freeze();
                                fieldWithout.reduce(operation.getMinoField());
                                long filledLineWithout = fieldWithout.getFilledLine();
                                long onePieceFilledKeyWithout = onePieceFilledKeyWithoutT & ~operation.getUsingKey();

                                // Tスピンではなくなるとき、必要なミノである
                                if (!SpinCommons.canTSpin(fieldWithout, tx, ty)) {
                                    return true;
                                }

                                // Tスピンできる

                                // 宙に浮くミノがあるとき、必要なミノである
                                boolean existsOnGround = allOperations.stream()
                                        .allMatch(operation2 -> SpinCommons.existsOnGround(initField, fieldWithout, filledLineWithout, onePieceFilledKeyWithout, operation2));

                                if (!existsOnGround) {
                                    return true;
                                }

                                // 宙に浮くミノがない

                                // Tの回転入れに影響を与えるとき、必要なミノである
                                return !rotateReachable.checks(fieldWithout, mino, tx, ty, fieldHeight);
                            });
                })
                .map(roofResult -> new SimpleCandidate(roofResult.getLastResult(), roofResult.getOperationT()))
                ;
    }
}