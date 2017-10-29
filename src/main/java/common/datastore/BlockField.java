package common.datastore;

import common.comparator.FieldComparator;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class BlockField implements Comparable<BlockField> {
    private static final Comparator<Field> FIELD_COMPARATOR = new FieldComparator();
    private static final Field EMPTY_FIELD = FieldFactory.createField(1);

    private final int height;
    private final EnumMap<Piece, Field> map;

    public BlockField(int height) {
        this(height, new EnumMap<>(Piece.class));
    }

    private BlockField(int height, EnumMap<Piece, Field> map) {
        this.height = height;
        this.map = map;
    }

    // TODO: write unittest
    public void setBlock(Piece piece, int x, int y) {
        assert piece != null;
        map.computeIfAbsent(piece, b -> FieldFactory.createField(height)).setBlock(x, y);
    }

    public void merge(Field field, Piece piece) {
        map.computeIfAbsent(piece, b -> FieldFactory.createField(height)).merge(field);
    }

    public Field get(Piece piece) {
        return map.getOrDefault(piece, EMPTY_FIELD);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockField that = (BlockField) o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public int compareTo(BlockField o) {
        for (Piece piece : Piece.values()) {
            Field field = this.map.getOrDefault(piece, EMPTY_FIELD);
            Field oField = o.map.getOrDefault(piece, EMPTY_FIELD);
            int compare = FIELD_COMPARATOR.compare(field, oField);
            if (compare != 0)
                return compare;
        }
        return 0;
    }

    public boolean containsAll(BlockField target) {
        for (Map.Entry<Piece, Field> targetEntry : target.map.entrySet()) {
            Piece key = targetEntry.getKey();
            Field targetField = targetEntry.getValue();
            Field myField = this.get(key);
            if (!myField.contains(targetField)) {
                return false;
            }
        }
        return true;
    }

    // TODO: write unittest
    public Piece getBlock(int x, int y) {
        for (Map.Entry<Piece, Field> entry : map.entrySet()) {
            Field field = entry.getValue();
            if (!field.isEmpty(x, y))
                return entry.getKey();
        }
        return null;
    }

    public int getHeight() {
        return height;
    }
}
