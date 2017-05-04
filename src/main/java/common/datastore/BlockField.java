package common.datastore;

import common.FieldComparator;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;

import java.util.Comparator;
import java.util.EnumMap;

// TODO: unittest
public class BlockField implements Comparable<BlockField> {
    private static final Comparator<Field> FIELD_COMPARATOR = new FieldComparator();
    private static final Field EMPTY_FIELD = FieldFactory.createField(1);

    private final int height;
    private final EnumMap<Block, Field> map = new EnumMap<>(Block.class);

    public BlockField(int height) {
        this.height = height;
    }

    public void merge(Field field, Block block) {
        map.computeIfAbsent(block, b -> FieldFactory.createField(height)).merge(field);
    }

    @Override
    public int compareTo(BlockField o) {
        for (Block block : Block.values()) {
            Field field = this.map.getOrDefault(block, EMPTY_FIELD);
            Field oField = o.map.getOrDefault(block, EMPTY_FIELD);
            int compare = FIELD_COMPARATOR.compare(field, oField);
            if (compare != 0)
                return compare;
        }
        return 0;
    }

    public Field get(Block block) {
        return map.getOrDefault(block, EMPTY_FIELD);
    }
}
