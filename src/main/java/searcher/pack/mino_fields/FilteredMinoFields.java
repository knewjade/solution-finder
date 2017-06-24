package searcher.pack.mino_fields;

import searcher.pack.mino_field.MinoField;
import searcher.pack.memento.SolutionFilter;

import java.util.stream.Stream;

public class FilteredMinoFields implements MinoFields {
    private final MinoFields minoFields;
    private final SolutionFilter filter;

    public FilteredMinoFields(MinoFields minoFields, SolutionFilter filter) {
        this.minoFields = minoFields;
        this.filter = filter;
    }

    @Override
    public Stream<? extends MinoField> stream() {
        return minoFields.stream().filter(filter::testMinoField);
    }
}
