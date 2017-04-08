package core.srs;

class Offset {
    private final int[][] offsets;

    Offset(int[][] offsets) {
        this.offsets = offsets;
    }

    Pattern to(Offset to) {
        int[][] pattern = new int[offsets.length][offsets[0].length];

        for (int index = 0; index < pattern.length; index++)
            for (int position = 0; position < pattern[index].length; position++)
                pattern[index][position] = this.offsets[index][position] - to.offsets[index][position];

        return new Pattern(pattern);
    }
}
