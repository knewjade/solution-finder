package searcher.spins.fill;

import common.datastore.PieceCounter;
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
import searcher.spins.fill.line.spot.SpotRunner;
import searcher.spins.fill.results.FillResult;
import searcher.spins.pieces.Scaffolds;
import searcher.spins.pieces.SimpleOriginalPieceFactory;
import searcher.spins.pieces.SimpleOriginalPieces;
import searcher.spins.results.EmptyResult;
import searcher.spins.scaffold.ScaffoldRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FillRunnerTest {
    @Test
    void case1() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        int fieldHeight = 7;

        Field initField = FieldFactory.createField("" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_" +
                        "XXXXXXXXX_"
                , fieldHeight);

        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        ScaffoldRunner scaffoldRunner = new ScaffoldRunner(Scaffolds.create(factory.createMinimalPieces(initField)));

        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());

        LineFillRunner lineFillRunner = createLineFillRunner(pieceCounter.getBlocks().size(), fieldHeight, fieldHeight);

        FillRunner fillRunner = new FillRunner(lineFillRunner, scaffoldRunner, fieldHeight);

        EmptyResult emptyResult = new EmptyResult(initField, pieceCounter, fieldHeight);

        Stream<FillResult> stream = fillRunner.search(emptyResult);
        List<FillResult> results = stream.collect(Collectors.toList());

        assertThat(results).hasSize(17016);
        assertThat(results.stream().filter(FillResult::containsT)).hasSize(13592);
    }

    private LineFillRunner createLineFillRunner(int maxPieceNum, int maxTargetHeight, int fieldHeight) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        LinePools pools = LinePools.create(minoFactory, minoShifter);
        Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs = pools.getPieceBlockCountToMinoDiffs();
        Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts = pools.getPieceToPieceBlockCounts();

        int maxHeightForPiece = SpotRunner.MAX_HEIGHT <= maxTargetHeight ? maxTargetHeight : SpotRunner.MAX_HEIGHT;
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, maxHeightForPiece);
        SimpleOriginalPieces simpleOriginalPieces = SimpleOriginalPieces.create(factory.createAllPieces());

        return new LineFillRunner(pieceBlockCountToMinoDiffs, pieceToPieceBlockCounts, simpleOriginalPieces, maxPieceNum, maxTargetHeight, fieldHeight);
    }
}