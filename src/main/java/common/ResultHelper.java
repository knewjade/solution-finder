package common;

import common.comparator.ResultPCFComparator;
import common.datastore.Operation;
import common.datastore.Result;
import common.datastore.SimpleOperation;
import common.datastore.action.Action;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ResultHelper {
    private static final ResultPCFComparator COMPARATOR = new ResultPCFComparator();

    public static List<Result> uniquify(List<Result> results) {
        TreeSet<Result> set = new TreeSet<>(COMPARATOR);
        set.addAll(results);
        return new ArrayList<>(set);
    }

    public static List<Operation> createOperations(Result result) {
        OperationHistory history = result.getOrder().getHistory();
        int[] operationNumbers = history.getOperationNumbers();

        // head to tail
        int max = history.getNextIndex();
        ArrayList<Operation> operations = new ArrayList<>();
        for (int index = 0; index < max; index++) {
            int value = operationNumbers[index];
            Operation operation = ActionParser.parseToOperation(value);
            operations.add(operation);
        }

        // last
        Block lastBlock = result.getLastBlock();
        Action action = result.getAction();
        SimpleOperation operation = new SimpleOperation(lastBlock, action.getRotate(), action.getX(), action.getY());
        operations.add(operation);
        return operations;
    }
}
