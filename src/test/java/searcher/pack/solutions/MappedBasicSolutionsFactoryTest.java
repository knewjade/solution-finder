package searcher.pack.solutions;

import lib.Stopwatch;
import core.column_field.ColumnField;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MappedBasicSolutionsFactoryTest {
    @Test
    public void create3x1() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 1);
        int expectedSolutions = 3;
        int expectedSolutionItems = 3;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    public void create3x2() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 2);
        int expectedSolutions = 28;
        int expectedSolutionItems = 88;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    public void create3x3() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 3);
        int expectedSolutions = 254;
        int expectedSolutionItems = 3972;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    public void create3x4() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 4);
        int expectedSolutions = 2211;
        int expectedSolutionItems = 228022;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    public void create2x5() throws Exception {
        SizedBit sizedBit = new SizedBit(2, 5);
        int expectedSolutions = 822;
        int expectedSolutionItems = 321978;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    public void create2x6() throws Exception {
        SizedBit sizedBit = new SizedBit(2, 6);
        int expectedSolutions = 3490;
        int expectedSolutionItems = 8380826;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    private void assertCache(SizedBit sizedBit, long expectedSolutions, long expectedSolutionItems) throws IOException {
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);

        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();

        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();

        stopwatch1.stop();
        System.out.println("create only: " + stopwatch1.toMessage(TimeUnit.MILLISECONDS));

        AllPassedSolutionFilter solutionFilter = new AllPassedSolutionFilter();
        MappedBasicSolutions solutions = new MappedBasicSolutions(calculate, solutionFilter);

        assertThat(countValidKey(solutions), is(expectedSolutions));
        assertThat(countValidItem(solutions), is(expectedSolutionItems));
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }

    private static long countValidKey(MappedBasicSolutions basicSolutions) {
        return basicSolutions.getSolutions().values().parallelStream()
                .map(MinoFields::stream)
                .map(Stream::findAny)
                .filter(Optional::isPresent)
                .count();
    }

    private static long countValidItem(MappedBasicSolutions basicSolutions) {
        return basicSolutions.getSolutions().values().parallelStream()
                .map(MinoFields::stream)
                .mapToLong(Stream::count)
                .sum();
    }
}