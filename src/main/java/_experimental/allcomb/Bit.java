package _experimental.allcomb;

public class Bit {
    public final int width;
    public final int height;
    public final int maxBit;
    public final long fillBoard;

    Bit(int width, int height) {
        this.width = width;
        this.height = height;
        this.maxBit = height * width;
        this.fillBoard = (1L << maxBit) - 1L;
    }
}
