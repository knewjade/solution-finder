package core.mino;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class BlockTest {
    @Test
    void getBlock() {
        assertThat(Block.getBlock(0)).isEqualTo(Block.T);
        assertThat(Block.getBlock(1)).isEqualTo(Block.I);
        assertThat(Block.getBlock(2)).isEqualTo(Block.L);
        assertThat(Block.getBlock(3)).isEqualTo(Block.J);
        assertThat(Block.getBlock(4)).isEqualTo(Block.S);
        assertThat(Block.getBlock(5)).isEqualTo(Block.Z);
        assertThat(Block.getBlock(6)).isEqualTo(Block.O);
    }

    @Test
    void valueList() {
        List<Block> blocks = Block.valueList();
        assertThat(blocks)
                .hasSize(7)
                .containsExactly(Block.T, Block.I, Block.L, Block.J, Block.S, Block.Z, Block.O);
    }

    @Test
    void getSize() {
        assertThat(Block.getSize()).isEqualTo(7);
    }

    @Test
    void getNumber() {
        assertThat(Block.T.getNumber()).isEqualTo(0);
        assertThat(Block.I.getNumber()).isEqualTo(1);
        assertThat(Block.L.getNumber()).isEqualTo(2);
        assertThat(Block.J.getNumber()).isEqualTo(3);
        assertThat(Block.S.getNumber()).isEqualTo(4);
        assertThat(Block.Z.getNumber()).isEqualTo(5);
        assertThat(Block.O.getNumber()).isEqualTo(6);
    }

    @Test
    void getName() {
        assertThat(Block.T.getName()).isEqualTo("T");
        assertThat(Block.I.getName()).isEqualTo("I");
        assertThat(Block.L.getName()).isEqualTo("L");
        assertThat(Block.J.getName()).isEqualTo("J");
        assertThat(Block.S.getName()).isEqualTo("S");
        assertThat(Block.Z.getName()).isEqualTo("Z");
        assertThat(Block.O.getName()).isEqualTo("O");
    }
}