package entry.setup.functions;

import common.buildup.BuildUpStream;
import common.datastore.MinoOperationWithKey;
import common.datastore.blocks.LongPieces;
import core.field.Field;
import core.mino.Piece;
import entry.path.ValidPiecesPool;
import entry.setup.filters.OrderFilter;
import entry.setup.filters.SetupSolutionFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class OrderFunctions implements SetupFunctions {
    private final ThreadLocal<BuildUpStream> buildUpStreamThreadLocal;
    private final ValidPiecesPool validPiecesPool;
    private final Field initField;

    public OrderFunctions(ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, ValidPiecesPool validPiecesPool, Field initField) {
        this.buildUpStreamThreadLocal = buildUpStreamThreadLocal;
        this.validPiecesPool = validPiecesPool;
        this.initField = initField;
    }

    @Override
    public SetupSolutionFilter getSetupSolutionFilter() {
        return new OrderFilter(buildUpStreamThreadLocal, validPiecesPool, initField);
    }

    @Override
    public BiFunction<List<MinoOperationWithKey>, Field, String> getNaming() {
        return (operationWithKeys, field) -> {
            HashSet<LongPieces> validPieces = validPiecesPool.getValidPieces();
            BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
            Optional<LongPieces> sample = buildUpStream.existsValidBuildPattern(field, operationWithKeys)
                    .map(operations -> new LongPieces(operations.stream().map(MinoOperationWithKey::getPiece)))
                    .filter(validPieces::contains)
                    .findFirst();

            LongPieces pieces = sample.orElseGet(() -> new LongPieces(operationWithKeys.stream().map(MinoOperationWithKey::getPiece)));

            return pieces.blockStream()
                    .map(Piece::getName)
                    .collect(Collectors.joining());
        };
    }
}