package core.field;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

class FieldHelperTest {
    @Test
    void isTSpin() {
        Field field = FieldFactory.createField("" +
                "_X________" +
                "_____X____" +
                "_X_X______"
        );

        assertThat(FieldHelper.isTSpin(field, 0, 1)).isTrue();
        assertThat(FieldHelper.isTSpin(field, 2, 1)).isTrue();
        assertThat(FieldHelper.isTSpin(field, 6, 0)).isTrue();

        assertThat(FieldHelper.isTSpin(field, 3, 1)).isFalse();
        assertThat(FieldHelper.isTSpin(field, 9, 1)).isFalse();
        assertThat(FieldHelper.isTSpin(field, 7, 0)).isFalse();
    }
}