package searcher.spins.roof;

import common.datastore.PieceCounter;
import common.parser.OperationTransform;
import concurrent.RotateReachableThreadLocal;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.pieces.MinimalSimpleOriginalPieces;
import searcher.spins.pieces.SimpleOriginalPieceFactory;
import searcher.spins.results.AddLastsResult;
import searcher.spins.results.EmptyResult;
import searcher.spins.results.Result;
import searcher.spins.roof.results.RoofResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class RoofRunnerTest {
    @Test
    void case1() {
        int fieldHeight = 8;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        Field initField = FieldFactory.createField("" +
                        "XX_XXXX_XX" +
                        "XX_XXX__XX" +
                        "XX__XXX_XX"
                , fieldHeight);
        MinimalSimpleOriginalPieces minimalPieces = factory.createMinimalPieces(initField);
        Roofs roofs = new Roofs(minimalPieces);
//        ILockedReachableThreadLocal lockedReachableThreadLocal = new ILockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, fieldHeight);
        RotateReachableThreadLocal rotateReachableThreadLocal = new RotateReachableThreadLocal(minoFactory, minoShifter, minoRotation, fieldHeight);
        RoofRunner runner = new RoofRunner(roofs, rotateReachableThreadLocal, Integer.MAX_VALUE, fieldHeight);

        EmptyResult emptyResult = new EmptyResult(initField, new PieceCounter(Arrays.asList(
                Piece.L, Piece.T,
                Piece.S, Piece.Z
        )), fieldHeight);
        SimpleOriginalPiece tOperation = to(Piece.T, Rotate.Left, 7, 1, fieldHeight);
        List<SimpleOriginalPiece> operations = Arrays.asList(
                to(Piece.L, Rotate.Right, 2, 1, fieldHeight),
                tOperation
        );

        Result result = AddLastsResult.create(emptyResult, operations);

        System.out.println(BlockFieldView.toString(result.parseToBlockField()));

        Field notAllowed = FieldFactory.createField(fieldHeight);

        List<RoofResult> results = runner.search(new CandidateWithMask(result, tOperation, notAllowed)).collect(Collectors.toList());
        System.out.println(results.size());

        for (RoofResult roofResult : results) {
            System.out.println(BlockFieldView.toString(roofResult.getLastResult().parseToBlockField()));
            System.out.println();
        }

//        runner.search(new CandidateWithMask(result,tOperation, ))
    }

    private SimpleOriginalPiece to(Piece piece, Rotate rotate, int x, int y, int fieldHeight) {
        return new SimpleOriginalPiece(
                OperationTransform.toFullOperationWithKey(new Mino(piece, rotate), x, y, 0L, fieldHeight), fieldHeight
        );
    }
}