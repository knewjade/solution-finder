package _experimental.allcomb.solutions;

import _experimental.allcomb.SizedBit;
import core.column_field.ColumnSmallField;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import pack.separable_mino.SeparableMino;
import pack.separable_mino.SeparableMinoFactory;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class BasicSolutionsTest {
    @Test
    public void get() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 4);
        List<SeparableMino> minos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(minos, sizedBit);
        BasicSolutions solutions = calculator.calculate();
        assertThat(solutions.get(new ColumnSmallField()), hasSize(5685));
    }

    private static List<SeparableMino> createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        return factory.create();
    }
}