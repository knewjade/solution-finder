package searcher.spins;

import common.datastore.PieceCounter;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.spins.roof.results.RoofResult;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MainRunnerTest {
    @Test
    void case1() {
        Field initField = FieldFactory.createField("" +
                "XXXX______" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXXXX__" +
                "XXXXXXXXX_" +
                ""
        );
        MainRunner runner = new MainRunner(5, 8);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 1).collect(Collectors.toList());
        assertThat(results).hasSize(105);
    }

    @Test
    @LongTest
    void case2() {
        Field initField = FieldFactory.createField("" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                "__________" +
                ""
        );
        MainRunner runner = new MainRunner(5, 8);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 2).collect(Collectors.toList());
        assertThat(results).hasSize(272);
    }
}