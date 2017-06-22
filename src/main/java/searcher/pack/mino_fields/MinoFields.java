package searcher.pack.mino_fields;

import searcher.pack.MinoField;
import searcher.pack.RecursiveMinoField;

import java.util.stream.Stream;

public interface MinoFields {
    Stream<? extends MinoField> stream();
}
