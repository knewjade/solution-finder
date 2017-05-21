package _experimental.allcomb.memento;

import _experimental.allcomb.MinoField;
import _experimental.allcomb.SlideXOperationWithKey;
import common.datastore.IOperationWithKey;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MinoFieldMemento {
    private static final List<IOperationWithKey> EMPTY_LIST = Collections.emptyList();

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
        long sum = 0L;
        switch (index) {
            case 4:
                if (minoField4 != null)
                    sum += minoField4.getBlockCounter().getCounter();
            case 3:
                if (minoField3 != null)
                    sum += minoField3.getBlockCounter().getCounter();
            case 2:
                if (minoField2 != null)
                    sum += minoField2.getBlockCounter().getCounter();
            case 1:
                if (minoField1 != null)
                    sum += minoField1.getBlockCounter().getCounter();
            case 0:
                return sum;
        }
        throw new IllegalStateException("No reachable");
    }

    public LinkedList<IOperationWithKey> getRawOperations() {
        LinkedList<IOperationWithKey> operations = new LinkedList<>();
        switch (index) {
            case 4:
                if (minoField4 != null)
                    operations.addAll(minoField4.getOperations());
            case 3:
                if (minoField3 != null)
                    operations.addAll(minoField3.getOperations());
            case 2:
                if (minoField2 != null)
                    operations.addAll(minoField2.getOperations());
            case 1:
                if (minoField1 != null)
                    operations.addAll(minoField1.getOperations());
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

    public LinkedList<IOperationWithKey> getOperations() {
        LinkedList<IOperationWithKey> list = new LinkedList<>();
        switch (index) {
            case 4:
                if (minoField4 != null)
                    addSlideX(list, minoField4.getOperations(), 9);
            case 3:
                if (minoField3 != null)
                    addSlideX(list, minoField3.getOperations(), 6);
            case 2:
                if (minoField2 != null)
                    addSlideX(list, minoField2.getOperations(), 3);
            case 1:
                if (minoField1 != null)
                    addSlideX(list, minoField1.getOperations(), 0);
            case 0:
                return list;
        }
        throw new IllegalStateException("No reachable");
    }

    private void addSlideX(LinkedList<IOperationWithKey> list, List<IOperationWithKey> operations, int slideX) {
        for (IOperationWithKey operation : operations)
            list.add(toSlideWrapper(operation, slideX));
    }

    private IOperationWithKey toSlideWrapper(IOperationWithKey operationWithKey, int slideX) {
        return new SlideXOperationWithKey(operationWithKey, slideX);
    }
}
