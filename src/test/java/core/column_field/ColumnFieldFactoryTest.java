package core.column_field;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnFieldFactoryTest {
    @Test
    void createField() {
        ColumnSmallField field = ColumnFieldFactory.createField();
        assertThat(field.getNumOfAllBlocks()).isEqualTo(0);
    }

    @Test
    void createField1() {
        long board = 0b1110L;
        ColumnSmallField field = ColumnFieldFactory.createField(board);
        assertThat(field.getNumOfAllBlocks()).isEqualTo(3);
        assertThat(field.isEmpty(0, 0, 4)).isTrue();
        assertThat(field.isEmpty(0, 1, 4)).isFalse();
        assertThat(field.isEmpty(0, 2, 4)).isFalse();
        assertThat(field.isEmpty(0, 3, 4)).isFalse();
        assertThat(field.isEmpty(1, 0, 4)).isTrue();
    }

    @Test
    void createField2() {
        ColumnSmallField field = ColumnFieldFactory.createField(Collections.emptyList());
        assertThat(field.getNumOfAllBlocks()).isEqualTo(0);
    }

    @Test
    void createField3() {
        long board = 0b1010101L;
        ColumnSmallField field = ColumnFieldFactory.createField(Collections.singletonList(board));
        assertThat(field.getNumOfAllBlocks()).isEqualTo(4);
        assertThat(field.isEmpty(0, 0, 4)).isFalse();
        assertThat(field.isEmpty(0, 1, 4)).isTrue();
        assertThat(field.isEmpty(0, 2, 4)).isFalse();
        assertThat(field.isEmpty(0, 3, 4)).isTrue();
        assertThat(field.isEmpty(1, 0, 4)).isFalse();
    }

    @Test
    void createField4() {
        ColumnSmallField field = ColumnFieldFactory.createField("" +
                "_X_" +
                "__X" +
                "_X_" +
                "X__", 4);

        assertThat(field.getNumOfAllBlocks()).isEqualTo(4);
        assertThat(field.isEmpty(0, 0, 4)).isFalse();
        assertThat(field.isEmpty(0, 1, 4)).isTrue();
        assertThat(field.isEmpty(0, 2, 4)).isTrue();
        assertThat(field.isEmpty(0, 3, 4)).isTrue();
    }
}