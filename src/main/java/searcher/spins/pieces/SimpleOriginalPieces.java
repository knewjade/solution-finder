package searcher.spins.pieces;

import common.comparator.FieldComparator;
import common.datastore.OperationWithKey;
import core.field.Field;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimpleOriginalPieces {
    public static SimpleOriginalPieces create(AllSimpleOriginalPieces allSimpleOriginalPieces) {
        List<SimpleOriginalPiece> originalPieces = allSimpleOriginalPieces.getOriginalPieces();
        Map<Long, SimpleOriginalPiece> keyToPiece = createKeyToPiece(originalPieces);
        Map<Field, SimpleOriginalPiece> fieldToPiece = createFieldToPiece(originalPieces);
        return new SimpleOriginalPieces(keyToPiece, fieldToPiece, allSimpleOriginalPieces.getMaxHeight());
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
    private final int maxHeight;

    private SimpleOriginalPieces(
            Map<Long, SimpleOriginalPiece> keyToPiece,
            Map<Field, SimpleOriginalPiece> fieldToPiece,
            int maxHeight
    ) {
        this.keyToPiece = keyToPiece;
        this.fieldToPiece = fieldToPiece;
        this.maxHeight = maxHeight;
    }

    public SimpleOriginalPiece get(Piece piece, Rotate rotate, int x, int y) {
        long key = OperationWithKey.toUniqueKey(piece, rotate, x, y);
        return keyToPiece.get(key);
    }

    public SimpleOriginalPiece get(Field field) {
        return fieldToPiece.get(field);
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}
