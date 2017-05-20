package _experimental.allcomb.memento;

import _experimental.allcomb.MinoField;
import common.datastore.OperationWithKey;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MinoFieldMemento {
    private static final List<OperationWithKey> EMPTY_LIST = Collections.emptyList();

    private final MinoField minoField1;
    private final MinoField minoField2;
    private final MinoField minoField3;
    private final MinoField minoField4;
    private final int index;

    public MinoFieldMemento() {
        this(null, null, null, null, 0);
    }

    private MinoFieldMemento(MinoField minoField1) {
        this(minoField1, null, null, null, 1);
    }

    private MinoFieldMemento(MinoField minoField1, MinoField minoField2) {
        this(minoField1, minoField2, null, null, 2);
    }

    private MinoFieldMemento(MinoField minoField1, MinoField minoField2, MinoField minoField3) {
        this(minoField1, minoField2, minoField3, null, 3);
    }

    private MinoFieldMemento(MinoField minoField1, MinoField minoField2, MinoField minoField3, MinoField minoField4) {
        this(minoField1, minoField2, minoField3, minoField4, 4);
    }

    private MinoFieldMemento(MinoField minoField1, MinoField minoField2, MinoField minoField3, MinoField minoField4, int index) {
        this.minoField1 = minoField1;
        this.minoField2 = minoField2;
        this.minoField3 = minoField3;
        this.minoField4 = minoField4;
        this.index = index;
    }

    public MinoFieldMemento concat(MinoField minoField) {
        switch (index) {
            case 0:
                return new MinoFieldMemento(minoField);
            case 1:
                return new MinoFieldMemento(minoField1, minoField);
            case 2:
                return new MinoFieldMemento(minoField1, minoField2, minoField);
            case 3:
                return new MinoFieldMemento(minoField1, minoField2, minoField3, minoField);
        }
        throw new IllegalStateException("No reachable");
    }

    public MinoFieldMemento skip() {
        return concat(null);
    }

    long getSumBlockCounter() {
        switch (index) {
            case 0:
                return 0L;
            case 1:
                return getCounter1();
            case 2:
                return getCounter1() + getCounter2();
            case 3:
                return getCounter1() + getCounter2() + getCounter3();
            case 4:
                return getCounter1() + getCounter2() + getCounter3() + getCounter4();
        }
        throw new IllegalStateException("No reachable");
    }

    private long getCounter1() {
        return minoField1 == null ? 0L : minoField1.getBlockCounter().getCounter();
    }

    private long getCounter2() {
        return minoField2 == null ? 0L : minoField2.getBlockCounter().getCounter();
    }

    private long getCounter3() {
        return minoField3 == null ? 0L : minoField3.getBlockCounter().getCounter();
    }

    private long getCounter4() {
        return minoField4 == null ? 0L : minoField4.getBlockCounter().getCounter();
    }

    private List<OperationWithKey> getOperations1() {
        return minoField1 == null ? EMPTY_LIST : minoField1.getOperations();
    }

    private List<OperationWithKey> getOperations2() {
        return minoField2 == null ? EMPTY_LIST : minoField2.getOperations();
    }

    private List<OperationWithKey> getOperations3() {
        return minoField3 == null ? EMPTY_LIST : minoField3.getOperations();
    }

    private List<OperationWithKey> getOperations4() {
        return minoField4 == null ? EMPTY_LIST : minoField4.getOperations();
    }

    LinkedList<OperationWithKey> getRawOperations() {
        LinkedList<OperationWithKey> operations = new LinkedList<>();
        switch (index) {
            case 4:
                operations.addAll(getOperations4());
            case 3:
                operations.addAll(getOperations3());
            case 2:
                operations.addAll(getOperations2());
            case 1:
                operations.addAll(getOperations1());
            case 0:
                return operations;
        }
        throw new IllegalStateException("No reachable");
    }

    boolean isConcat() {
        switch (index) {
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                return minoField1 != null && minoField2 != null;
            case 3:
                if (minoField1 == null)
                    return minoField2 != null && minoField3 != null;
                return minoField2 != null || minoField3 != null;
            case 4:
                if (minoField1 == null) {
                    if (minoField2 == null)
                        return minoField3 != null && minoField4 != null;
                    return minoField3 != null || minoField4 != null;
                } else {
                    return minoField2 != null || minoField3 != null || minoField4 != null;
                }
        }
        throw new IllegalStateException("No reachable");
    }

    @Override
    public String toString() {
        return "MinoFieldMemento{" +
                "minoField1=" + minoField1 +
                ", minoField2=" + minoField2 +
                ", minoField3=" + minoField3 +
                ", index=" + index +
                '}';
    }

    public int getIndex() {
        return index;
    }
}
