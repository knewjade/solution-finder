package searcher.spins.fill.line;

import common.datastore.BlockField;
import common.datastore.PieceCounter;
import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.spins.fill.line.spot.LinePools;
import searcher.spins.fill.line.spot.MinoDiff;
import searcher.spins.fill.line.spot.PieceBlockCount;
import searcher.spins.fill.line.spot.SpotRunner;
import searcher.spins.pieces.SimpleOriginalPieceFactory;
import searcher.spins.pieces.SimpleOriginalPieces;
import searcher.spins.results.Result;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class LineFillRunnerTest {
    @Test
    void case1() {
        int targetY = 2;
        int maxTargetHeight = 5;
        Field initField = FieldFactory.createField("" +
                        "_____XXXXX" +
                        "_____XXXXX" +
                        "_____XXXXX" +
                        "_____XXXXX"
                , maxTargetHeight);

        PieceCounter pieceCounter = new PieceCounter(Stream.of(Piece.I, Piece.O, Piece.T));

        LineFillRunner lineFillRunner = createLineFillRunner(pieceCounter.getBlocks().size(), maxTargetHeight, maxTargetHeight);

        List<Result> results = lineFillRunner.search(initField, pieceCounter, targetY, initField.getFilledLine())
                .parallel()
                .collect(Collectors.toList());

        assertThat(results).hasSize(60);

        verifyDuplicate(results);

        verifyFill(results, targetY, initField.getFilledLine(), maxTargetHeight);
    }

    @Test
    void case2() {
        int targetY = 3;
        int maxTargetHeight = 6;
        Field initField = FieldFactory.createField("" +
                        "X______X__" +
                        "XX________" +
                        "XXX_______" +
                        "XXXX______"
                , maxTargetHeight);

        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());

        LineFillRunner lineFillRunner = createLineFillRunner(pieceCounter.getBlocks().size(), maxTargetHeight, maxTargetHeight);

        List<Result> results = lineFillRunner.search(initField, pieceCounter, targetY, initField.getFilledLine())
                .parallel()
                .collect(Collectors.toList());

        assertThat(results).hasSize(725068);

        verifyDuplicate(results);

        verifyFill(results, targetY, initField.getFilledLine(), maxTargetHeight);
    }

    @Test
    void case3() {
        int targetY = 2;
        int maxTargetHeight = 6;
        Field initField = FieldFactory.createField("" +
                        "XXXXXX____" +
                        "XXXXXXX__X" +
                        "XXXXXXXX_X" +
                        "XXXXXXXXXX" +
                        "XXXXXXXXXX"
                , maxTargetHeight);

        PieceCounter pieceCounter = new PieceCounter(Collections.singletonList(Piece.S));

        LineFillRunner lineFillRunner = createLineFillRunner(pieceCounter.getBlocks().size(), maxTargetHeight, maxTargetHeight);

        List<Result> results = lineFillRunner.search(initField, pieceCounter, targetY, initField.getFilledLine())
                .parallel()
                .collect(Collectors.toList());

        assertThat(results).hasSize(1);

        verifyDuplicate(results);

        verifyFill(results, targetY, initField.getFilledLine(), maxTargetHeight);
    }

    @Test
    @LongTest
    void case4() {
        int targetY = 2;
        int maxTargetHeight = 5;
        Field initField = FieldFactory.createField(maxTargetHeight);

        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());

        LineFillRunner lineFillRunner = createLineFillRunner(pieceCounter.getBlocks().size(), maxTargetHeight, maxTargetHeight);

        List<Result> results = lineFillRunner.search(initField, pieceCounter, targetY, initField.getFilledLine())
                .parallel()
                .collect(Collectors.toList());

        assertThat(results).hasSize(6076938);

        verifyFill(results, targetY, initField.getFilledLine(), maxTargetHeight);
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

    private void verifyDuplicate(List<Result> results) {
        TreeSet<BlockField> blockFields = new TreeSet<>(BlockField::compareTo);
        for (Result result : results) {
            blockFields.add(result.parseToBlockField());
        }
        assertThat(blockFields).hasSize(results.size());
    }

    private void verifyFill(List<Result> results, int targetY, long initFilledLine, int maxTargetHeight) {
        boolean allMatch = results.parallelStream()
                .allMatch(result -> {
                    long filledLine = result.getAllMergedField().getFilledLine();

                    // targetYが揃っている
                    if ((filledLine & KeyOperators.getBitKey(targetY)) == 0L) {
                        return false;
                    }

                    // maxTargetHeightより上にブロックがない
                    long usingKey = result.getAllMergedField().getUsingKey();
                    long aboveY = KeyOperators.getMaskForKeyAboveY(maxTargetHeight);
                    if ((usingKey & aboveY) != 0L) {
                        return false;
                    }

                    // targetYより下で揃っているラインが最初と変わっていない
                    return (filledLine & KeyOperators.getMaskForKeyBelowY(targetY)) == initFilledLine;
                });

        if (!allMatch) {
            fail("Target Y does not satisfy");
        }
    }
}
