package searcher.pack.solutions;

import core.column_field.ColumnField;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.FilteredMinoFields;
import searcher.pack.mino_fields.MinoFields;

public class FilterWrappedBasicSolutions implements BasicSolutions {
    private final BasicSolutions basicSolutions;
    private final SolutionFilter solutionFilter;

    public FilterWrappedBasicSolutions(BasicSolutions basicSolutions, SolutionFilter solutionFilter) {
        this.basicSolutions = basicSolutions;
        this.solutionFilter = solutionFilter;
    }

    @Override
    public MinoFields parse(ColumnField columnField) {
        return new FilteredMinoFields(basicSolutions.parse(columnField), solutionFilter);
    }
}
