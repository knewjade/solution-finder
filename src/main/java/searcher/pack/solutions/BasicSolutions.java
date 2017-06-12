package searcher.pack.solutions;

import core.column_field.ColumnField;
import searcher.pack.MinoField;
import searcher.pack.RecursiveMinoField;
import searcher.pack.memento.SolutionFilter;

import java.util.*;
import java.util.stream.Collectors;

public class BasicSolutions {
    public static BasicSolutions create(Map<ColumnField, List<RecursiveMinoField>> solutions) {
        HashMap<ColumnField, List<MinoField>> newMap = new HashMap<>();
        for (Map.Entry<ColumnField, List<RecursiveMinoField>> entry : solutions.entrySet())
            newMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        return new BasicSolutions(newMap);
    }

    public static BasicSolutions create(Map<ColumnField, List<RecursiveMinoField>> solutions, SolutionFilter filter) {
        HashMap<ColumnField, List<MinoField>> filtered = new HashMap<>();
        for (Map.Entry<ColumnField, List<RecursiveMinoField>> entry : solutions.entrySet()) {
            List<MinoField> newList = entry.getValue().stream()
                    .filter(filter::testMinoField)
                    .collect(Collectors.toList());

            if (0 < newList.size())
                filtered.put(entry.getKey(), newList);
        }
        return new BasicSolutions(filtered);
    }

    private static final List<MinoField> EMPTY_MINO_FIELDS = Collections.emptyList();

    private final Map<ColumnField, List<MinoField>> solutions;

    public BasicSolutions(Map<ColumnField, List<MinoField>> solutions) {
        assert solutions != null;
        this.solutions = solutions;
    }

    public BasicSolutions(Map<ColumnField, List<MinoField>> solutions, SolutionFilter filter) {
        HashMap<ColumnField, List<MinoField>> filtered = new HashMap<>();
        for (Map.Entry<ColumnField, List<MinoField>> entry : solutions.entrySet()) {
            List<MinoField> newList = entry.getValue().stream()
                    .filter(filter::testMinoField)
                    .collect(Collectors.toList());

            if (0 < newList.size())
                filtered.put(entry.getKey(), newList);
        }
        this.solutions = filtered;
    }

    public List<MinoField> parse(ColumnField columnField) {
        return solutions.getOrDefault(columnField, EMPTY_MINO_FIELDS);
    }

    public Map<ColumnField, List<MinoField>> getSolutions() {
        return solutions;
    }
}
