package searcher.spins.wall;

import common.buildup.BuildUp;
import common.datastore.PieceCounter;
import common.parser.OperationTransform;
import concurrent.LockedReachableThreadLocal;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.spins.SpinCommons;
import searcher.spins.candidates.Candidate;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.pieces.MinimalSimpleOriginalPieces;
import searcher.spins.pieces.Scaffolds;
import searcher.spins.pieces.SimpleOriginalPieceFactory;
import searcher.spins.pieces.bits.BitBlocks;
import searcher.spins.results.AddLastsResult;
import searcher.spins.results.EmptyResult;
import searcher.spins.results.Result;
import searcher.spins.scaffold.ScaffoldRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class WallRunnerTest {
    @Test
    void case1() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField(fieldHeight);
        WallRunner runner = createWallRunner(initField, fieldHeight);

        EmptyResult emptyResult = new EmptyResult(initField, new PieceCounter(Piece.valueList()), fieldHeight);
        SimpleOriginalPiece tOperation = to(Piece.T, Rotate.Reverse, 2, 1, fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.I, Rotate.Spawn, 6, 0, fieldHeight),
                to(Piece.L, Rotate.Right, 0, 1, fieldHeight),
                to(Piece.J, Rotate.Left, 4, 1, fieldHeight),
                to(Piece.S, Rotate.Right, 8, 1, fieldHeight),
                tOperation
        );

        Result result = AddLastsResult.create(emptyResult, operations);
        List<CandidateWithMask> results = runner.search(new Candidate(result, tOperation)).collect(Collectors.toList());

        assertThat(results).hasSize(2);

        verify(results, initField, fieldHeight);
    }

    @Test
    void case2() {
        // 壁を使ったT-Spin Miniがすでにできている
        int fieldHeight = 8;
        Field initField = FieldFactory.createField(fieldHeight);
        WallRunner runner = createWallRunner(initField, fieldHeight);

        PieceCounter reminderPieceCounter = new PieceCounter(Arrays.asList(
                Piece.Z, Piece.L, Piece.L, Piece.I, Piece.O, Piece.O, Piece.T
        ));
        EmptyResult emptyResult = new EmptyResult(initField, reminderPieceCounter, fieldHeight);
        SimpleOriginalPiece tOperation = to(Piece.T, Rotate.Right, 0, 1, KeyOperators.getBitKey(1), fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.Z, Rotate.Spawn, 1, 0, fieldHeight),
                to(Piece.L, Rotate.Spawn, 7, 1, fieldHeight),
                to(Piece.L, Rotate.Spawn, 8, 0, fieldHeight),
                to(Piece.I, Rotate.Spawn, 3, 1, fieldHeight),
                to(Piece.O, Rotate.Spawn, 3, 0, KeyOperators.getBitKey(1), fieldHeight),
                to(Piece.O, Rotate.Spawn, 5, 0, KeyOperators.getBitKey(1), fieldHeight),
                tOperation
        );
        Result result = AddLastsResult.create(emptyResult, operations);

        List<CandidateWithMask> results = runner.search(new Candidate(result, tOperation)).collect(Collectors.toList());

        assertThat(results).hasSize(1);

        verify(results, initField, fieldHeight);
    }

    @Test
    void case3() {
        // 床を使ったT-Spin Miniがすでにできている
        int fieldHeight = 8;
        Field initField = FieldFactory.createField(fieldHeight);
        WallRunner runner = createWallRunner(initField, fieldHeight);

        PieceCounter reminderPieceCounter = new PieceCounter(Arrays.asList(
                Piece.Z, Piece.L, Piece.L, Piece.I, Piece.Z, Piece.O, Piece.J, Piece.T
        ));
        EmptyResult emptyResult = new EmptyResult(initField, reminderPieceCounter, fieldHeight);
        SimpleOriginalPiece tOperation = to(Piece.T, Rotate.Spawn, 4, 0, KeyOperators.getBitKey(1), fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.Z, Rotate.Spawn, 1, 0, fieldHeight),
                to(Piece.L, Rotate.Spawn, 7, 1, fieldHeight),
                to(Piece.L, Rotate.Spawn, 8, 0, fieldHeight),
                to(Piece.I, Rotate.Spawn, 3, 1, fieldHeight),
                to(Piece.Z, Rotate.Left, 1, 1, KeyOperators.getBitKey(1), fieldHeight),
                to(Piece.O, Rotate.Spawn, 2, 2, fieldHeight),
                to(Piece.J, Rotate.Right, 6, 1, KeyOperators.getBitKey(1), fieldHeight),
                tOperation
        );
        Result result = AddLastsResult.create(emptyResult, operations);

        List<CandidateWithMask> results = runner.search(new Candidate(result, tOperation)).collect(Collectors.toList());

        assertThat(results).hasSize(1);

        verify(results, initField, fieldHeight);
    }

    @Test
    void case4() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField(fieldHeight);
        WallRunner runner = createWallRunner(initField, fieldHeight);

        PieceCounter reminderPieceCounter = new PieceCounter(Arrays.asList(
                Piece.Z, Piece.L, Piece.L, Piece.I, Piece.I, Piece.L, Piece.T, Piece.Z, Piece.S
        ));
        EmptyResult emptyResult = new EmptyResult(initField, reminderPieceCounter, fieldHeight);
        SimpleOriginalPiece tOperation = to(Piece.T, Rotate.Reverse, 3, 1, KeyOperators.getBitKey(1), fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.Z, Rotate.Spawn, 1, 0, fieldHeight),
                to(Piece.L, Rotate.Spawn, 7, 1, fieldHeight),
                to(Piece.L, Rotate.Spawn, 8, 0, fieldHeight),
                to(Piece.I, Rotate.Spawn, 3, 1, fieldHeight),
                to(Piece.I, Rotate.Left, 0, 1, KeyOperators.getBitKey(1), fieldHeight),
                to(Piece.L, Rotate.Spawn, 5, 0, KeyOperators.getBitKey(1), fieldHeight),
                tOperation
        );
        Result result = AddLastsResult.create(emptyResult, operations);

        List<CandidateWithMask> results = runner.search(new Candidate(result, tOperation)).collect(Collectors.toList());

        assertThat(results).hasSize(4);

        verify(results, initField, fieldHeight);
    }

    @Test
    @LongTest
    void case5() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField(fieldHeight);
        WallRunner runner = createWallRunner(initField, fieldHeight);

        PieceCounter reminderPieceCounter = new PieceCounter(Arrays.asList(
                Piece.Z, Piece.S, Piece.I, Piece.J, Piece.I, Piece.I, Piece.O, Piece.J, Piece.O, Piece.T,
                Piece.J, Piece.L, Piece.S, Piece.Z
        ));
        EmptyResult emptyResult = new EmptyResult(initField, reminderPieceCounter, fieldHeight);
        SimpleOriginalPiece tOperation = to(Piece.T, Rotate.Reverse, 4, 4, KeyOperators.getBitKey(4), fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.Z, Rotate.Spawn, 1, 3, fieldHeight),
                to(Piece.S, Rotate.Spawn, 8, 3, fieldHeight),
                to(Piece.I, Rotate.Spawn, 3, 4, fieldHeight),
                to(Piece.J, Rotate.Right, 6, 3, fieldHeight),
                to(Piece.I, Rotate.Left, 0, 1, fieldHeight),
                to(Piece.I, Rotate.Left, 9, 1, fieldHeight),
                to(Piece.O, Rotate.Spawn, 8, 5, fieldHeight),
                to(Piece.J, Rotate.Spawn, 1, 5, fieldHeight),
                to(Piece.O, Rotate.Right, 6, 6, fieldHeight),
                tOperation
        );
        Result result = AddLastsResult.create(emptyResult, operations);

        List<CandidateWithMask> results = runner.search(new Candidate(result, tOperation)).collect(Collectors.toList());

        assertThat(results).hasSize(119);

        verify(results, initField, fieldHeight);
    }

    private void verify(List<CandidateWithMask> results, Field initField, int height) {
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(height);

        assertThat(results)
                .allSatisfy(candidateWithMask -> {
                    SimpleOriginalPiece operationT = candidateWithMask.getOperationT();
                    Result result = candidateWithMask.getResult();
                    List<SimpleOriginalPiece> operationsWithoutT = result.operationStream()
                            .filter(op -> !op.equals(operationT))
                            .collect(Collectors.toList());

                    LockedReachable lockedReachable = lockedReachableThreadLocal.get();

                    // Tミノ以外で組み立てられる手順が存在する
                    boolean exists = BuildUp.existsValidBuildPattern(initField, operationsWithoutT, height, lockedReachable);
                    assertThat(exists).isTrue();

                    // Tミノ以外でフィールドを組み立てられる
                    Field fieldWithoutT = FieldFactory.createField(height);
                    for (SimpleOriginalPiece originalPiece : operationsWithoutT) {
                        if (fieldWithoutT.canMerge(originalPiece.getMinoField())) {
                            fieldWithoutT.merge(originalPiece.getMinoField());
                        } else {
                            fail("cannot merge");
                        }
                    }

                    // Tミノに必要なラインが削除されている
                    long needDeletedKey = operationT.getNeedDeletedKey();
                    assertThat(fieldWithoutT.getFilledLine() & needDeletedKey).isEqualTo(needDeletedKey);

                    // Tミノを置くことができる
                    fieldWithoutT.deleteLineWithKey(needDeletedKey);
                    assertThat(fieldWithoutT.canPut(operationT.getMino(), operationT.getX(), operationT.getY())).isTrue();

                    // Tスピンできる
                    assertThat(SpinCommons.canTSpin(fieldWithoutT, operationT.getX(), operationT.getY())).isTrue();
                });
    }

    private WallRunner createWallRunner(Field initField, int fieldHeight) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        MinimalSimpleOriginalPieces minimalPieces = factory.createMinimalPieces(initField);
        BitBlocks bitBlocks = BitBlocks.create(minimalPieces);
        ScaffoldRunner scaffoldRunner = new ScaffoldRunner(Scaffolds.create(minimalPieces));
        return WallRunner.create(bitBlocks, scaffoldRunner, fieldHeight);
    }

    private SimpleOriginalPiece to(Piece piece, Rotate rotate, int x, int y, int fieldHeight) {
        return to(piece, rotate, x, y, 0L, fieldHeight);
    }

    private SimpleOriginalPiece to(Piece piece, Rotate rotate, int x, int y, long deletedKey, int fieldHeight) {
        return new SimpleOriginalPiece(
                OperationTransform.toFullOperationWithKey(new Mino(piece, rotate), x, y, deletedKey), fieldHeight
        );
    }
}