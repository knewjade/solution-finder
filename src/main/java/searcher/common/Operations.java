package searcher.common;

import java.util.List;

public class Operations implements Comparable<Operations> {
    private final List<Operation> operations;

    public Operations(List<Operation> operations) {
        this.operations = operations;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    @Override
    public int compareTo(Operations o) {
        int size = this.operations.size();
        int size1 = o.operations.size();
        if (size == size1) {
            for (int index = 0; index < size; index++) {
                int compare = Integer.compare(this.operations.get(index).toNumber(), o.operations.get(index).toNumber());
                if (compare != 0)
                    return compare;
            }
            return 0;
        } else {
            return Integer.compare(size, size1);
        }
    }

    @Override
    public String toString() {
        return "Operations{" +
                "operations=" + operations +
                '}';
    }
}
