package searcher.spins.scaffold.results;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.SpinCommons;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ScaffoldResultWithT extends ScaffoldResult {
    static List<SimpleOriginalPiece> extractAirOperations(Result result, SimpleOriginalPiece operationT, Stream<SimpleOriginalPiece> targetOperationStream) {
        // 地形からTミノを除く
        Field field = result.freezeAllMergedField();
        field.reduce(operationT.getMinoField());

        long filledLine = field.getFilledLine();

        return targetOperationStream
                .filter(operation -> SpinCommons.existsOnGround(field, filledLine, operation))
                .collect(Collectors.toList());
    }

    // Tミノを取得
    public abstract SimpleOriginalPiece getOperationT();

    abstract Field getNotAllowed();
}
