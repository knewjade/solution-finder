package searcher.spins.fill.line;

import common.datastore.PieceCounter;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import org.junit.jupiter.api.Test;
import searcher.spins.AllSimpleOriginalPieces;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LineFillRunnerTest {
    @Test
    void case1() {
        int maxHeight = 8;
        Field initField = FieldFactory.createField(maxHeight);
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        AllSimpleOriginalPieces allSimpleOriginalPieces = new AllSimpleOriginalPieces(minoFactory, minoShifter, 10, maxHeight);
        List<SimpleOriginalPiece> originalPieces = allSimpleOriginalPieces.createList().stream()
                .filter(simpleOriginalPiece -> initField.canMerge(simpleOriginalPiece.getMinoField()))
                .collect(Collectors.toList());

        LineFillRunner runner = LineFillRunner.create(minoFactory, minoShifter, originalPieces, maxHeight);

        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        Stream<LineFillResult> stream = runner.search(pieceCounter, initField, initField.getFilledLine(), 0, 3, 10).parallel();
        long count = stream.count();

        assertThat(count).isEqualTo(13521180L);
    }

    @Test
    void case2() {
        int maxHeight = 8;
        Field initField = FieldFactory.createField("" +
                        "XXXXX_____" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_"
                , maxHeight);
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        AllSimpleOriginalPieces allSimpleOriginalPieces = new AllSimpleOriginalPieces(minoFactory, minoShifter, 10, maxHeight);
        List<SimpleOriginalPiece> originalPieces = allSimpleOriginalPieces.createList().stream()
                .filter(simpleOriginalPiece -> initField.canMerge(simpleOriginalPiece.getMinoField()))
                .collect(Collectors.toList());

        LineFillRunner runner = LineFillRunner.create(minoFactory, minoShifter, originalPieces, maxHeight);

        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        Stream<LineFillResult> stream = runner.search(pieceCounter, initField, initField.getFilledLine(), 5, 4, 5).parallel();
        long count = stream.count();

        assertThat(count).isEqualTo(7833L);
    }

    @Test
    void case3() {
        int maxHeight = 3;
        Field initField = FieldFactory.createField("" +
                        "XXXXXXXX__" +
                        "XXXXXXXX__" +
                        "XXXXXXXX__"
                , maxHeight);
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        AllSimpleOriginalPieces allSimpleOriginalPieces = new AllSimpleOriginalPieces(minoFactory, minoShifter, 10, maxHeight);
        List<SimpleOriginalPiece> originalPieces = allSimpleOriginalPieces.createList().stream()
                .filter(simpleOriginalPiece -> initField.canMerge(simpleOriginalPiece.getMinoField()))
                .collect(Collectors.toList());

        LineFillRunner runner = LineFillRunner.create(minoFactory, minoShifter, originalPieces, maxHeight);

        PieceCounter pieceCounter = new PieceCounter(Collections.singletonList(Piece.O));
        Stream<LineFillResult> stream = runner.search(pieceCounter, initField, initField.getFilledLine(), 8, 1, 2).parallel();
        long count = stream.count();

        assertThat(count).isEqualTo(1L);
    }

    @Test
    void case4() {
        int maxHeight = 4;
        Field initField = FieldFactory.createField("" +
                        "XXXXXX____" +
                        "XXXXXX____" +
                        "XXXXXXXXXX" +
                        "XXXXXX____"
                , maxHeight);
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        AllSimpleOriginalPieces allSimpleOriginalPieces = new AllSimpleOriginalPieces(minoFactory, minoShifter, 10, maxHeight);
        List<SimpleOriginalPiece> originalPieces = allSimpleOriginalPieces.createList().stream()
                .filter(simpleOriginalPiece -> initField.canMerge(simpleOriginalPiece.getMinoField()))
                .collect(Collectors.toList());

        LineFillRunner runner = LineFillRunner.create(minoFactory, minoShifter, originalPieces, maxHeight);

        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        Stream<LineFillResult> stream = runner.search(pieceCounter, initField, initField.getFilledLine(), 6, 2, 4).parallel();
        long count = stream.count();

        assertThat(count).isEqualTo(43L);
    }
}