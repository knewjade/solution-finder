package searcher.pack.task;

import core.column_field.ColumnField;
import searcher.pack.InOutPairField;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;

import java.util.List;

public interface PackSearcher {
    SizedBit getSizedBit();

    List<InOutPairField> getInOutPairFields();

    BasicSolutions getSolutions(int index);

    int getLastIndex();

    TaskResultHelper getTaskResultHelper();

    SolutionFilter getSolutionFilter();

    boolean isFilled(ColumnField columnField, int index);

    boolean contains(ColumnField columnField, int index);
}
