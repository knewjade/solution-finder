package action.memory;

public interface Memory {
    int FIELD_WIDTH = 10;

    boolean get(int x, int y);

    void setTrue(int x, int y);

    void clear();
}
