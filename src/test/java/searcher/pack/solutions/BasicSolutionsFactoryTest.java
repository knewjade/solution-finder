package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import searcher.pack.*;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicSolutionsFactoryTest {
    @Test
    public void createWrite() throws Exception {
        File cacheFile = File.createTempFile("cache", "");
        cacheFile.delete();
        assert !cacheFile.exists();

        SizedBit sizedBit = new SizedBit(3, 4);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);

        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, List<RecursiveMinoField>> calculate = calculator.calculate();

        AllPassedSolutionFilter solutionFilter = new AllPassedSolutionFilter();
        BasicSolutions solutions1 = BasicSolutionsFactory.createAndWriteSolutions(cacheFile, solutionFilter, separableMinos, sizedBit);
        assertThat(solutions1.getSolutions().size(), is(2211));
        assertThat(countAllItem(solutions1), is(228022));

        for (long board = 0L; board < sizedBit.getFillBoard(); board++) {
            ColumnSmallField field = new ColumnSmallField(board);

            List<MinoField> list1 = new ArrayList<>(solutions1.parse(field));
            list1.sort(MinoFieldComparator::compareMinoField);

            List<RecursiveMinoField> list2 = calculate.get(field);
            list2.sort(MinoFieldComparator::compareMinoField);

            assertThat(list1, is(list2));
        }

        cacheFile.deleteOnExit();
    }

    @Test
    public void createWriteAndRead() throws Exception {
        File cacheFile = File.createTempFile("cache", "");
        cacheFile.delete();
        assert !cacheFile.exists();

        SizedBit sizedBit = new SizedBit(3, 4);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);

        AllPassedSolutionFilter solutionFilter = new AllPassedSolutionFilter();
        BasicSolutions solutions1 = BasicSolutionsFactory.createAndWriteSolutions(cacheFile, solutionFilter, separableMinos, sizedBit);
        assertThat(solutions1.getSolutions().size(), is(2211));
        assertThat(countAllItem(solutions1), is(228022));

        BasicSolutions solutions2 = BasicSolutionsFactory.readAndCreateSolutions(cacheFile, solutionFilter, separableMinos, sizedBit);

        assertThat(solutions2.getSolutions().size(), is(2211));
        assertThat(countAllItem(solutions2), is(228022));

        for (long board = 0L; board < sizedBit.getFillBoard(); board++) {
            ColumnSmallField field = new ColumnSmallField(board);

            List<MinoField> list1 = new ArrayList<>(solutions1.parse(field));
            list1.sort(MinoFieldComparator::compareMinoField);

            List<MinoField> list2 = new ArrayList<>(solutions2.parse(field));
            list2.sort(MinoFieldComparator::compareMinoField);

            assertThat(list1, is(list2));
        }

        cacheFile.deleteOnExit();
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private static int countAllItem(BasicSolutions basicSolutions) {
        int sum = 0;
        Map<ColumnField, List<MinoField>> solutions = basicSolutions.getSolutions();
        for (List<MinoField> minoFields : solutions.values())
            sum += minoFields.size();
        return sum;
    }
}