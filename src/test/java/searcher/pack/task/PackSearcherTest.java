package searcher.pack.task;

import common.datastore.Pair;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.jupiter.api.Test;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.solutions.OnDemandBasicSolutions;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class PackSearcherTest {
    private static final int FIELD_WIDTH = 10;

    // width = expected_count
    // SizedBit=3x4用の最後まで探索した解の個数
    // デグレ確認用
    private static final HashSet<Pair<Integer, Long>> ALL3x4_WIDTH_EXPECT = new HashSet<Pair<Integer, Long>>() {
        {
            add(new Pair<>(4, 840L));
            add(new Pair<>(5, 6953L));
            add(new Pair<>(6, 53418L));
            add(new Pair<>(7, 388293L));
            add(new Pair<>(8, 3195227L));
        }
    };

    // SizedBit=2x4用の最後まで探索した解の個数
    // デグレ確認用
    private static final HashSet<Pair<Integer, Long>> ALL2x4_WIDTH_EXPECT = new HashSet<Pair<Integer, Long>>() {
        {
            add(new Pair<>(4, 956L));
            add(new Pair<>(5, 7043L));
            add(new Pair<>(6, 60416L));
            add(new Pair<>(7, 437597L));
            add(new Pair<>(8, 3615403L));
        }
    };

    private static final HashSet<Pair<Integer, Long>> SRS_WIDTH_EXPECT = new HashSet<Pair<Integer, Long>>() {
        {
            add(new Pair<>(4, 424L));
            add(new Pair<>(5, 2602L));
            add(new Pair<>(6, 16944L));
            add(new Pair<>(7, 103465L));
            add(new Pair<>(8, 634634L));
        }
    };

    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();

    @Test
    void packAllCandidate1() throws ExecutionException, InterruptedException {
        // ALL: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=Mapped
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create
        BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        SolutionFilter solutionFilter = new AllPassedSolutionFilter();

        // Assert
        assertAllCandidates(sizedBit, basicSolutions, taskResultHelper, solutionFilter, ALL3x4_WIDTH_EXPECT);
    }

    @Test
    void packAllCandidate2() throws ExecutionException, InterruptedException {
        // ALL: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=OnDemand
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create
        BasicSolutions basicSolutions = createOnDemandBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        SolutionFilter solutionFilter = new AllPassedSolutionFilter();

        // Assert
        assertAllCandidates(sizedBit, basicSolutions, taskResultHelper, solutionFilter, ALL3x4_WIDTH_EXPECT);
    }

    @Test
    void packAllCandidate3() throws ExecutionException, InterruptedException {
        // ALL: SizedBit=2x4, TaskResultHelper=Basic, BasicSolutions=Mapped
        int width = 2;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create
        BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new BasicMinoPackingHelper();
        SolutionFilter solutionFilter = new AllPassedSolutionFilter();

        // Assert
        assertAllCandidates(sizedBit, basicSolutions, taskResultHelper, solutionFilter, ALL2x4_WIDTH_EXPECT);
    }

    private BasicSolutions createMappedBasicSolutions(SizedBit sizedBit) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        return new MappedBasicSolutions(calculate);
    }

    private BasicSolutions createOnDemandBasicSolutions(SizedBit sizedBit) {
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        Predicate<ColumnField> memorizedPredicate = BasicSolutions.createBitCountPredicate(1);
        return new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);
    }

    private void assertAllCandidates(SizedBit sizedBit, BasicSolutions basicSolutions, TaskResultHelper taskResultHelper, SolutionFilter solutionFilter, HashSet<Pair<Integer, Long>> exceptPairs) throws InterruptedException, ExecutionException {
        for (Pair<Integer, Long> pair : exceptPairs) {
            Integer emptyWidth = pair.getKey();
            Long expectedCount = pair.getValue();
            Field initField = createSquareEmptyField(emptyWidth, sizedBit.getHeight());
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);

            PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            assertThat(searcher.count()).isEqualTo(expectedCount);
        }
    }

    private static Field createSquareEmptyField(int emptyWidth, int emptyHeight) {
        Field field = FieldFactory.createField(emptyHeight);
        for (int x = emptyWidth; x < FIELD_WIDTH; x++)
            for (int y = 0; y < emptyHeight; y++)
                field.setBlock(x, y);
        return field;
    }

    @Test
    void packSRSCandidate1() throws ExecutionException, InterruptedException {
        // SRS: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=Mapped
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create
        BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        // Assert
        assertSRSCandidates(sizedBit, basicSolutions, taskResultHelper);
    }

    @Test
    void packSRSCandidate2() throws ExecutionException, InterruptedException {
        // SRS: SizedBit=3x4, TaskResultHelper=4x10, BasicSolutions=OnDemand
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create
        BasicSolutions basicSolutions = createOnDemandBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        // Assert
        assertSRSCandidates(sizedBit, basicSolutions, taskResultHelper);
    }

    @Test
    void packSRSCandidate3() throws ExecutionException, InterruptedException {
        // SRS: SizedBit=2x4, TaskResultHelper=Basic, BasicSolutions=Mapped
        int width = 2;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        // Create
        BasicSolutions basicSolutions = createMappedBasicSolutions(sizedBit);
        TaskResultHelper taskResultHelper = new BasicMinoPackingHelper();

        // Assert
        assertSRSCandidates(sizedBit, basicSolutions, taskResultHelper);
    }

    private void assertSRSCandidates(SizedBit sizedBit, BasicSolutions basicSolutions, TaskResultHelper taskResultHelper) throws InterruptedException, ExecutionException {
        for (Pair<Integer, Long> pair : SRS_WIDTH_EXPECT) {
            Integer emptyWidth = pair.getKey();
            Field initField = createSquareEmptyField(emptyWidth, sizedBit.getHeight());
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, initField);

            SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, initField);
            PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Long expectedCount = pair.getValue();
            assertThat(searcher.count()).isEqualTo(expectedCount);
        }
    }

    private SolutionFilter createSRSSolutionFilter(SizedBit sizedBit, Field initField) {
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
    }
}
