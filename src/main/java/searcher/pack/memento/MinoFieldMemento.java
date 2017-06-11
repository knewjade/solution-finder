package searcher.pack.memento;

import common.datastore.OperationWithKey;
import searcher.pack.MinoField;
import searcher.pack.SlideXOperationWithKey;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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

    public long getSumBlockCounter() {
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

    public Stream<OperationWithKey> getRawOperationsStream() {
        Stream<OperationWithKey> operations = Stream.empty();
        switch (index) {
            case 4:
                if (minoField4 != null)
                    operations = Stream.concat(operations, minoField4.getOperationsStream());
            case 3:
                if (minoField3 != null)
                    operations = Stream.concat(operations, minoField3.getOperationsStream());
            case 2:
                if (minoField2 != null)
                    operations = Stream.concat(operations, minoField2.getOperationsStream());
            case 1:
                if (minoField1 != null)
                    operations = Stream.concat(operations, minoField1.getOperationsStream());
            case 0:
                return operations;
        }
        throw new IllegalStateException("No reachable");
    }

    public boolean isConcat() {
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

    public Stream<OperationWithKey> getOperationsStream() {
        Stream<OperationWithKey> operations = Stream.empty();
        switch (index) {
            case 4:
                if (minoField4 != null)
                    operations = Stream.concat(operations, addSlideX(minoField4.getOperationsStream(), 9));
            case 3:
                if (minoField3 != null)
                    operations = Stream.concat(operations, addSlideX(minoField3.getOperationsStream(), 6));
            case 2:
                if (minoField2 != null)
                    operations = Stream.concat(operations, addSlideX(minoField2.getOperationsStream(), 3));
            case 1:
                if (minoField1 != null)
                    operations = Stream.concat(operations, addSlideX(minoField1.getOperationsStream(), 0));
            case 0:
                return operations;
        }
        throw new IllegalStateException("No reachable");
    }

    private Stream<OperationWithKey> addSlideX(Stream<OperationWithKey> operations, int slideX) {
        return operations.map(operationWithKey -> toSlideWrapper(operationWithKey, slideX));
    }

    private OperationWithKey toSlideWrapper(OperationWithKey operationWithKey, int slideX) {
        return new SlideXOperationWithKey(operationWithKey, slideX);
    }
}
