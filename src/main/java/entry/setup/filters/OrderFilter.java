package entry.setup.filters;

import common.buildup.BuildUpStream;
import common.datastore.MinoOperationWithKey;
import common.datastore.blocks.LongPieces;
import core.field.Field;
import entry.path.ValidPiecesPool;

import java.util.HashSet;

public class OrderFilter implements SetupSolutionFilter {
    private final ThreadLocal<BuildUpStream> buildUpStreamThreadLocal;
    private final ValidPiecesPool validPiecesPool;
    private final Field initField;

    public OrderFilter(ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, ValidPiecesPool validPiecesPool, Field initField) {
        this.buildUpStreamThreadLocal = buildUpStreamThreadLocal;
        this.validPiecesPool = validPiecesPool;
        this.initField = initField;
    }

    @Override
    public boolean test(SetupResult result) {
        HashSet<LongPieces> validPieces = validPiecesPool.getValidPieces();
        BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
        return buildUpStream.existsValidBuildPattern(initField, result.getSolution())
                .map(operations -> new LongPieces(operations.stream().map(MinoOperationWithKey::getPiece)))
                .anyMatch(validPieces::contains);
    }
}