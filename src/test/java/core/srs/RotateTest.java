package core.srs;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RotateTest {
    @Test
    void getRotate() {
        assertThat(Rotate.getRotate(0)).isEqualTo(Rotate.Spawn);
        assertThat(Rotate.getRotate(1)).isEqualTo(Rotate.Right);
        assertThat(Rotate.getRotate(2)).isEqualTo(Rotate.Reverse);
        assertThat(Rotate.getRotate(3)).isEqualTo(Rotate.Left);
    }

    @Test
    void valueList() {
        assertThat(Rotate.valueList()).containsExactly(Rotate.Spawn, Rotate.Right, Rotate.Reverse, Rotate.Left);
    }

    @Test
    void getSize() {
        assertThat(Rotate.getSize()).isEqualTo(4);
    }

    @Test
    void spawn() {
        assertThat(Rotate.Spawn)
                .returns(0, Rotate::getNumber)
                .returns(Rotate.Right, Rotate::getRightRotate)
                .returns(Rotate.Left, Rotate::getLeftRotate);
    }

    @Test
    void right() {
        assertThat(Rotate.Right)
                .returns(1, Rotate::getNumber)
                .returns(Rotate.Reverse, Rotate::getRightRotate)
                .returns(Rotate.Spawn, Rotate::getLeftRotate);
    }

    @Test
    void reverse() {
        assertThat(Rotate.Reverse)
                .returns(2, Rotate::getNumber)
                .returns(Rotate.Left, Rotate::getRightRotate)
                .returns(Rotate.Right, Rotate::getLeftRotate);
    }

    @Test
    void left() {
        assertThat(Rotate.Left)
                .returns(3, Rotate::getNumber)
                .returns(Rotate.Spawn, Rotate::getRightRotate)
                .returns(Rotate.Reverse, Rotate::getLeftRotate);
    }
}