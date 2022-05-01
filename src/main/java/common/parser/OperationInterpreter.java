package common.parser;

import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OperationInterpreter {
    public static Operations parseToOperations(String strings) {
        List<Operation> operationList = Arrays.stream(strings.split(";"))
                .map(OperationInterpreter::createOperation)
                .collect(Collectors.toList());
        return new Operations(operationList);
    }

    private static Operation createOperation(String strings) {
        String[] split = strings.split(",");
        assert split.length == 4;
        Piece piece = StringEnumTransform.toPiece(split[0].trim());
        Rotate rotate = StringEnumTransform.toRotate(split[1].trim());
        int x = Integer.parseInt(split[2].trim());
        int y = Integer.parseInt(split[3].trim());
        return new SimpleOperation(piece, rotate, x, y);
    }

    public static String parseToString(Operations operation) {
        return operation.getOperations().stream()
                .map(OperationInterpreter::parseToString)
                .collect(Collectors.joining(";"));
    }

    private static String parseToString(Operation operation) {
        return String.format("%s,%s,%d,%d",
                operation.getPiece().getName(),
                StringEnumTransform.toString(operation.getRotate()),
                operation.getX(),
                operation.getY()
        );
    }
}
