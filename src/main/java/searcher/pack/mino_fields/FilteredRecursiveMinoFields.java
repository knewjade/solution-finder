package searcher.pack.mino_fields;

import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_field.MinoField;
import searcher.pack.mino_field.RecursiveMinoField;

import java.util.stream.Stream;

public class FilteredRecursiveMinoFields implements RecursiveMinoFields {
    private final RecursiveMinoFields minoFields;
    private final SolutionFilter filter;

    public FilteredRecursiveMinoFields(RecursiveMinoFields minoFields, SolutionFilter filter) {
        this.minoFields = minoFields;
        this.filter = filter;
    }

    @Override
    public Stream<? extends MinoField> stream() {
        return recursiveStream();
    }

    @Override
    public Stream<RecursiveMinoField> recursiveStream() {
        return minoFields.recursiveStream().filter(filter::testMinoField);
    }
}
