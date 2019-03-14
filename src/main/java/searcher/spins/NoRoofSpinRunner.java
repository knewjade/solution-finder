package searcher.spins;

import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.candidates.Candidate;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.candidates.SimpleCandidate;
import searcher.spins.fill.FillRunner;
import searcher.spins.fill.results.FillResult;
import searcher.spins.results.EmptyResult;
import searcher.spins.results.Result;
import searcher.spins.scaffold.ScaffoldRunner;
import searcher.spins.wall.WallRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoRoofSpinRunner implements SpinRunner {
    @Override
    public Stream<CandidateWithMask> search(SecondPreSpinRunner secondPreSpinRunner, int minClearedLine) {
        Field initField = secondPreSpinRunner.getInitField();
        PieceCounter pieceCounter = secondPreSpinRunner.getPieceCounter();
        int fieldHeight = secondPreSpinRunner.getFieldHeight();

        EmptyResult emptyResult = new EmptyResult(initField, pieceCounter, fieldHeight);

        ScaffoldRunner scaffoldRunner = secondPreSpinRunner.getScaffoldRunner();
        FillRunner fillRunner = secondPreSpinRunner.getFillRunner();
        WallRunner wallRunner = secondPreSpinRunner.getWallRunner();

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
                                            Candidate candidate = new SimpleCandidate(scaffoldResult.getLastResult(), tOperation);
                                            return wallRunner.search(candidate);
                                        });
                            });
                });
    }
}