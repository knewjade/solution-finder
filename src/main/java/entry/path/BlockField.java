package entry.path;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import misc.FieldComparator;
import searcher.common.Operations;

import java.util.Comparator;
import java.util.EnumMap;

class BlockField implements Comparable<BlockField> {
    private static final Comparator<Field> FIELD_COMPARATOR = new FieldComparator();
    private static final Field EMPTY_FIELD = FieldFactory.createField(1);

    private final Operations operations;
    private final int maxClearLine;
    private final EnumMap<Block, Field> map = new EnumMap<>(Block.class);

    BlockField(Operations operations, int maxClearLine) {
        this.operations = operations;
        this.maxClearLine = maxClearLine;
    }

    void merge(Field field, Block block) {
        map.computeIfAbsent(block, b -> FieldFactory.createField(maxClearLine)).merge(field);
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

    Operations getOperations() {
        return operations;
    }
}