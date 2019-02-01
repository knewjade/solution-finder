package core.field;

import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FieldHelperTest {
    @Test
    void getUsingHeight() {
        Randoms randoms = new Randoms();
        int maxHeight = 24;
        for (int y = 0; y < maxHeight; y++) {
            int x = randoms.nextIntOpen(0, 10);

            Field field = FieldFactory.createField(maxHeight);
            field.setBlock(x, y);

            assertThat(FieldHelper.getUsingHeight(field)).isEqualTo(y + 1);
        }
    }
}