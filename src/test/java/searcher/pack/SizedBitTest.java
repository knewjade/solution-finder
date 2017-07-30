package searcher.pack;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SizedBitTest {
    @Test
    void size2x4() {
        SizedBit sizedBit = new SizedBit(2, 4);
        assertThat(sizedBit)
                .returns(2, SizedBit::getWidth)
                .returns(4, SizedBit::getHeight)
                .returns(0b11111111L, SizedBit::getFillBoard)
                .returns(8, SizedBit::getMaxBitDigit);
    }

    @Test
    void size2x5() {
        SizedBit sizedBit = new SizedBit(2, 5);
        assertThat(sizedBit)
                .returns(2, SizedBit::getWidth)
                .returns(5, SizedBit::getHeight)
                .returns(0b1111111111L, SizedBit::getFillBoard)
                .returns(10, SizedBit::getMaxBitDigit);
    }

    @Test
    void size3x4() {
        SizedBit sizedBit = new SizedBit(3, 4);
        assertThat(sizedBit)
                .returns(3, SizedBit::getWidth)
                .returns(4, SizedBit::getHeight)
                .returns(0b111111111111L, SizedBit::getFillBoard)
                .returns(12, SizedBit::getMaxBitDigit);
    }

    @Test
    void size3x5() {
        SizedBit sizedBit = new SizedBit(3, 5);
        assertThat(sizedBit)
                .returns(3, SizedBit::getWidth)
                .returns(5, SizedBit::getHeight)
                .returns(0b111111111111111L, SizedBit::getFillBoard)
                .returns(15, SizedBit::getMaxBitDigit);
    }
}