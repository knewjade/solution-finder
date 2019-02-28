package searcher.spins.fill.line.next;

import core.field.Field;
import core.field.FieldFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RemainderFieldRunnerTest {
    @Test
    void case1() {
        RemainderFieldRunner runner = new RemainderFieldRunner();

        Field field = FieldFactory.createField("" +
                "__________" +
                "__________" +
                "__________" +
                "__________"
        );
        List<RemainderField> remainderFields = runner.extract(field, 2);

        assertThat(remainderFields).hasSize(1);

        assertThat(remainderFields.get(0))
                .returns(0, RemainderField::getMinX)
                .returns(10, RemainderField::getTargetBlockCount);
    }

    @Test
    void case2() {
        RemainderFieldRunner runner = new RemainderFieldRunner();

        Field field = FieldFactory.createField("" +
                "__XX__XX__"
        );
        List<RemainderField> remainderFields = runner.extract(field, 0);

        assertThat(remainderFields).hasSize(3);

        assertThat(remainderFields.get(0))
                .returns(0, RemainderField::getMinX)
                .returns(2, RemainderField::getTargetBlockCount);

        assertThat(remainderFields.get(1))
                .returns(4, RemainderField::getMinX)
                .returns(2, RemainderField::getTargetBlockCount);

        assertThat(remainderFields.get(2))
                .returns(8, RemainderField::getMinX)
                .returns(2, RemainderField::getTargetBlockCount);
    }
}