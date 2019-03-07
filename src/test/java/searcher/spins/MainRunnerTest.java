package searcher.spins;

import common.datastore.BlockField;
import common.datastore.PieceCounter;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.spins.roof.results.RoofResult;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MainRunnerTest {
    @Test
    void case1_h4() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_" +
                        ""
                , fieldHeight);
        MainRunner runner = new MainRunner(4, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 1).parallel().collect(Collectors.toList());
        assertThat(results).hasSize(350);

        verify(results);
    }

    private void verify(List<RoofResult> results) {
        TreeSet<BlockField> blockFields = new TreeSet<>(BlockField::compareTo);
        for (RoofResult result : results) {
            BlockField e = result.getLastResult().parseToBlockField();
            boolean add = blockFields.add(e);
            if (!add) {
                System.out.println(BlockFieldView.toString(e));
            }
        }

        assertThat(blockFields).hasSize(results.size());
    }

    @Test
    @LongTest
    void case1_h5() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_" +
                        ""
                , fieldHeight);
        MainRunner runner = new MainRunner(5, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 1).parallel().collect(Collectors.toList());

        assertThat(results).hasSize(6827);

        verify(results);
    }

    @Test
    @LongTest
    void caseTSDOpening() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "__________" +
                        ""
                , fieldHeight);
        MainRunner runner = new MainRunner(4, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 2).parallel().collect(Collectors.toList());

        assertThat(results).hasSize(272);

        verify(results);
    }
}