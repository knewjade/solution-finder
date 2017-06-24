package searcher.pack.mino_fields;

import searcher.pack.mino_field.MinoField;

import java.util.stream.Stream;

public class EmptyMinoFields implements MinoFields {
    @Override
    public Stream<? extends MinoField> stream() {
        return Stream.empty();
    }
}
