package common;

import common.comparator.FieldComparator;
import core.field.Field;
import core.field.FieldFactory;
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

        assert comparator.compare(field1, field2) < 0  && comparator.compare(field2, field3) < 0;
        assertThat(comparator.compare(field1, field3), is(lessThan(0)));
    }
}