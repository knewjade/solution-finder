package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import lib.Stopwatch;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.mino_fields.RecursiveMinoFields;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MappedBasicSolutionsFactoryTest {
    @Test
    void create3x1() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 1);
        int expectedSolutions = 3;
        int expectedSolutionItems = 3;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    void create3x2() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 2);
        int expectedSolutions = 28;
        int expectedSolutionItems = 88;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    void create3x3() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 3);
        int expectedSolutions = 254;
        int expectedSolutionItems = 3972;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    void create3x4() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 4);
        int expectedSolutions = 2211;
        int expectedSolutionItems = 228022;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    void create2x5() throws Exception {
        SizedBit sizedBit = new SizedBit(2, 5);
        int expectedSolutions = 822;
        int expectedSolutionItems = 321978;
        assertCache(sizedBit, expectedSolutions, expectedSolutionItems);
    }

    @Test
    @Tag("long")
    void create2x6() throws Exception {
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

        assertThat(countValidKey(solutions)).isEqualTo(expectedSolutions);
        assertThat(countValidItem(solutions)).isEqualTo(expectedSolutionItems);
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        return SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
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