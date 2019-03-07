package searcher.spins.fill.line.next;

public class RemainderField {
    private final int minX;
    private final int targetBlockCount;

    RemainderField(int minX, int targetBlockCount) {
        this.minX = minX;
        this.targetBlockCount = targetBlockCount;
    }

    public int getMinX() {
        return minX;
    }

    public int getTargetBlockCount() {
        return targetBlockCount;
    }

    @Override
    public String toString() {
        return "RemainderField{" +
                "minX=" + minX +
                ", targetBlockCount=" + targetBlockCount +
                '}';
    }
}
