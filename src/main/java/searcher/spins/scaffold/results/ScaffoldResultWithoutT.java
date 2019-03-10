package searcher.spins.scaffold.results;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.SpinCommons;
import searcher.spins.results.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ScaffoldResultWithoutT extends ScaffoldResult {
    // すべてのミノが地面 or 他のミノの上にあるか
    static List<SimpleOriginalPiece> extractAirOperations(Result result, Stream<SimpleOriginalPiece> targetOperationStream) {
        long filledLine = result.getAllMergedFilledLine();
        Field field = result.getAllMergedField();
        long onePieceFilledKey = result.getOnePieceFilledKey();
        return targetOperationStream
                .filter(operation -> {
                    long l = filledLine & operation.getMinoField().getFilledLine();
                    return !SpinCommons.existsOnGround(result.getInitField(), field, filledLine, onePieceFilledKey, operation);
                })
                .collect(Collectors.toList());
    }
}
