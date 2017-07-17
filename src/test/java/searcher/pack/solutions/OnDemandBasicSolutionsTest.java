package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.Test;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.mino_field.MinoField;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.separable_mino.SeparableMinoFactory;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OnDemandBasicSolutionsTest {
    @Test
    public void get() throws Exception {
        SizedBit sizedBit = new SizedBit(3, 4);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        Predicate<ColumnField> memorizedPredicate = columnField -> true;
        OnDemandBasicSolutions solutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);
        Stream<? extends MinoField> stream = solutions.parse(ColumnFieldFactory.createField()).stream();
        long count = stream.count();
        assertThat(count, is(8516L));
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight());
        List<SeparableMino> separableMinos = factory.create();
        return new SeparableMinos(separableMinos);
    }
}