package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.jupiter.api.Test;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.mino_field.MinoField;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.mino_fields.RecursiveMinoFields;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MappedBasicSolutionsTest {
    @Test
    void get2x3() {
        SizedBit sizedBit = new SizedBit(2, 3);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new MappedBasicSolutions(calculate);
        MinoFields minoFields = solutions.parse(ColumnFieldFactory.createField());
        Stream<? extends MinoField> stream = minoFields.stream();
        assertThat(stream.count()).isEqualTo(78L);
    }

    @Test
    void get2x4() {
        SizedBit sizedBit = new SizedBit(2, 4);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new MappedBasicSolutions(calculate);
        MinoFields minoFields = solutions.parse(ColumnFieldFactory.createField());
        Stream<? extends MinoField> stream = minoFields.stream();
        assertThat(stream.count()).isEqualTo(1239L);
    }

    @Test
    void get2x5() {
        SizedBit sizedBit = new SizedBit(2, 5);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new MappedBasicSolutions(calculate);
        MinoFields minoFields = solutions.parse(ColumnFieldFactory.createField());
        Stream<? extends MinoField> stream = minoFields.stream();
        assertThat(stream.count()).isEqualTo(19375L);
    }

    @Test
    void get3x3() {
        SizedBit sizedBit = new SizedBit(3, 3);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new MappedBasicSolutions(calculate);
        MinoFields minoFields = solutions.parse(ColumnFieldFactory.createField());
        Stream<? extends MinoField> stream = minoFields.stream();
        assertThat(stream.count()).isEqualTo(278L);
    }

    @Test
    void get3x4() {
        SizedBit sizedBit = new SizedBit(3, 4);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new MappedBasicSolutions(calculate);
        MinoFields minoFields = solutions.parse(ColumnFieldFactory.createField());
        Stream<? extends MinoField> stream = minoFields.stream();
        assertThat(stream.count()).isEqualTo(8516L);
    }

    @Test
    void get3x5() {
        SizedBit sizedBit = new SizedBit(3, 5);
        SeparableMinos separableMinos = createSeparableMinos(sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions solutions = new MappedBasicSolutions(calculate);
        MinoFields minoFields = solutions.parse(ColumnFieldFactory.createField());
        Stream<? extends MinoField> stream = minoFields.stream();
        assertThat(stream.count()).isEqualTo(260179L);
    }

    private static SeparableMinos createSeparableMinos(SizedBit sizedBit) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        return SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
    }
}