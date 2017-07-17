package core.mino;

import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinoFactoryTest {
    @Test
    void create() {
        MinoFactory minoFactory = new MinoFactory();
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = minoFactory.create(block, rotate);
                for (int count = 0; count < 100; count++) {
                    // 同じインスタンスであること
                    assertThat(mino == minoFactory.create(block, rotate)).isTrue();
                }
            }
        }
    }
}