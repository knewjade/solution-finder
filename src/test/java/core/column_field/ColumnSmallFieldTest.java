package core.column_field;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnSmallFieldTest {
    @Test
    void setBlock() {
        ColumnSmallField field = new ColumnSmallField();
        int height = 4;

        assertThat(field.isEmpty(0, 0, height)).isTrue();
        field.setBlock(0, 0, height);
        assertThat(field.isEmpty(0, 0, height)).isFalse();
        field.removeBlock(0, 0, height);
        assertThat(field.isEmpty(0, 0, height)).isTrue();
    }

    @Test
    void getBoard() {
        ColumnSmallField field = new ColumnSmallField();
        int height = 4;

        assertThat(field.getBoard(0)).isEqualTo(0L);
        field.setBlock(0, 0, height);
        assertThat(field.getBoard(0)).isEqualTo(1L);
    }

    @Test
    void getBoardCount() {
        int height = 4;
        ColumnSmallField field = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        assertThat(field.getBoardCount()).isEqualTo(1);
    }

    @Test
    void getNumOfAllBlocks() {
        int height = 4;
        ColumnSmallField field = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        assertThat(field.getNumOfAllBlocks()).isEqualTo(4);
    }

    @Test
    void merge() {
        int height = 4;

        ColumnSmallField field1 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field2 = ColumnFieldFactory.createField("" +
                "___" +
                "__X" +
                "X__" +
                "___", height);
        field1.merge(field2);

        ColumnSmallField expect = ColumnFieldFactory.createField("" +
                "XX_" +
                "__X" +
                "X__" +
                "_X_", height);
        assertThat(field1).isEqualTo(expect);
    }

    @Test
    void reduce() {
        int height = 4;

        ColumnSmallField field1 = ColumnFieldFactory.createField("" +
                "XX_" +
                "_XX" +
                "XX_" +
                "_XX", height);
        ColumnSmallField field2 = ColumnFieldFactory.createField("" +
                "_X_" +
                "_X_" +
                "_X_" +
                "_X_", height);
        field1.reduce(field2);

        ColumnSmallField expect = ColumnFieldFactory.createField("" +
                "X__" +
                "__X" +
                "X__" +
                "__X", height);
        assertThat(field1).isEqualTo(expect);
    }

    @Test
    void canMerge1() {
        int height = 4;

        ColumnSmallField field1 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field2 = ColumnFieldFactory.createField("" +
                "___" +
                "__X" +
                "X__" +
                "___", height);
        assertThat(field1.canMerge(field2)).isFalse();
    }

    @Test
    void canMerge2() {
        int height = 4;

        ColumnSmallField field1 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field2 = ColumnFieldFactory.createField("" +
                "___" +
                "__X" +
                "__X" +
                "__X", height);
        assertThat(field1.canMerge(field2)).isTrue();
    }

    @Test
    void freeze() {
        int height = 4;

        ColumnSmallField field = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnField freeze = field.freeze(height);

        freeze.setBlock(0, 2, height);
        freeze.setBlock(0, 3, height);

        ColumnSmallField expect = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        assertThat(freeze).isNotEqualTo(expect);
        assertThat(field).isEqualTo(expect);
    }

    @Test
    void equals() {
        int height = 4;

        ColumnSmallField field1 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field2 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field3 = ColumnFieldFactory.createField("" +
                "XX_" +
                "__X" +
                "__X" +
                "XX_", height);
        assertThat(field1.equals(field2)).isTrue();
        assertThat(field2.equals(field1)).isTrue();
        assertThat(field1.equals(field3)).isFalse();
        assertThat(field3.equals(field1)).isFalse();
    }

    @Test
    void hashCode1() {
        int height = 4;

        ColumnSmallField field1 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field2 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field3 = ColumnFieldFactory.createField("" +
                "XX_" +
                "__X" +
                "__X" +
                "XX_", height);
        assertThat(field1.hashCode()).isEqualTo(field2.hashCode());
        assertThat(field1.hashCode()).isNotEqualTo(field3.hashCode());
    }

    @Test
    void compareTo() {
        int height = 4;

        ColumnSmallField field1 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field2 = ColumnFieldFactory.createField("" +
                "XX_" +
                "___" +
                "X__" +
                "_X_", height);
        ColumnSmallField field3 = ColumnFieldFactory.createField("" +
                "XX_" +
                "__X" +
                "__X" +
                "XX_", height);
        assertThat(field1.compareTo(field2)).isEqualTo(0);
        assertThat(field2.compareTo(field1)).isEqualTo(0);
        assertThat(field1.compareTo(field3) * field3.compareTo(field1)).isLessThan(0);
    }
}