package entry.setup.functions;

import common.datastore.MinoOperationWithKey;
import common.datastore.blocks.LongPieces;
import core.field.Field;
import core.mino.Piece;
import entry.setup.filters.CombinationFilter;
import entry.setup.filters.SetupSolutionFilter;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class CombinationFunctions implements SetupFunctions {
    @Override
    public SetupSolutionFilter getSetupSolutionFilter() {
        return new CombinationFilter();
    }

    @Override
    public BiFunction<List<MinoOperationWithKey>, Field, String> getNaming() {
        return (operationWithKeys, field) -> {
            LongPieces pieces = new LongPieces(operationWithKeys.stream().map(MinoOperationWithKey::getPiece));
            return pieces.blockStream()
                    .map(Piece::getName)
                    .collect(Collectors.joining());
        };
    }
}