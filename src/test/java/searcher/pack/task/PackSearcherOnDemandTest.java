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
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;
import searcher.pack.solutions.FilterOnDemandBasicSolutions;
import searcher.pack.solutions.OnDemandBasicSolutions;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackSearcherOnDemandTest {
    private static final int FIELD_WIDTH = 10;

    // フィールドを埋めることができる、パフェの可能性があるすべてのパターン
    // 回転入れなどの制約などはなし
    @Test
    public void testAllCandidatePacks() throws Exception {
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);

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
            int counter = calculateAllCandidateCount(separableMinos, sizedBit, initField);

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
            int counter = calculateSRSValidCount(separableMinos, sizedBit, initField);

            Integer expectedCount = pair.getValue();
            assertThat(counter, is(expectedCount));
        }
    }

    private int calculateAllCandidateCount(SeparableMinos separableMinos, SizedBit sizedBit, Field initField) throws InterruptedException, ExecutionException {
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);
        Predicate<ColumnField> memorizedPredicate = BasicSolutions.createBitCountPredicate(1);
        OnDemandBasicSolutions basicSolutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        SolutionFilter solutionFilter = new AllPassedSolutionFilter();
        PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);

        AtomicInteger counter = new AtomicInteger();
        searcher.forEach(result -> counter.getAndIncrement());
        return counter.get();
    }

    private int calculateSRSValidCount(SeparableMinos separableMinos, SizedBit sizedBit, Field initField) throws InterruptedException, ExecutionException {
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(height);
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
        Predicate<ColumnField> memorizedPredicate = BasicSolutions.createBitCountPredicate(1);
        FilterOnDemandBasicSolutions basicSolutions = new FilterOnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate, solutionFilter);

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