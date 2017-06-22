package searcher.pack.task;

import common.datastore.Pair;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.BasicSolutions;
import searcher.pack.solutions.BasicSolutionsCalculator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackSearcherTest {
    private static final int FIELD_WIDTH = 10;

    // フィールドを埋めることができる、パフェの可能性があるすべてのパターン
    // 回転入れなどの制約などはなし
    @Test
    public void testAllCandidatePacks() throws Exception {
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions basicSolutions = new BasicSolutions(calculate);

        // width = expected_count
        HashSet<Pair<Integer, Integer>> widthExpected = new HashSet<Pair<Integer, Integer>>() {
            {
                add(new Pair<>(4, 840));
                add(new Pair<>(5, 6953));
                add(new Pair<>(6, 53418));
                add(new Pair<>(7, 388293));
                add(new Pair<>(8, 3195227));
            }
        };

        for (Pair<Integer, Integer> pair : widthExpected) {
            Integer emptyWidth = pair.getKey();
            Field initField = createSquareEmptyField(emptyWidth, height);
            int counter = calculateAllCandidateCount(sizedBit, basicSolutions, initField);

            Integer expectedCount = pair.getValue();
            assertThat(counter, is(expectedCount));
        }
    }

    // 回転入れを考慮されたパフェの可能性があるすべてのパターン
    @Test
    public void testAllSRSValidPacks() throws Exception {
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions basicSolutions = new BasicSolutions(calculate);

        // width = expected_count
        HashSet<Pair<Integer, Integer>> widthExpected = new HashSet<Pair<Integer, Integer>>() {
            {
                add(new Pair<>(4, 424));
                add(new Pair<>(5, 2602));
                add(new Pair<>(6, 16944));
                add(new Pair<>(7, 103465));
                add(new Pair<>(8, 634634));
            }
        };

        for (Pair<Integer, Integer> pair : widthExpected) {
            Integer emptyWidth = pair.getKey();
            Field initField = createSquareEmptyField(emptyWidth, height);
            int counter = calculateSRSValidCount(sizedBit, basicSolutions, initField);

            Integer expectedCount = pair.getValue();
            assertThat(counter, is(expectedCount));
        }
    }

    private int calculateAllCandidateCount(SizedBit sizedBit, BasicSolutions basicSolutions, Field initField) throws InterruptedException, ExecutionException {
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);
        SolutionFilter solutionFilter = new AllPassedSolutionFilter();

        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);

        AtomicInteger counter = new AtomicInteger();
        searcher.forEach(result -> counter.getAndIncrement());
        return counter.get();
    }

    private int calculateSRSValidCount(SizedBit sizedBit, BasicSolutions basicSolutions, Field initField) throws InterruptedException, ExecutionException {
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(height);
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);

        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);

        AtomicInteger counter = new AtomicInteger();
        searcher.forEach(result -> counter.getAndIncrement());
        return counter.get();
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private static Field createSquareEmptyField(int emptyWidth, int emptyHeight) {
        Field field = FieldFactory.createField(emptyHeight);
        for (int x = emptyWidth; x < FIELD_WIDTH; x++)
            for (int y = 0; y < emptyHeight; y++)
                field.setBlock(x, y);
        return field;
    }
}