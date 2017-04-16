package searcher.common;

import core.mino.Block;
import searcher.common.action.Action;

public class OperationHistory {
    private final int[] operationNumbers;
    private final int index;

    public OperationHistory(int max) {
        this.operationNumbers = new int[max];
        this.index = 0;
    }

    private OperationHistory(int[] history, int index) {
        this.operationNumbers = history;
        this.index = index;
    }

    OperationHistory record(Block block, Action action) {
        return record(ActionParser.parseToInt(block, action));
    }

    private OperationHistory record(int value) {
        int[] newArray = new int[operationNumbers.length];
        System.arraycopy(operationNumbers, 0, newArray, 0, index);
        newArray[index] = value;
        return new OperationHistory(newArray, index + 1);
    }

    @Override
    public String toString() {
        if (operationNumbers == null || operationNumbers.length < 1)
            return "";

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < index; i++) {
            int history = operationNumbers[i];
            Operation operation = ActionParser.parseToOperation(history);
            str.append(operation).append(" / ");
        }
        return str.substring(0, str.length() - 3);
    }

    int[] getOperationNumbers() {
        return operationNumbers;
    }

    public int getIndex() {
        return index;
    }
}
