package common.parser;

import common.datastore.FullOperationWithKey;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OperationWithKeyInterpreter {
    public static String parseToString(List<? extends OperationWithKey> operations) {
        return parseToString(operations.stream());
    }

    public static String parseToString(Stream<? extends OperationWithKey> operations) {
        return operations
                .map(OperationWithKeyInterpreter::parseToString)
                .collect(Collectors.joining(";"));
    }

    private static String parseToString(OperationWithKey operation) {
        return String.format("%s,%s,%d,%d,%d,%d",
                operation.getPiece().getName(),
                StringEnumTransform.toString(operation.getRotate()),
                operation.getX(),
                operation.getY(),
                KeyOperators.toColumnKey(operation.getNeedDeletedKey()),
                KeyOperators.toColumnKey(operation.getUsingKey())
        );
    }

    public static List<MinoOperationWithKey> parseToList(String operations, MinoFactory minoFactory) {
        return parseToStream(operations, minoFactory).collect(Collectors.toList());
    }

    public static Stream<MinoOperationWithKey> parseToStream(String operations, MinoFactory minoFactory) {
        return Arrays.stream(operations.split(";"))
                .map(s -> s.split(","))
                .map(strings -> {
                    Piece piece = StringEnumTransform.toPiece(strings[0]);
                    Rotate rotate = StringEnumTransform.toRotate(strings[1]);
                    Mino mino = minoFactory.create(piece, rotate);
                    int x = Integer.parseInt(strings[2]);
                    int y = Integer.parseInt(strings[3]);
                    long deleteKey = KeyOperators.toBitKey(Long.parseLong(strings[4]));
                    long usingKey = KeyOperators.toBitKey(Long.parseLong(strings[5]));
                    return new FullOperationWithKey(mino, x, y, deleteKey, usingKey);
                });
    }

    public static String parseToStringSimple(OperationWithKey operation) {
        return String.format("%s,%d,%d,%d",
                StringEnumTransform.toString(operation.getRotate()),
                operation.getX(),
                operation.getY(),
                KeyOperators.toColumnKey(operation.getNeedDeletedKey())
        );
    }
}
