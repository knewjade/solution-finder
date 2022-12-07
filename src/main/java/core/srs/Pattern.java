package core.srs;

import java.util.Arrays;

public class Pattern {
    public static Pattern noPrivilegeSpins(int[][] offsets) {
        return new Pattern(offsets, new boolean[offsets.length]);
    }

    private final int[][] offsets;
    private final boolean[] privilegeSpins;

    /**
     * @param offsets        テストパターンごとに、ミノの移動量を表す配列（[x, y]）
     * @param privilegeSpins テストパターンごとで、スピンをMiniからRegularに昇格するパターンを`true`で表す配列
     */
    public Pattern(int[][] offsets, boolean[] privilegeSpins) {
        assert offsets.length == privilegeSpins.length;
        this.offsets = offsets;
        this.privilegeSpins = privilegeSpins;
    }

    int[][] getOffsets() {
        return offsets;
    }

    boolean isPrivilegeSpinsAt(int index) {
        assert 0 <= index && index < privilegeSpins.length : index;
        return privilegeSpins[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        return Arrays.deepEquals(offsets, pattern.offsets) && Arrays.equals(privilegeSpins, pattern.privilegeSpins);
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(offsets);
        result = 31 * result + Arrays.hashCode(privilegeSpins);
        return result;
    }
}
