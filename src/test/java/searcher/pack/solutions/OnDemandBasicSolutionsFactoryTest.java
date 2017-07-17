package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import lib.Stopwatch;
import org.junit.Test;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OnDemandBasicSolutionsFactoryTest {
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

        int minMemorizedBit = (int) (sizedBit.getMaxBitDigit() * 0.2);
        Predicate<ColumnField> memorizedPredicate = BasicSolutions.createBitCountPredicate(minMemorizedBit);
        OnDemandBasicSolutions solutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();

        LongStream.rangeClosed(0, sizedBit.getFillBoard())
                .parallel()
                .boxed()
                .sorted(Comparator.comparingLong(Long::bitCount).reversed())
                .map(ColumnFieldFactory::createField)
                .forEach(solutions::parse);

        stopwatch1.stop();

        System.out.println("create only: " + stopwatch1.toMessage(TimeUnit.MILLISECONDS));

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

    private static long countValidKey(OnDemandBasicSolutions basicSolutions) {
        return basicSolutions.getSolutions().values().parallelStream()
                .map(MinoFields::stream)
                .map(Stream::findAny)
                .filter(Optional::isPresent)
                .count();
    }

    private static long countValidItem(OnDemandBasicSolutions basicSolutions) {
        return basicSolutions.getSolutions().values().parallelStream()
                .map(MinoFields::stream)
                .mapToLong(Stream::count)
                .sum();
    }
}