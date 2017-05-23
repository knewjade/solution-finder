package _experimental.allcomb;

public class SizedBit {
    private final int width;
    private final int height;
    private final int maxBitDigit;
    private final long fillBoard;

    public SizedBit(int width, int height) {
        this.width = width;
        this.height = height;
        this.maxBitDigit = height * width;
        this.fillBoard = (1L << getMaxBitDigit()) - 1L;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getMaxBitDigit() {
        return maxBitDigit;
    }

    public long getFillBoard() {
        return fillBoard;
    }
}
