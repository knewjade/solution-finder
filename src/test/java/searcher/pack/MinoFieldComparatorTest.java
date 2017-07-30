package searcher.pack;

import core.column_field.ColumnFieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import lib.Randoms;
import org.junit.jupiter.api.Test;
import searcher.pack.mino_field.RecursiveMinoField;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MinoFieldComparatorTest {
    private final SeparableMinos separableMinos = createSeparableMinos();

    private SeparableMinos createSeparableMinos() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SizedBit sizedBit = new SizedBit(3, 4);
        return SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
    }

    @Test
    void compareMinoFieldEqual() {
        Randoms randoms = new Randoms();

        List<SeparableMino> minos = separableMinos.getMinos();

        SeparableMino mino = minos.get(0);
        RecursiveMinoField recursiveMinoField1 = new RecursiveMinoField(mino, ColumnFieldFactory.createField(), separableMinos);
        RecursiveMinoField recursiveMinoField2 = new RecursiveMinoField(mino, ColumnFieldFactory.createField(), separableMinos);

        for (int count = 0; count < 10; count++) {
            int index = randoms.nextInt(0, minos.size() - 1);
            SeparableMino randomMino = minos.get(index);
            recursiveMinoField1 = new RecursiveMinoField(randomMino, recursiveMinoField1, ColumnFieldFactory.createField(), separableMinos);
            recursiveMinoField2 = new RecursiveMinoField(randomMino, recursiveMinoField2, ColumnFieldFactory.createField(), separableMinos);
        }

        MinoFieldComparator comparator = new MinoFieldComparator();
        assertThat(comparator.compare(recursiveMinoField1, recursiveMinoField2)).isEqualTo(0);
    }

    @Test
    void compareMinoFieldDiffMino() {
        Randoms randoms = new Randoms();
        List<SeparableMino> minos = separableMinos.getMinos();
        MinoFieldComparator comparator = new MinoFieldComparator();

        for (int count = 0; count < 100000; count++) {
            int index1 = randoms.nextInt(0, minos.size());
            SeparableMino mino1 = minos.get(index1);
            RecursiveMinoField recursiveMinoField1 = new RecursiveMinoField(mino1, ColumnFieldFactory.createField(), separableMinos);

            int index2 = randoms.nextInt(0, minos.size() - 1);
            if (index1 == index2)
                index2 += 1;
            SeparableMino mino2 = minos.get(index2);
            RecursiveMinoField recursiveMinoField2 = new RecursiveMinoField(mino2, ColumnFieldFactory.createField(), separableMinos);

            // assert is not 0 & sign reversed
            assertThat(comparator.compare(recursiveMinoField1, recursiveMinoField2) * comparator.compare(recursiveMinoField2, recursiveMinoField1)).isLessThan(0);
        }
    }

    @Test
    void compareMinoFieldDiffSize() {
        Randoms randoms = new Randoms();

        List<SeparableMino> minos = separableMinos.getMinos();
        int index = randoms.nextInt(0, minos.size() - 1);
        SeparableMino mino = minos.get(index);
        RecursiveMinoField recursiveMinoField1 = new RecursiveMinoField(mino, ColumnFieldFactory.createField(), separableMinos);

        RecursiveMinoField recursiveMinoField2 = new RecursiveMinoField(mino, ColumnFieldFactory.createField(), separableMinos);
        recursiveMinoField2 = new RecursiveMinoField(mino, recursiveMinoField2, ColumnFieldFactory.createField(), separableMinos);

        // assert is not 0 & sign reversed
        MinoFieldComparator comparator = new MinoFieldComparator();
        assertThat(comparator.compare(recursiveMinoField1, recursiveMinoField2) * comparator.compare(recursiveMinoField2, recursiveMinoField1)).isLessThan(0);
    }

    @Test
    void compareMinoFieldDiffOuterField() {
        Randoms randoms = new Randoms();

        List<SeparableMino> minos = separableMinos.getMinos();
        int index = randoms.nextInt(0, minos.size() - 1);
        SeparableMino mino = minos.get(index);
        RecursiveMinoField recursiveMinoField1 = new RecursiveMinoField(mino, ColumnFieldFactory.createField(), separableMinos);

        RecursiveMinoField recursiveMinoField2 = new RecursiveMinoField(mino, ColumnFieldFactory.createField(1L), separableMinos);

        MinoFieldComparator comparator = new MinoFieldComparator();
        assertThat(comparator.compare(recursiveMinoField1, recursiveMinoField2)).isEqualTo(0);
    }
}