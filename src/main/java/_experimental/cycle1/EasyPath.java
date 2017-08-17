package _experimental.cycle1;

import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EasyPath {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;

    public EasyPath() {
        this(new EasyPool());
    }

    public EasyPath(EasyPool easyPool) {
        this.minoFactory = easyPool.getMinoFactory();
        this.minoShifter = easyPool.getMinoShifter();
    }

    public List<Result> calculate(Field initField, int width, int height) throws ExecutionException, InterruptedException {
        assert !initField.existsAbove(height);

        // SRS: SizedBit=widthxheight, TaskResultHelper=4x10, BasicSolutions=Mapped
        SizedBit sizedBit = new SizedBit(width, height);
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);

        // Create
        BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        // Assert
        SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);

        // パフェ手順の列挙
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
        return searcher.toList();
    }

    private BasicSolutions createMappedBasicSolutions(SizedBit sizedBit) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        return new MappedBasicSolutions(calculate);
    }

    private SolutionFilter createSRSSolutionFilter(SizedBit sizedBit, Field initField) {
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
    }

}
