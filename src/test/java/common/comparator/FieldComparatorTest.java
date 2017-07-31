package common.comparator;

import core.field.Field;
import core.field.FieldFactory;
import core.field.MiddleField;
import core.field.SmallField;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FieldComparatorTest {
    @Test
    void compareEqual() throws Exception {
        Field field1 = FieldFactory.createSmallField("");
        Field field2 = FieldFactory.createSmallField("");
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2)).isEqualTo(0);
    }

    @Test
    void compareEqual2() throws Exception {
        Field field1 = FieldFactory.createSmallField("XXXX______");
        Field field2 = FieldFactory.createMiddleField("XXXX______");
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2)).isEqualTo(0);
    }

    @Test
    void compareDiff1() throws Exception {
        Field field1 = FieldFactory.createSmallField("X_________");
        Field field2 = FieldFactory.createMiddleField("");
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2)).isNotEqualTo(0);

        boolean isLess = comparator.compare(field1, field2) < 0;
        assertThat(comparator.compare(field2, field1) < 0).isEqualTo(!isLess);
    }

    @Test
    void compareDiff2() throws Exception {
        Field field1 = FieldFactory.createSmallField("X_________");
        Field field2 = FieldFactory.createSmallField("");
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2)).isNotEqualTo(0);

        boolean isLess = comparator.compare(field1, field2) < 0;
        assertThat(comparator.compare(field2, field1) < 0).isEqualTo(!isLess);
    }

    @Test
    void compareDiff3() throws Exception {
        Field field1 = FieldFactory.createMiddleField("");
        Field field2 = FieldFactory.createSmallField("X_________");
        Field field3 = FieldFactory.createMiddleField("XXX_______XXX_______");

        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2)).isNotEqualTo(0);
        assertThat(comparator.compare(field2, field3)).isNotEqualTo(0);
        assertThat(comparator.compare(field1, field3)).isNotEqualTo(0);

        assert comparator.compare(field1, field2) < 0 && comparator.compare(field2, field3) < 0;
        assertThat(comparator.compare(field1, field3)).isLessThan(0);
    }

    @Test
    void compare() throws Exception {
        Randoms randoms = new Randoms();
        FieldComparator comparator = new FieldComparator();

        for (int count = 0; count < 10000; count++) {
            // same field
            int height = randoms.nextInt(1, 6);
            Field field1 = new SmallField();
            Field field2 = new MiddleField();

            int maxBlock = randoms.nextInt(1, 15);
            for (int block = 0; block < maxBlock; block++) {
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(height);
                field1.setBlock(x, y);
                field2.setBlock(x, y);
            }

            assertThat(comparator.compare(field1, field2)).isEqualTo(0);
            assertThat(comparator.compare(field2, field1)).isEqualTo(0);

            // 1block different field
            int x = randoms.nextInt(10);
            int y = randoms.nextInt(height);
            if (field1.isEmpty(x, y))
                field1.setBlock(x, y);
            else
                field1.removeBlock(x, y);

            // assert is not 0 & reversed sign
            assertThat(comparator.compare(field1, field2) * comparator.compare(field2, field1)).isLessThan(0);
        }
    }
}