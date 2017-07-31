package common.comparator;

import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnFieldComparatorTest {
    @Test
    void compare() throws Exception {
        Randoms randoms = new Randoms();
        ColumnFieldComparator comparator = new ColumnFieldComparator();

        for (int count = 0; count < 10000; count++) {
            // same field
            int height = randoms.nextInt(1, 10);
            ColumnSmallField field1 = ColumnFieldFactory.createField();
            ColumnSmallField field2 = ColumnFieldFactory.createField();

            int maxBlock = randoms.nextInt(1, 15);
            for (int block = 0; block < maxBlock; block++) {
                int x = randoms.nextInt(10);
                int y = randoms.nextInt(height);
                field1.setBlock(x, y, height);
                field2.setBlock(x, y, height);
            }

            assertThat(comparator.compare(field1, field2)).isEqualTo(0);
            assertThat(comparator.compare(field2, field1)).isEqualTo(0);

            // 1block different field
            int x = randoms.nextInt(10);
            int y = randoms.nextInt(height);
            if (field1.isEmpty(x, y, height))
                field1.setBlock(x, y, height);
            else
                field1.removeBlock(x, y, height);

            // assert is not 0 & reversed sign
            assertThat(comparator.compare(field1, field2) * comparator.compare(field2, field1)).isLessThan(0);
        }
    }
}