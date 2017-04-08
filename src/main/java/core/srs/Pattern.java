package core.srs;

class Pattern {
    private final int[][] offsets;

    Pattern(int[][] offsets) {
        this.offsets = offsets;
    }

    int[][] getOffsets() {
        return offsets;
    }
}
