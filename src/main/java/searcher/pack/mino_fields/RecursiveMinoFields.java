package searcher.pack.mino_fields;

import searcher.pack.RecursiveMinoField;

import java.util.stream.Stream;

public interface RecursiveMinoFields extends MinoFields {
    Stream<RecursiveMinoField> recursiveStream();
}
