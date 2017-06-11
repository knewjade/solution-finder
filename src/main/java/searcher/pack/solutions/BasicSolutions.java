package searcher.pack.solutions;

import core.column_field.ColumnField;
import searcher.pack.IMinoField;
import searcher.pack.memento.SolutionFilter;

import java.util.*;
import java.util.stream.Collectors;

public class BasicSolutions {
    public static BasicSolutions createFromSet(Map<ColumnField, Set<IMinoField>> solutions) {
        HashMap<ColumnField, List<IMinoField>> newHashMap = new HashMap<>();
        for (Map.Entry<ColumnField, Set<IMinoField>> entry : solutions.entrySet()) {
            List<IMinoField> newList = new ArrayList<>(entry.getValue());

            if (0 < newList.size())
                newHashMap.put(entry.getKey(), newList);
        }
        return new BasicSolutions(newHashMap);
    }

    public static BasicSolutions createFromSet(Map<ColumnField, Set<IMinoField>> solutions, SolutionFilter filter) {
        HashMap<ColumnField, List<IMinoField>> filtered = new HashMap<>();
        for (Map.Entry<ColumnField, Set<IMinoField>> entry : solutions.entrySet()) {
            List<IMinoField> newList = entry.getValue().stream()
                    .filter(filter::testMinoField)
                    .collect(Collectors.toList());

            if (0 < newList.size())
                filtered.put(entry.getKey(), newList);
        }
        return new BasicSolutions(filtered);
    }

    private static final List<IMinoField> EMPTY_MINO_FIELDS = Collections.emptyList();

    private final Map<ColumnField, List<IMinoField>> solutions;

    public BasicSolutions(Map<ColumnField, List<IMinoField>> solutions) {
        assert solutions != null;
        this.solutions = solutions;
    }

    public BasicSolutions(HashMap<ColumnField, List<IMinoField>> solutions, SolutionFilter filter) {
        HashMap<ColumnField, List<IMinoField>> filtered = new HashMap<>();
        for (Map.Entry<ColumnField, List<IMinoField>> entry : solutions.entrySet()) {
            List<IMinoField> newList = entry.getValue().stream()
                    .filter(filter::testMinoField)
                    .collect(Collectors.toList());

            if (0 < newList.size())
                filtered.put(entry.getKey(), newList);
        }
        this.solutions = filtered;
    }

    public List<IMinoField> parse(ColumnField columnField) {
        return solutions.getOrDefault(columnField, EMPTY_MINO_FIELDS);
    }

    public Map<ColumnField, List<IMinoField>> getSolutions() {
        return solutions;
    }
}
