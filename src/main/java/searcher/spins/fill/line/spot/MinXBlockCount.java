package searcher.spins.fill.line.spot;

class MinXBlockCount {
    private int blockCount = 0;
    private int minX = Integer.MAX_VALUE;

    void incrementBlockCount() {
        blockCount += 1;
    }

    void updateMinX(int dx) {
        if (dx < minX) {
            minX = dx;
        }
    }

    int getBlockCount() {
        return blockCount;
    }

    int getMinX() {
        return minX;
    }
}
