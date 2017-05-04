package searcher.common;

import common.datastore.Operation;
import common.datastore.SimpleOperation;
import common.datastore.Operations;
import core.mino.Block;
import core.srs.Rotate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: unittest
public class OperationsFactory {
    public static Operations createOperations(String... strings) {
        List<Operation> operationList = Arrays.stream(strings)
                .map(OperationsFactory::createOperation)
                .collect(Collectors.toList());
        return new Operations(operationList);
    }

    private static Operation createOperation(String str) {
        String[] split = str.split(",");
        assert split.length == 4;
        Block block = getBlock(split[0].trim());
        Rotate rotate = getRotate(split[1].trim());
        int x = Integer.valueOf(split[2].trim());
        int y = Integer.valueOf(split[3].trim());
        return new SimpleOperation(block, rotate, x, y);
    }

    private static Block getBlock(String name) {
        switch (name) {
            case "T":
                return Block.T;
            case "S":
                return Block.S;
            case "Z":
                return Block.Z;
            case "I":
                return Block.I;
            case "O":
                return Block.O;
            case "J":
                return Block.J;
            case "L":
                return Block.L;
        }
        throw new IllegalArgumentException("No reachable");
    }

    private static Rotate getRotate(String name) {
        switch (name) {
            case "0":
                return Rotate.Spawn;
            case "L":
                return Rotate.Left;
            case "2":
                return Rotate.Reverse;
            case "R":
                return Rotate.Right;
        }
        throw new IllegalArgumentException("No reachable");
    }
}
