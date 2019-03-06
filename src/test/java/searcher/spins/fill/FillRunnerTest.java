package searcher.spins.fill;

import common.datastore.PieceCounter;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import org.junit.jupiter.api.Test;
import searcher.spins.fill.line.LineFillRunner;
import searcher.spins.fill.line.spot.LinePools;
import searcher.spins.fill.line.spot.MinoDiff;
import searcher.spins.fill.line.spot.PieceBlockCount;
import searcher.spins.fill.results.FillResult;
import searcher.spins.pieces.SimpleOriginalPieceFactory;
import searcher.spins.pieces.SimpleOriginalPieces;
import searcher.spins.results.EmptyResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FillRunnerTest {
    @Test
    void case1() {
        int allowFillMaxHeight = 5;
        int fieldHeight = 7;

        Field initField = FieldFactory.createField("" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_" +
                        "XXXXXXXXX_"
                , fieldHeight);

        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());

        LineFillRunner lineFillRunner = createLineFillRunner(pieceCounter.getBlocks().size(), allowFillMaxHeight, fieldHeight);

        FillRunner fillRunner = new FillRunner(lineFillRunner, fieldHeight);

        EmptyResult emptyResult = new EmptyResult(initField, pieceCounter, fieldHeight);

        Stream<FillResult> stream = fillRunner.search(emptyResult);
        List<FillResult> results = stream.collect(Collectors.toList());

        assertThat(results).hasSize(25255);
        assertThat(results.stream().filter(FillResult::containsT)).hasSize(20560);
    }

    private LineFillRunner createLineFillRunner(int maxPieceNum, int allowFillMaxHeight, int fieldHeight) {
        int maxTargetHeight = allowFillMaxHeight + 2;

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        LinePools pools = LinePools.create(minoFactory, minoShifter);
        Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs = pools.getPieceBlockCountToMinoDiffs();
        Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts = pools.getPieceToPieceBlockCounts();

        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, maxTargetHeight);
        SimpleOriginalPieces simpleOriginalPieces = SimpleOriginalPieces.create(factory.createAllPieces());

        return new LineFillRunner(pieceBlockCountToMinoDiffs, pieceToPieceBlockCounts, simpleOriginalPieces, maxPieceNum, maxTargetHeight, fieldHeight);
    }

    @Test
    void case2() {
        int allowFillMaxHeight = 4;
        int fieldHeight = 8;

        Field initField = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_" +
                        ""
                , fieldHeight);

        PieceCounter pieceCounter = new PieceCounter(Arrays.asList(Piece.S, Piece.O, Piece.T));

        LineFillRunner lineFillRunner = createLineFillRunner(pieceCounter.getBlocks().size(), allowFillMaxHeight, fieldHeight);

        FillRunner fillRunner = new FillRunner(lineFillRunner, allowFillMaxHeight);

        EmptyResult emptyResult = new EmptyResult(initField, pieceCounter, fieldHeight);

        Stream<FillResult> stream = fillRunner.search(emptyResult);
        List<FillResult> results = stream.collect(Collectors.toList());

        assertThat(results).hasSize(52);
    }
}