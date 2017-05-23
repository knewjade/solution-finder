package _experimental.allcomb.solutions;

import _experimental.allcomb.MinoField;
import _experimental.allcomb.memento.MementoFilter;
import core.column_field.ColumnField;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicSolutions {
    private final Map<ColumnField, Set<MinoField>> solutions;

    public BasicSolutions(Map<ColumnField, Set<MinoField>> solutions) {
        this.solutions = solutions;
    }

    public BasicSolutions(Map<ColumnField, Set<MinoField>> solutions, MementoFilter filter) {
        HashMap<ColumnField, Set<MinoField>> filtered = new HashMap<>();
        for (Map.Entry<ColumnField, Set<MinoField>> entry : solutions.entrySet()) {
            HashSet<MinoField> newSet = entry.getValue().stream()
                    .filter(filter::testMinoField)
                    .collect(Collectors.toCollection(HashSet::new));
            filtered.put(entry.getKey(), newSet);
        }
        this.solutions = filtered;
    }

    public Set<MinoField> get(ColumnField columnField) {
        return solutions.get(columnField);
    }
}
