package _usecase;

import common.datastore.Operations;

import java.util.List;

class PathCSV {
    private final List<Operations> operations;

    PathCSV(List<Operations> operations) {
        this.operations = operations;
    }

    List<Operations> operations() {
        return operations;
    }
}
