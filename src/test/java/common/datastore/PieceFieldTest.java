package common.datastore;

import core.field.Field;
import core.field.FieldFactory;
import core.field.SmallField;
import core.mino.Piece;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class PieceFieldTest {
    @Test
    void mergeAndGet() throws Exception {
        int height = 4;
        BlockField blockField = new BlockField(height);

        Field field1 = FieldFactory.createField("" +
                "__X_______" +
                "_XXX______"
        );
        blockField.merge(field1, Piece.T);

        Field field2 = FieldFactory.createField("" +
                "X_________" +
                "XX________" +
                "X_________"
        );
        blockField.merge(field2, Piece.T);

        Field expected = FieldFactory.createField("" +
                "X_________" +
                "XXX_______" +
                "XXXX______"
        );
        assertThat(blockField.get(Piece.T)).isEqualTo(expected);

        for (Piece piece : Arrays.asList(Piece.I, Piece.O, Piece.S, Piece.Z, Piece.L, Piece.J)) {
            Field field = new SmallField();
            assertThat(blockField.get(piece)).isEqualTo(field);
        }
    }

    @Test
    void compare1() throws Exception {
        int height = 4;
        BlockField blockField1 = new BlockField(height);

        Field field1 = FieldFactory.createField("" +
                "__X_______" +
                "_XXX______"
        );
        blockField1.merge(field1, Piece.T);

        Field field2 = FieldFactory.createField("" +
                "X_________" +
                "XX________" +
                "X_________"
        );
        blockField1.merge(field2, Piece.T);

        BlockField blockField2 = new BlockField(height);
        Field merged = FieldFactory.createField("" +
                "X_________" +
                "XXX_______" +
                "XXXX______"
        );
        blockField2.merge(merged, Piece.T);

        assertThat(blockField1.equals(blockField2)).isEqualTo(true);
        assertThat(blockField2.equals(blockField1)).isEqualTo(true);

        assertThat(blockField1.compareTo(blockField2)).isEqualTo(0);
        assertThat(blockField2.compareTo(blockField1)).isEqualTo(0);
    }

    @Test
    void compare2() throws Exception {
        int height = 4;
        BlockField blockField1 = new BlockField(height);

        Field field1 = FieldFactory.createField("" +
                "__X_______" +
                "_XXX______"
        );
        blockField1.merge(field1, Piece.T);

        Field field2 = FieldFactory.createField("" +
                "X_________" +
                "XX________" +
                "X_________"
        );
        blockField1.merge(field2, Piece.T);

        BlockField blockField2 = new BlockField(height);
        Field merged = FieldFactory.createField("" +
                "X_________" +
                "XXX_______" +
                "XXXX______"
        );
        blockField2.merge(merged, Piece.I);

        assertThat(blockField1.equals(blockField2)).isEqualTo(false);
        assertThat(blockField2.equals(blockField1)).isEqualTo(false);

        // assert is not 0 & sign reversed
        assertThat(blockField1.compareTo(blockField2) * blockField2.compareTo(blockField1)).isLessThan(0);
    }

    @Test
    void compare3() throws Exception {
        int height = 4;
        BlockField blockField1 = new BlockField(height);

        Field field1 = FieldFactory.createField("" +
                "___X______" +
                "__XX______" +
                "___X______"
        );
        blockField1.merge(field1, Piece.T);

        Field field2 = FieldFactory.createField("" +
                "X_________" +
                "XX________" +
                "X_________"
        );
        blockField1.merge(field2, Piece.T);

        BlockField blockField2 = new BlockField(height);
        Field merged = FieldFactory.createField("" +
                "X_________" +
                "XXX_______" +
                "XXXX______"
        );
        blockField2.merge(merged, Piece.T);

        assertThat(blockField1.equals(blockField2)).isEqualTo(false);
        assertThat(blockField2.equals(blockField1)).isEqualTo(false);

        // assert is not 0 & sign reversed
        assertThat(blockField1.compareTo(blockField2) * blockField2.compareTo(blockField1)).isLessThan(0);
    }
}