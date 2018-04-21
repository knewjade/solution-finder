package _usecase.path.files;

import common.datastore.Operations;

import java.util.List;

public class PathCSV {
    private final List<Operations> operations;

    PathCSV(List<Operations> operations) {
        this.operations = operations;
    }

    public List<Operations> operations() {
        return operations;
    }
}
