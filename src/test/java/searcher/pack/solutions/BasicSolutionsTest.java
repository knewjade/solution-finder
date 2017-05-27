package searcher.pack.solutions;

import searcher.pack.MinoField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import pack.separable_mino.SeparableMino;
import pack.separable_mino.SeparableMinoFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class BasicSolutionsTest {
    @Test
    public void get() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 4);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, Set<MinoField>> calculate = calculator.calculate();
        BasicSolutions solutions = BasicSolutions.createFromSet(calculate);
        assertThat(solutions.parse(new ColumnSmallField()), hasSize(5685));
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }
}