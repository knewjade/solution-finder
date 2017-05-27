package searcher.pack.solutions;

import core.column_field.ColumnField;
import searcher.pack.MinoField;
import searcher.pack.memento.SolutionFilter;

import java.util.*;
import java.util.stream.Collectors;

public class BasicSolutions {
    public static BasicSolutions createFromSet(Map<ColumnField, Set<MinoField>> solutions) {
        HashMap<ColumnField, List<MinoField>> newHashMap = new HashMap<>();
        for (Map.Entry<ColumnField, Set<MinoField>> entry : solutions.entrySet()) {
            List<MinoField> newList = new ArrayList<>(entry.getValue());

            if (0 < newList.size())
                newHashMap.put(entry.getKey(), newList);
        }
        return new BasicSolutions(newHashMap);
    }

    public static BasicSolutions createFromSet(Map<ColumnField, Set<MinoField>> solutions, SolutionFilter filter) {
        HashMap<ColumnField, List<MinoField>> filtered = new HashMap<>();
        for (Map.Entry<ColumnField, Set<MinoField>> entry : solutions.entrySet()) {
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

    public BasicSolutions(HashMap<ColumnField, List<MinoField>> solutions, SolutionFilter filter) {
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
