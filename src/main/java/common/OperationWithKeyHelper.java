package common;

import common.datastore.OperationWithKey;
import core.mino.Mino;
import core.srs.Rotate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OperationWithKeyHelper {
    public static String parseToString(List<? extends OperationWithKey> operation) {
        return operation.stream()
                .map(OperationWithKeyHelper::parseToString)
                .collect(Collectors.joining(";"));
    }

    public static String parseToString(OperationWithKey operation) {
        Mino mino = operation.getMino();
        return String.format("%s,%s,%d,%d,%d,%d",
                mino.getBlock().getName(),
                parseRotateToString(mino.getRotate()),
                operation.getX(),
                operation.getY(),
                operation.getNeedDeletedKey(),
                operation.getUsingKey()
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
