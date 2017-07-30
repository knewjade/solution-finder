package common.datastore;

import common.comparator.OperationListComparator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Operations implements Comparable<Operations> {
    private final List<? extends Operation> operations;

    public Operations(Stream<? extends Operation> operationStream) {
        this(operationStream.collect(Collectors.toList()));
    }

    public Operations(List<? extends Operation> operations) {
        assert operations != null;
        this.operations = operations;
    }

    public List<? extends Operation> getOperations() {
        return operations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operations that = (Operations) o;
        return operations.equals(that.operations);
    }

    @Override
    public int hashCode() {
        return operations.hashCode();
    }

    @Override
    public int compareTo(Operations o) {
        return OperationListComparator.compareOperation(this.operations, o.operations);
    }

    @Override
    public String toString() {
        return "Operations{" +
                "operations=" + operations +
                '}';
    }
}
