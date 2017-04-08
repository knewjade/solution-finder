package searcher.common;

import core.mino.Block;
import core.srs.Rotate;
import searcher.common.action.Action;

public class History {
    private final int[] history;
    private final int index;

    public History(int max) {
        this.history = new int[max];
        this.index = 0;
    }

    private History(int[] history, int index) {
        this.history = history;
        this.index = index;
    }

    public History record(Block block, Action action) {
        return record(action.getX() + action.getY() * 10 + action.getRotate().getNumber() * 240 + block.getNumber() * 240 * 4);
    }

    private History record(int value) {
        int[] newArray = new int[history.length];
        System.arraycopy(history, 0, newArray, 0, index);
        newArray[index] = value;
        return new History(newArray, index + 1);
    }

    @Override
    public String toString() {
        if (history == null || history.length < 1)
            return "";

        String str = "";
        for (int history : history) {
            int x = history % 10;
            history /= 10;
            int y = history % 24;
            history /= 24;
            Rotate rotate = getRotate(history % 4);
            history /= 4;
            Block block = getBlock(history);
            str += String.format("%s %s %d,%d / ", block.name(), rotate.name(), x, y);
        }
        return str.substring(0, str.length() - 2);
    }

    private Rotate getRotate(int number) {
        for (Rotate rotate : Rotate.values())
            if (rotate.getNumber() == number)
                return rotate;
        throw new IllegalStateException("No reachable");
    }

    private Block getBlock(int number) {
        for (Block block : Block.values())
            if (block.getNumber() == number)
                return block;
        throw new IllegalStateException("No reachable");
    }

    public int getIndex() {
        return index;
    }
}
