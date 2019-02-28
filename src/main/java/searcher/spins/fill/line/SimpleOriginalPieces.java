package searcher.spins.fill.line;

import common.comparator.FieldComparator;
import common.datastore.OperationWithKey;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;
import searcher.spins.AllSimpleOriginalPieces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimpleOriginalPieces {
    public static SimpleOriginalPieces create(MinoFactory minoFactory, MinoShifter minoShifter, int maxTargetHeight) {
        AllSimpleOriginalPieces allSimpleOriginalPieces = new AllSimpleOriginalPieces(minoFactory, minoShifter, 10, maxTargetHeight);
        List<SimpleOriginalPiece> originalPieces = allSimpleOriginalPieces.createList();
        return create(originalPieces, maxTargetHeight);
    }

    private static SimpleOriginalPieces create(List<SimpleOriginalPiece> originalPieces, int maxTargetHeight) {
        Map<Long, SimpleOriginalPiece> keyToPiece = createKeyToPiece(originalPieces);
        Map<Field, SimpleOriginalPiece> fieldToPiece = createFieldToPiece(originalPieces);
        return new SimpleOriginalPieces(keyToPiece, fieldToPiece, maxTargetHeight);
    }

    private static Map<Long, SimpleOriginalPiece> createKeyToPiece(List<SimpleOriginalPiece> originalPieces) {
        Map<Long, SimpleOriginalPiece> keyToPiece = new HashMap<>();
        for (SimpleOriginalPiece originalPiece : originalPieces) {
            long key = originalPiece.toUniqueKey();
            assert !keyToPiece.containsKey(key) : originalPiece;
            keyToPiece.put(key, originalPiece);
        }
        return keyToPiece;
    }

    private static Map<Field, SimpleOriginalPiece> createFieldToPiece(List<SimpleOriginalPiece> originalPieces) {
        FieldComparator fieldComparator = new FieldComparator();
        Map<Field, SimpleOriginalPiece> fieldToPiece = new TreeMap<>(fieldComparator);
        for (SimpleOriginalPiece originalPiece : originalPieces) {
            Field field = originalPiece.getMinoField();
            assert !fieldToPiece.containsKey(field) : originalPiece;
            fieldToPiece.put(field, originalPiece);
        }
        return fieldToPiece;
    }

    private final Map<Long, SimpleOriginalPiece> keyToPiece;
    private final Map<Field, SimpleOriginalPiece> fieldToPiece;
    private final int maxTargetHeight;

    private SimpleOriginalPieces(
            Map<Long, SimpleOriginalPiece> keyToPiece,
            Map<Field, SimpleOriginalPiece> fieldToPiece,
            int maxTargetHeight
    ) {
        this.keyToPiece = keyToPiece;
        this.fieldToPiece = fieldToPiece;
        this.maxTargetHeight = maxTargetHeight;
    }

    public SimpleOriginalPiece get(Piece piece, Rotate rotate, int x, int y) {
        long key = OperationWithKey.toUniqueKey(piece, rotate, x, y);
        return keyToPiece.get(key);
    }

    public SimpleOriginalPiece get(Field field) {
        return fieldToPiece.get(field);
    }

    public int getMaxTargetHeight() {
        return maxTargetHeight;
    }
}
