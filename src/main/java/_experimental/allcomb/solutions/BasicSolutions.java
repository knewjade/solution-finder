package _experimental.allcomb.solutions;

import _experimental.allcomb.MinoField;
import _experimental.allcomb.memento.MementoFilter;
import core.column_field.ColumnField;

import java.util.*;
import java.util.stream.Collectors;

public class BasicSolutions {
    private static final List<MinoField> EMPTY_MINO_FIELDS = Collections.emptyList();

    private final Map<ColumnField, List<MinoField>> solutions;

    public BasicSolutions(Map<ColumnField, List<MinoField>> solutions) {
        this.solutions = solutions;
    }

    public BasicSolutions(Map<ColumnField, Set<MinoField>> solutions, MementoFilter filter) {
        System.out.println(solutions.size());

        int sum = 0;
        for (Set<MinoField> minoFields : solutions.values()) {
            sum += minoFields.size();
        }
        System.out.println(sum);


        HashMap<ColumnField, List<MinoField>> filtered = new HashMap<>();
        for (Map.Entry<ColumnField, Set<MinoField>> entry : solutions.entrySet()) {
            List<MinoField> newList = entry.getValue().stream()
                    .filter(filter::testMinoField)
                    .collect(Collectors.toList());

            if (0 < newList.size())
                filtered.put(entry.getKey(), newList);
        }
        this.solutions = filtered;
    }

    public BasicSolutions(HashMap<ColumnField, List<MinoField>> solutions, MementoFilter filter) {

        System.out.println(solutions.size());

        int sum = 0;
        for (List<MinoField> minoFields : solutions.values()) {
            sum += minoFields.size();
        }
        System.out.println(sum);


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

    public List<MinoField> get(ColumnField columnField) {
        return solutions.getOrDefault(columnField, EMPTY_MINO_FIELDS);
    }
}
