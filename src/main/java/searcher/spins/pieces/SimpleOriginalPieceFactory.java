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
    private final int maxHeight;

    public SimpleOriginalPieceFactory(MinoFactory minoFactory, MinoShifter minoShifter, int maxHeight) {
        this(minoFactory, minoShifter, 10, maxHeight);
    }

    private SimpleOriginalPieceFactory(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int maxHeight) {
        SimpleOriginalPieceFactoryImpl factory = new SimpleOriginalPieceFactoryImpl(minoFactory, minoShifter, fieldWidth, maxHeight);
        this.originalPieces = factory.createList();
        this.maxHeight = maxHeight;
    }

    public AllSimpleOriginalPieces createAllPieces() {
        return new AllSimpleOriginalPieces(originalPieces, maxHeight);
    }

    public MinimalSimpleOriginalPieces createMinimalPieces(Field field) {
        List<SimpleOriginalPiece> minimalOriginalPieces = originalPieces.stream()
                .filter(originalPiece -> field.canMerge(originalPiece.getMinoField()))
                .collect(Collectors.toList());
        return new MinimalSimpleOriginalPieces(minimalOriginalPieces, maxHeight);
    }
}
