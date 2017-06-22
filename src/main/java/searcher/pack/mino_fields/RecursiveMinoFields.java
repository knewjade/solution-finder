package searcher.pack.mino_fields;

import searcher.pack.MinoField;
import searcher.pack.RecursiveMinoField;

import java.util.ArrayList;
import java.util.stream.Stream;

public class RecursiveMinoFields implements MinoFields {
    private final ArrayList<RecursiveMinoField> minoFields;

    public RecursiveMinoFields(ArrayList<RecursiveMinoField> minoFields) {
        this.minoFields = minoFields;
    }

    @Override
    public Stream<? extends MinoField> stream() {
        return recursiveStream();
    }

    public Stream<RecursiveMinoField> recursiveStream() {
        return minoFields.stream();
    }
}
