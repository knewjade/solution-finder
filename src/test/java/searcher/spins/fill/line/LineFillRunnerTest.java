package searcher.spins.fill.line;

import common.datastore.BlockField;
import common.datastore.PieceCounter;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.KeyOperators;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import org.junit.jupiter.api.Test;
import searcher.spins.fill.line.spot.LinePools;
import searcher.spins.fill.line.spot.MinoDiff;
import searcher.spins.fill.line.spot.PieceBlockCount;
import searcher.spins.fill.line.spot.SpotRunner;
import searcher.spins.results.Result;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class LineFillRunnerTest {
    @Test
    void case1() {
        int maxHeight = 5;
        Field initField = FieldFactory.createField("" +
                        "_____XXXXX" +
                        "_____XXXXX" +
                        "_____XXXXX" +
                        "_____XXXXX"
                , maxHeight);

        PieceCounter pieceCounter = new PieceCounter(Stream.of(Piece.I, Piece.O, Piece.T));

        LineFillRunner lineFillRunner = createLineFillRunner(initField, pieceCounter.getBlocks().size(), maxHeight, maxHeight);

        Stream<Result> stream = lineFillRunner.search(initField, pieceCounter, 2);

        System.out.println(stream.count());  // 84

//        List<SpotResult> spots = spotRunner.search(Arrays.asList(
//                new PieceBlockCount(Piece.I, 4),
//                new PieceBlockCount(Piece.O, 2),
//                new PieceBlockCount(Piece.T, 2)
//        ));

//     Stream<LineFillResult> stream = runner.search(pieceCounter, initField, initField.getFilledLine(), 0, 3, 10).parallel();

    }

    @Test
    void case2() {
        int maxHeight = 5;
        Field initField = FieldFactory.createField(maxHeight);

        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());

        LineFillRunner lineFillRunner = createLineFillRunner(initField, pieceCounter.getBlocks().size(), maxHeight, maxHeight);

        Stream<Result> stream = lineFillRunner.search(initField, pieceCounter, 2);
        List<Result> collect = stream.collect(Collectors.toList());

        System.out.println(collect.size());

        for (Result result : collect) {
            long filledLine = result.getAllMergedField().getFilledLine();
            if ((filledLine & KeyOperators.getBitKey(2)) == 0L) {
                System.out.println(FieldView.toString(result.getAllMergedField()));
                System.out.println();
                fail();
            }
        }

        TreeSet<BlockField> blockFields = new TreeSet<>(BlockField::compareTo);
        for (Result result : collect) {
            blockFields.add(result.parseToBlockField());
        }

        assertThat(blockFields).hasSize(collect.size());
    }

    private LineFillRunner createLineFillRunner(Field initField, int maxPieceNum, int maxTargetHeight, int fieldHeight) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        LinePools pools = LinePools.create(minoFactory, minoShifter);
        Map<PieceBlockCount, List<MinoDiff>> pieceBlockCountToMinoDiffs = pools.getPieceBlockCountToMinoDiffs();
        Map<Piece, Set<PieceBlockCount>> pieceToPieceBlockCounts = pools.getPieceToPieceBlockCounts();

        int maxHeightForPiece = SpotRunner.MAX_HEIGHT <= maxTargetHeight ? maxTargetHeight : SpotRunner.MAX_HEIGHT;
        SimpleOriginalPieces simpleOriginalPieces = SimpleOriginalPieces.create(minoFactory, minoShifter, maxHeightForPiece);

        return new LineFillRunner(pieceBlockCountToMinoDiffs, pieceToPieceBlockCounts, simpleOriginalPieces, maxPieceNum, fieldHeight);
    }
}
