package _experimental.allcomb;

public class Bit {
    public final int width;
    public final int height;
    public final int maxBit;
    public final long fillBoard;
    public final long oneLineEmpty;

    Bit(int width, int height) {
        this.width = width;
        this.height = height;
        this.maxBit = height * width;
        this.fillBoard = (1L << maxBit) - 1L;
        this.oneLineEmpty = fillBoard - ((1L << height) - 1);
    }
}
