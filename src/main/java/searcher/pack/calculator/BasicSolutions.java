package searcher.pack.calculator;

import core.column_field.ColumnField;
import searcher.pack.mino_fields.MinoFields;

import java.util.function.Predicate;

public interface BasicSolutions {
    static Predicate<ColumnField> createBitCountPredicate(int minMemorizeBit) {
        return field -> minMemorizeBit <= Long.bitCount(field.getBoard(0));
    }

    MinoFields parse(ColumnField columnField);
}
