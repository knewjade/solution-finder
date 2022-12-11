package entry.path.output;

import common.datastore.MinimalOperationWithKey;
import common.datastore.MinoOperationWithKey;
import common.tetfu.common.ColorConverter;
import core.field.Field;
import core.field.FieldFactory;
import core.field.KeyOperators;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SequenceFumenParserTest {
    @Test
    void case1() {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        SequenceFumenParser parser = new SequenceFumenParser(minoFactory, minoRotation, colorConverter, false);

        Field field = FieldFactory.createField("" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____"
        );

        List<MinoOperationWithKey> operationKeys = Arrays.asList(
                new MinimalOperationWithKey(minoFactory.create(Piece.S, Rotate.Spawn), 8, 0, 0L),
                new MinimalOperationWithKey(minoFactory.create(Piece.Z, Rotate.Left), 7, 1, 0L),
                new MinimalOperationWithKey(minoFactory.create(Piece.L, Rotate.Reverse), 7, 3, 0L),
                new MinimalOperationWithKey(minoFactory.create(Piece.T, Rotate.Left), 9, 1, KeyOperators.getBitKey(1))
        );
        Collections.shuffle(operationKeys);

        assertThat(parser.parse(operationKeys, field, 4)).isEqualTo("9gF8DeF8DeF8DeF8NeXNYXAFLDmClcJSAVDEHBEooR?BToAVB6OkBAvhCcsB9tBisB");
    }

    @Test
    void case2() {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        SequenceFumenParser parser = new SequenceFumenParser(minoFactory, minoRotation, colorConverter, false);

        Field field = FieldFactory.createField("" +
                "__XXXXXXXX" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "___XXXXXXX"
        );

        List<MinoOperationWithKey> operationKeys = Arrays.asList(
                new MinimalOperationWithKey(minoFactory.create(Piece.L, Rotate.Right), 1, 1, KeyOperators.getBitKey(1)),
                new MinimalOperationWithKey(minoFactory.create(Piece.S, Rotate.Spawn), 2, 1, 0L),
                new MinimalOperationWithKey(minoFactory.create(Piece.I, Rotate.Left), 0, 1, 0L)
        );

        assertThat(parser.parse(operationKeys, field, 4)).isEqualTo("/gH8DeF8CeG8CeG8JeXFYWAFLDmClcJSAVDEHBEooR?BToAVBpCBAAvhBZkBqpB");
    }
}