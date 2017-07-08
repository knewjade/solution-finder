package common.comparator;

import core.field.Field;
import core.field.FieldFactory;
import core.field.MiddleField;
import core.field.SmallField;
import lib.Randoms;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class FieldComparatorTest {
    @Test
    public void compareEqual() throws Exception {
        Field field1 = FieldFactory.createSmallField("");
        Field field2 = FieldFactory.createSmallField("");
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2), is(0));
    }

    @Test
    public void compareEqual2() throws Exception {
        Field field1 = FieldFactory.createSmallField("XXXX______");
        Field field2 = FieldFactory.createMiddleField("XXXX______");
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2), is(0));
    }

    @Test
    public void compareDiff1() throws Exception {
        Field field1 = FieldFactory.createSmallField("X_________");
        Field field2 = FieldFactory.createMiddleField("");
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2), is(not(0)));

        boolean isLess = comparator.compare(field1, field2) < 0;
        assertThat(comparator.compare(field2, field1) < 0, is(!isLess));
    }

    @Test
    public void compareDiff2() throws Exception {
        Field field1 = FieldFactory.createSmallField("X_________");
        Field field2 = FieldFactory.createSmallField("");
        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2), is(not(0)));

        boolean isLess = comparator.compare(field1, field2) < 0;
        assertThat(comparator.compare(field2, field1) < 0, is(!isLess));
    }

    @Test
    public void compareDiff3() throws Exception {
        Field field1 = FieldFactory.createMiddleField("");
        Field field2 = FieldFactory.createSmallField("X_________");
        Field field3 = FieldFactory.createMiddleField("XXX_______XXX_______");

        FieldComparator comparator = new FieldComparator();
        assertThat(comparator.compare(field1, field2), is(not(0)));
        assertThat(comparator.compare(field2, field3), is(not(0)));
        assertThat(comparator.compare(field1, field3), is(not(0)));

        assert comparator.compare(field1, field2) < 0 && comparator.compare(field2, field3) < 0;
        assertThat(comparator.compare(field1, field3), is(lessThan(0)));
    }

    @Test
    public void compare() throws Exception {
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

            MatcherAssert.assertThat(comparator.compare(field1, field2), is(0));
            MatcherAssert.assertThat(comparator.compare(field2, field1), is(0));

            // 1block different field
            int x = randoms.nextInt(10);
            int y = randoms.nextInt(height);
            if (field1.isEmpty(x, y))
                field1.setBlock(x, y);
            else
                field1.removeBlock(x, y);

            // assert is not 0 & reversed sign
            MatcherAssert.assertThat(comparator.compare(field1, field2) * comparator.compare(field2, field1), is(lessThan(0)));
        }
    }
}