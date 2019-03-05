package searcher.spins.pieces;

import common.datastore.FullOperationWithKey;
import common.parser.AbstractAllOperationFactory;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.neighbor.SimpleOriginalPiece;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleOriginalPieceFactory {
    private static final int LEAST_MAX_HEIGHT = 7;

    private static class SimpleOriginalPieceFactoryImpl extends AbstractAllOperationFactory<SimpleOriginalPiece> {
        SimpleOriginalPieceFactoryImpl(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int fieldHeight) {
            super(minoFactory, minoShifter, fieldWidth, fieldHeight);
        }

        @Override
        protected SimpleOriginalPiece parseOperation(FullOperationWithKey operationWithKey, int upperY, int fieldHeight) {
            return new SimpleOriginalPiece(operationWithKey, fieldHeight);
        }
    }

    private final List<SimpleOriginalPiece> originalPieces;
    private final int maxHeightForAll;
    private final int maxHeightForMinimal;

    public SimpleOriginalPieceFactory(MinoFactory minoFactory, MinoShifter minoShifter, int maxTargetHeight) {
        this(minoFactory, minoShifter, 10, maxTargetHeight);
    }

    private SimpleOriginalPieceFactory(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int maxTargetHeight) {
        this.maxHeightForAll = LEAST_MAX_HEIGHT <= maxTargetHeight ? maxTargetHeight : LEAST_MAX_HEIGHT;

        SimpleOriginalPieceFactoryImpl factory = new SimpleOriginalPieceFactoryImpl(minoFactory, minoShifter, fieldWidth, maxHeightForAll);
        this.originalPieces = factory.createList();
        this.maxHeightForMinimal = maxTargetHeight + 1;
    }

    public AllSimpleOriginalPieces createAllPieces() {
        return new AllSimpleOriginalPieces(originalPieces, maxHeightForAll);
    }

    public MinimalSimpleOriginalPieces createMinimalPieces(Field field) {
        List<SimpleOriginalPiece> minimalOriginalPieces = originalPieces.stream()
                .filter(originalPiece -> field.canMerge(originalPiece.getMinoField()))
                .filter(originalPiece -> originalPiece.getY() + originalPiece.getMino().getMaxY() < maxHeightForMinimal)
                .collect(Collectors.toList());
        return new MinimalSimpleOriginalPieces(minimalOriginalPieces, maxHeightForMinimal);
    }
}
