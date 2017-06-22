package searcher.pack.mino_fields;

import searcher.pack.RecursiveMinoField;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ListMinoFields implements MinoFields {
    private final ArrayList<RecursiveMinoField> minoFields;

    public ListMinoFields(ArrayList<RecursiveMinoField> minoFields) {
        this.minoFields = minoFields;
    }

    @Override
    public Stream<RecursiveMinoField> stream() {
        return minoFields.stream();
    }
}
