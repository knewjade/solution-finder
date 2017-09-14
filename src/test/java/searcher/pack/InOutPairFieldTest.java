package searcher.pack;

import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnFieldView;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.FieldFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InOutPairFieldTest {
    @Test
    void createInOutPairFields3x4() {
        Field field = FieldFactory.createField("" +
                "___X__X_XX" +
                "__X__XXXX_" +
                "_X__XX_XX_" +
                "X___X__X_X"
        );
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create pairs
        List<InOutPairField> fields = InOutPairField.createInOutPairFields(sizedBit, field);
        assertThat(fields).hasSize(3);

        // Check inner
        ColumnSmallField innerField1 = ColumnFieldFactory.createField("" +
                "___" +
                "__X" +
                "_X_" +
                "X__", height);
        ColumnSmallField innerField2 = ColumnFieldFactory.createField("" +
                "X__" +
                "__X" +
                "_XX" +
                "_X_", height);
        ColumnSmallField innerField3 = ColumnFieldFactory.createField("" +
                "X_X" +
                "XXX" +
                "_XX" +
                "_X_", height);

        assertThat(fields.stream().map(InOutPairField::getInnerField))
                .containsExactly(innerField1, innerField2, innerField3);

        // Check outer
        ColumnSmallField outerField1 = ColumnFieldFactory.createField("" +
                "___X__" +
                "_____X" +
                "____XX" +
                "____X_", height);
        ColumnSmallField outerField2 = ColumnFieldFactory.createField("" +
                "___X_X" +
                "___XXX" +
                "____XX" +
                "____X_", height);
        ColumnSmallField outerField3 = ColumnFieldFactory.createField("" +
                "___XXX" +
                "____XX" +
                "____XX" +
                "___XXX", height);

        assertThat(fields.stream().map(InOutPairField::getOuterField))
                .containsExactly(outerField1, outerField2, outerField3);
    }

    @Test
    void createInOutPairFields2x5() {
        Field field = FieldFactory.createField("" +
                "____X___X_" +
                "___X___XX_" +
                "__X___XX_X" +
                "_X___XX__X" +
                "X____X___X"
        );
        int width = 2;
        int height = 5;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create pairs
        List<InOutPairField> fields = InOutPairField.createInOutPairFields(sizedBit, field);
        assertThat(fields).hasSize(4);

        // Check inner
        ColumnSmallField innerField1 = ColumnFieldFactory.createField("" +
                "__" +
                "__" +
                "__" +
                "_X" +
                "X_", height);
        ColumnSmallField innerField2 = ColumnFieldFactory.createField("" +
                "__" +
                "_X" +
                "X_" +
                "__" +
                "__", height);
        ColumnSmallField innerField3 = ColumnFieldFactory.createField("" +
                "X_" +
                "__" +
                "__" +
                "_X" +
                "_X", height);
        ColumnSmallField innerField4 = ColumnFieldFactory.createField("" +
                "__" +
                "_X" +
                "XX" +
                "X_" +
                "__", height);

        assertThat(fields.stream().map(InOutPairField::getInnerField))
                .containsExactly(innerField1, innerField2, innerField3, innerField4);

        // Check outer
        ColumnSmallField outerField1 = ColumnFieldFactory.createField("" +
                "_____" +
                "___X_" +
                "__X__" +
                "_____" +
                "_____", height);
        ColumnSmallField outerField2 = ColumnFieldFactory.createField("" +
                "__X__" +
                "_____" +
                "_____" +
                "___X_" +
                "___X_", height);
        ColumnSmallField outerField3 = ColumnFieldFactory.createField("" +
                "_____" +
                "___X_" +
                "__XX_" +
                "__X__" +
                "_____", height);
        ColumnSmallField outerField4 = ColumnFieldFactory.createField("" +
                "__X_X" +
                "__X_X" +
                "___XX" +
                "___XX" +
                "___XX", height);

        assertThat(fields.stream().map(InOutPairField::getOuterField))
                .containsExactly(outerField1, outerField2, outerField3, outerField4);
    }

    @Test
    void createMaxOuterBoard3x4() {
        Field field = FieldFactory.createField("" +
                "___X__X_XX" +
                "__X__XXXX_" +
                "_X__XX_XX_" +
                "X___X__X_X"
        );
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
        ColumnSmallField expects = ColumnFieldFactory.createField("" +
                "___X__" +
                "_____X" +
                "____XX" +
                "____X_", height);

        assertThat(maxOuterBoard).isEqualTo(expects);
    }

    @Test
    void createMaxOuterBoard3x4_2() {
        Field field = FieldFactory.createField("" +
                "___X__X_X_" +
                "__X__XXXX_" +
                "_X__XX_XX_" +
                "X___X__X_X"
        );
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
        ColumnSmallField expects = ColumnFieldFactory.createField("" +
                "______" +
                "_____X" +
                "____XX" +
                "____X_", height);

        assertThat(maxOuterBoard).isEqualTo(expects);
    }

    @Test
    void createMaxOuterBoard2x5() {
        Field field = FieldFactory.createField("" +
                "____X___X_" +
                "___X___XX_" +
                "__X___XX_X" +
                "_X___XX__X" +
                "X____X___X"
        );
        int width = 2;
        int height = 5;
        SizedBit sizedBit = new SizedBit(width, height);

        ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
        ColumnSmallField expects = ColumnFieldFactory.createField("" +
                "_____" +
                "_____" +
                "_____" +
                "_____" +
                "_____", height);

        assertThat(maxOuterBoard).isEqualTo(expects);
    }

    @Test
    void createMaxOuterBoard2x5_2() {
        Field field = FieldFactory.createField("" +
                "___XX_XXXX" +
                "XXXXX_XXXX" +
                "_XXX_XXX_X" +
                "__XXXXXXXX" +
                "X_XXXXXXXX"
        );
        int width = 2;
        int height = 5;
        SizedBit sizedBit = new SizedBit(width, height);

        ColumnSmallField maxOuterBoard = InOutPairField.createMaxOuterBoard(sizedBit, field);
        ColumnSmallField expects = ColumnFieldFactory.createField("" +
                "____X" +
                "__X_X" +
                "___X_" +
                "__XXX" +
                "__XXX", height);

        assertThat(maxOuterBoard).isEqualTo(expects);
    }
}