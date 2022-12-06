package core.srs;

import java.util.Arrays;

public class Pattern {
    private final int[][] offsets;

    public Pattern(int[][] offsets) {
        this.offsets = offsets;
    }

    int[][] getOffsets() {
        return offsets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        return Arrays.deepEquals(offsets, pattern.offsets);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(offsets);
    }
}
