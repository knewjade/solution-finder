package searcher.spins;

import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.PieceCounter;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.field.ColoredField;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.Piece;
import entry.path.output.OneFumenParser;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.spins.results.Result;
import searcher.spins.roof.results.RoofResult;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SpinRunnerTest {
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
        SpinRunner runner = new SpinRunner(4, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 1).parallel().collect(Collectors.toList());
        assertThat(results).hasSize(303);

        verify(results);
    }

    private void verify(List<RoofResult> results) {
        // 重複がない
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
        SpinRunner runner = new SpinRunner(5, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 1).parallel().collect(Collectors.toList());

        assertThat(results).hasSize(5000);

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
        SpinRunner runner = new SpinRunner(4, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 2).parallel().collect(Collectors.toList());

        assertThat(results).hasSize(272);

        verify(results);
    }

    @Test
    void case2() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField("" +
                        "XXXXXX____" +
                        "XXXXX_____" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXX__" +
                        ""
                , fieldHeight);
        SpinRunner runner = new SpinRunner(5, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Arrays.asList(Piece.L, Piece.S, Piece.T));
        List<RoofResult> results = runner.search(initField, pieceCounter, 1).parallel().collect(Collectors.toList());

        showTetfu(fieldHeight, initField, results);

        assertThat(results).hasSize(36);

        verify(results);
    }

    private void showTetfu(int fieldHeight, Field initField, List<RoofResult> results) {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        OneFumenParser oneFumenParser = new OneFumenParser(minoFactory, colorConverter);
        List<TetfuElement> elements = results.stream()
                .map(roofResult -> {
                    Result result = roofResult.getLastResult();
                    List<MinoOperationWithKey> operations = result.operationStream().collect(Collectors.toList());
                    ColoredField coloredField = oneFumenParser.parseToColoredField(operations, initField, fieldHeight);
                    return new TetfuElement(coloredField, "");
                })
                .collect(Collectors.toList());

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        String encode = tetfu.encode(elements);
        System.out.println("https://knewjade.github.io/fumen-for-mobile/#?d=v115@" + encode);
    }
}