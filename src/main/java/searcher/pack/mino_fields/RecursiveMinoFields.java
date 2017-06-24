package searcher.pack.mino_fields;

import searcher.pack.mino_field.RecursiveMinoField;

import java.util.stream.Stream;

public interface RecursiveMinoFields extends MinoFields {
    Stream<RecursiveMinoField> recursiveStream();
}
