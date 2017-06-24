package searcher.pack.mino_fields;

import searcher.pack.mino_field.MinoField;

import java.util.stream.Stream;

public interface MinoFields {
    Stream<? extends MinoField> stream();
}
