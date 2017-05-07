package common;

import common.datastore.Operation;
import common.datastore.Operations;
import core.srs.Rotate;

import java.util.stream.Collectors;

public class OperationHelper {
    public static String parseToString(Operations operation) {
        return operation.getOperations().stream()
                .map(OperationHelper::parseToString)
                .collect(Collectors.joining(";"));
    }

    private static String parseToString(Operation operation) {
        return String.format("%s,%s,%d,%d",
                operation.getBlock().getName(),
                parseRotateToString(operation.getRotate()),
                operation.getX(),
                operation.getY()
        );
    }

    private static String parseRotateToString(Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return "0";
            case Left:
                return "L";
            case Reverse:
                return "2";
            case Right:
                return "R";
        }
        throw new IllegalStateException("No reachable");
    }

}
