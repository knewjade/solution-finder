package newfield.step3;

public class XHistory {
    private final int[] history;
    private final int nextIndex;

     XHistory(int max) {
        this.history = new int[max];
        this.nextIndex = 0;
    }

    private XHistory(int[] history, int nextIndex) {
        this.history = history;
        this.nextIndex = nextIndex;
    }

     XHistory recordAndReturnNew(int x) {
        int[] newArray = new int[history.length];
        System.arraycopy(history, 0, newArray, 0, nextIndex);
        newArray[nextIndex] = x;
        return new XHistory(newArray, nextIndex + 1);
    }

    int[] getHistory() {
        return history;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    @Override
    public String toString() {
        if (history == null || history.length < 1)
            return "";

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < nextIndex; i++) {
            int history = this.history[i];
            str.append(history).append(" / ");
        }
        return str.substring(0, str.length() - 3);
    }
}
