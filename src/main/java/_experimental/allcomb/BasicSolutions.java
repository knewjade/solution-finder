package _experimental.allcomb;

import java.util.Map;
import java.util.Set;

public class BasicSolutions {
    private final Map<ColumnField, Set<MinoField>> solutions;

    public BasicSolutions(Map<ColumnField, Set<MinoField>> solutions) {
        this.solutions = solutions;
    }

    public Set<MinoField> get(ColumnField columnField) {
        return solutions.get(columnField);
    }
}
