package searcher.common;

import java.util.List;

public class Operations implements Comparable<Operations> {
    private final List<Operation> operations;

    public Operations(List<Operation> operations) {
        assert operations != null;
        this.operations = operations;
    }

    public List<Operation> getOperations() {
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
        int size = this.operations.size();
        int oSize = o.operations.size();
        if (size == oSize) {
            for (int index = 0; index < size; index++) {
                int compare = operations.get(index).compareTo(o.operations.get(index));
                if (compare != 0)
                    return compare;
            }
            return 0;
        } else {
            return Integer.compare(size, oSize);
        }
    }

    @Override
    public String toString() {
        return "Operations{" +
                "operations=" + operations +
                '}';
    }
}
