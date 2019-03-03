package searcher.spins.wall;

import common.comparator.FieldComparator;
import common.datastore.Coordinate;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.KeyOperators;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SpinMaskFieldsTest {
    private static final int MAX_HEIGHT = 8;

    @Test
    void center() {
        // 中央
        SpinMaskFields spinMaskFields = new SpinMaskFields(MAX_HEIGHT);

        FieldComparator comparator = new FieldComparator();

        List<MaskField> maskFields = spinMaskFields.get(1, 1)
                .sorted((m1, m2) -> comparator.compare(m1.getRemain(), m2.getRemain()))
                .collect(Collectors.toList());

        assertThat(maskFields).hasSize(5);

        assertThat(maskFields.get(0))
                .returns(toField(new Coordinate(0, 0), new Coordinate(2, 0), new Coordinate(0, 2)), MaskField::getRemain)
                .returns(toField(new Coordinate(2, 2)), MaskField::getNotAllowed);

        assertThat(maskFields.get(1))
                .returns(toField(new Coordinate(0, 0), new Coordinate(2, 0), new Coordinate(2, 2)), MaskField::getRemain)
                .returns(toField(new Coordinate(0, 2)), MaskField::getNotAllowed);

        assertThat(maskFields.get(2))
                .returns(toField(new Coordinate(0, 0), new Coordinate(0, 2), new Coordinate(2, 2)), MaskField::getRemain)
                .returns(toField(new Coordinate(2, 0)), MaskField::getNotAllowed);

        assertThat(maskFields.get(3))
                .returns(toField(new Coordinate(2, 0), new Coordinate(0, 2), new Coordinate(2, 2)), MaskField::getRemain)
                .returns(toField(new Coordinate(0, 0)), MaskField::getNotAllowed);

        assertThat(maskFields.get(4))
                .returns(toField(new Coordinate(0, 0), new Coordinate(2, 0), new Coordinate(0, 2), new Coordinate(2, 2)), MaskField::getRemain)
                .returns(toField(), MaskField::getNotAllowed);
    }

    @Test
    void leftSide() {
        // 左端
        SpinMaskFields spinMaskFields = new SpinMaskFields(MAX_HEIGHT);

        FieldComparator comparator = new FieldComparator();

        List<MaskField> maskFields = spinMaskFields.get(0, 2)
                .sorted((m1, m2) -> comparator.compare(m1.getRemain(), m2.getRemain()))
                .collect(Collectors.toList());

        assertThat(maskFields).hasSize(3);

        assertThat(maskFields.get(0))
                .returns(toField(new Coordinate(1, 1)), MaskField::getRemain)
                .returns(toField(new Coordinate(1, 3)), MaskField::getNotAllowed);

        assertThat(maskFields.get(1))
                .returns(toField(new Coordinate(1, 3)), MaskField::getRemain)
                .returns(toField(new Coordinate(1, 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(2))
                .returns(toField(new Coordinate(1, 1), new Coordinate(1, 3)), MaskField::getRemain)
                .returns(toField(), MaskField::getNotAllowed);
    }

    @Test
    void rightSide() {
        // 右端
        SpinMaskFields spinMaskFields = new SpinMaskFields(MAX_HEIGHT);

        FieldComparator comparator = new FieldComparator();

        List<MaskField> maskFields = spinMaskFields.get(9, 6)
                .sorted((m1, m2) -> comparator.compare(m1.getRemain(), m2.getRemain()))
                .collect(Collectors.toList());

        assertThat(maskFields).hasSize(3);

        assertThat(maskFields.get(0))
                .returns(toField(new Coordinate(8, 7)), MaskField::getRemain)
                .returns(toField(new Coordinate(8, 5)), MaskField::getNotAllowed);

        assertThat(maskFields.get(1))
                .returns(toField(new Coordinate(8, 5)), MaskField::getRemain)
                .returns(toField(new Coordinate(8, 7)), MaskField::getNotAllowed);

        assertThat(maskFields.get(2))
                .returns(toField(new Coordinate(8, 5), new Coordinate(8, 7)), MaskField::getRemain)
                .returns(toField(), MaskField::getNotAllowed);
    }

    @Test
    void bottom() {
        // 下端
        SpinMaskFields spinMaskFields = new SpinMaskFields(MAX_HEIGHT);

        FieldComparator comparator = new FieldComparator();

        List<MaskField> maskFields = spinMaskFields.get(5, 0)
                .sorted((m1, m2) -> comparator.compare(m1.getRemain(), m2.getRemain()))
                .collect(Collectors.toList());

        assertThat(maskFields).hasSize(3);

        assertThat(maskFields.get(0))
                .returns(toField(new Coordinate(4, 1)), MaskField::getRemain)
                .returns(toField(new Coordinate(6, 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(1))
                .returns(toField(new Coordinate(6, 1)), MaskField::getRemain)
                .returns(toField(new Coordinate(4, 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(2))
                .returns(toField(new Coordinate(4, 1), new Coordinate(6, 1)), MaskField::getRemain)
                .returns(toField(), MaskField::getNotAllowed);
    }

    @Test
    void upper() {
        // 上端
        SpinMaskFields spinMaskFields = new SpinMaskFields(MAX_HEIGHT);

        FieldComparator comparator = new FieldComparator();

        int y = MAX_HEIGHT - 1;
        List<MaskField> maskFields = spinMaskFields.get(4, y)
                .sorted((m1, m2) -> comparator.compare(m1.getRemain(), m2.getRemain()))
                .collect(Collectors.toList());

        assertThat(maskFields).hasSize(5);

        assertThat(maskFields.get(0))
                .returns(toField(new Coordinate(3, y - 1), new Coordinate(5, y - 1), new Coordinate(3, y + 1)), MaskField::getRemain)
                .returns(toField(new Coordinate(5, y + 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(1))
                .returns(toField(new Coordinate(3, y - 1), new Coordinate(5, y - 1), new Coordinate(5, y + 1)), MaskField::getRemain)
                .returns(toField(new Coordinate(3, y + 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(2))
                .returns(toField(new Coordinate(3, y - 1), new Coordinate(3, y + 1), new Coordinate(5, y + 1)), MaskField::getRemain)
                .returns(toField(new Coordinate(5, y - 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(3))
                .returns(toField(new Coordinate(5, y - 1), new Coordinate(3, y + 1), new Coordinate(5, y + 1)), MaskField::getRemain)
                .returns(toField(new Coordinate(3, y - 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(4))
                .returns(toField(new Coordinate(3, y - 1), new Coordinate(5, y - 1), new Coordinate(3, y + 1), new Coordinate(5, y + 1)), MaskField::getRemain)
                .returns(toField(), MaskField::getNotAllowed);
    }

    @Test
    void deletedLine1() {
        SpinMaskFields spinMaskFields = new SpinMaskFields(MAX_HEIGHT);

        FieldComparator comparator = new FieldComparator();

        List<MaskField> maskFields = spinMaskFields.get(4, 2, KeyOperators.getBitKeys(3, 5))
                .sorted((m1, m2) -> comparator.compare(m1.getRemain(), m2.getRemain()))
                .collect(Collectors.toList());

        assertThat(maskFields).hasSize(5);

        assertThat(maskFields.get(0))
                .returns(toField(new Coordinate(3, 1), new Coordinate(5, 1), new Coordinate(3, 4)), MaskField::getRemain)
                .returns(toField(new Coordinate(5, 4)), MaskField::getNotAllowed);

        assertThat(maskFields.get(1))
                .returns(toField(new Coordinate(3, 1), new Coordinate(5, 1), new Coordinate(5, 4)), MaskField::getRemain)
                .returns(toField(new Coordinate(3, 4)), MaskField::getNotAllowed);

        assertThat(maskFields.get(2))
                .returns(toField(new Coordinate(3, 1), new Coordinate(3, 4), new Coordinate(5, 4)), MaskField::getRemain)
                .returns(toField(new Coordinate(5, 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(3))
                .returns(toField(new Coordinate(5, 1), new Coordinate(3, 4), new Coordinate(5, 4)), MaskField::getRemain)
                .returns(toField(new Coordinate(3, 1)), MaskField::getNotAllowed);

        assertThat(maskFields.get(4))
                .returns(toField(new Coordinate(3, 1), new Coordinate(5, 1), new Coordinate(3, 4), new Coordinate(5, 4)), MaskField::getRemain)
                .returns(toField(), MaskField::getNotAllowed);
    }

    @Test
    void deletedLine2() {
        SpinMaskFields spinMaskFields = new SpinMaskFields(MAX_HEIGHT);

        FieldComparator comparator = new FieldComparator();

        List<MaskField> maskFields = spinMaskFields.get(4, 4, KeyOperators.getBitKeys(0, 1, 3, 5))
                .sorted((m1, m2) -> comparator.compare(m1.getRemain(), m2.getRemain()))
                .collect(Collectors.toList());

        assertThat(maskFields).hasSize(5);

        assertThat(maskFields.get(0))
                .returns(toField(new Coordinate(3, 2), new Coordinate(3, 6), new Coordinate(5, 6)), MaskField::getRemain)
                .returns(toField(new Coordinate(5, 2)), MaskField::getNotAllowed);

        assertThat(maskFields.get(1))
                .returns(toField(new Coordinate(5, 2), new Coordinate(3, 6), new Coordinate(5, 6)), MaskField::getRemain)
                .returns(toField(new Coordinate(3, 2)), MaskField::getNotAllowed);

        assertThat(maskFields.get(2))
                .returns(toField(new Coordinate(3, 2), new Coordinate(5, 2), new Coordinate(3, 6)), MaskField::getRemain)
                .returns(toField(new Coordinate(5, 6)), MaskField::getNotAllowed);

        assertThat(maskFields.get(3))
                .returns(toField(new Coordinate(3, 2), new Coordinate(5, 2), new Coordinate(5, 6)), MaskField::getRemain)
                .returns(toField(new Coordinate(3, 6)), MaskField::getNotAllowed);

        assertThat(maskFields.get(4))
                .returns(toField(new Coordinate(3, 2), new Coordinate(5, 2), new Coordinate(3, 6), new Coordinate(5, 6)), MaskField::getRemain)
                .returns(toField(), MaskField::getNotAllowed);
    }

    private Field toField(Coordinate... coordinates) {
        Field field = FieldFactory.createField(MAX_HEIGHT + 1);
        for (Coordinate coordinate : coordinates) {
            field.setBlock(coordinate.x, coordinate.y);
        }
        return field;
    }
}