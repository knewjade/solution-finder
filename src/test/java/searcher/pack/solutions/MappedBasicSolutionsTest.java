package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MappedBasicSolutionsTest {
    @Test
    public void get() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 4);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new MappedBasicSolutions(calculate);
        assertThat(solutions.parse(new ColumnSmallField()).stream().count(), is(8516L));
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }
}