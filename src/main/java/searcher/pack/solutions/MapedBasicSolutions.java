package searcher.pack.solutions;

import core.column_field.ColumnField;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.EmptyMinoFields;
import searcher.pack.mino_fields.FilteredMinoFields;
import searcher.pack.mino_fields.MinoFields;

import java.util.HashMap;
import java.util.Map;

public class MapedBasicSolutions implements BasicSolutions {
    private static final MinoFields EMPTY_MINO_FIELDS = new EmptyMinoFields();

    private final Map<ColumnField, ? extends MinoFields> solutions;

    public MapedBasicSolutions(Map<ColumnField, ? extends MinoFields> solutions) {
        assert solutions != null;
        this.solutions = solutions;
    }

    public MapedBasicSolutions(Map<ColumnField, ? extends MinoFields> solutions, SolutionFilter filter) {
        HashMap<ColumnField, MinoFields> filtered = new HashMap<>();
        for (Map.Entry<ColumnField, ? extends MinoFields> entry : solutions.entrySet()) {
            FilteredMinoFields minoFields = new FilteredMinoFields(entry.getValue(), filter);
            filtered.put(entry.getKey(), minoFields);
        }
        this.solutions = filtered;
    }

    @Override
    public MinoFields parse(ColumnField columnField) {
        MinoFields minoFields = solutions.get(columnField);
        return minoFields != null ? minoFields : EMPTY_MINO_FIELDS;
    }

    public Map<ColumnField, ? extends MinoFields> getSolutions() {
        return solutions;
    }
}
