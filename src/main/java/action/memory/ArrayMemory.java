package action.memory;

public class ArrayMemory implements Memory {
    private final boolean[][] flags;

    public ArrayMemory(int height) {
        this.flags = new boolean[height][FIELD_WIDTH];
    }

    @Override
    public boolean get(int x, int y) {
        return flags[y][x];
    }

    @Override
    public void setTrue(int x, int y) {
        flags[y][x] = true;
    }

    @Override
    public void clear() {
        for (int y = 0; y < flags.length; y++)
            for (int x = 0; x < FIELD_WIDTH; x++)
                flags[y][x] = false;
    }
}
