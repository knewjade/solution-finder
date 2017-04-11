package action.cache.memory;

public class SmallMemory implements Memory {
    private long flags = 0L;

    @Override
    public boolean get(int x, int y) {
        return (flags & getMask(x, y)) != 0L;
    }

    @Override
    public void setTrue(int x, int y) {
        flags |= getMask(x, y);
    }

    private long getMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    @Override
    public void clear() {
        this.flags = 0L;
    }
}
