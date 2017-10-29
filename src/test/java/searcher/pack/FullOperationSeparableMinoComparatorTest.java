package searcher.pack;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import lib.Randoms;
import org.junit.jupiter.api.Test;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FullOperationSeparableMinoComparatorTest {
    private final SeparableMinos separableMinos = createSeparableMinos();

    private SeparableMinos createSeparableMinos() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SizedBit sizedBit = new SizedBit(3, 4);
        return SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
    }

    @Test
    void compareMinoFieldEqual() {
        List<SeparableMino> minos = separableMinos.getMinos();
        Randoms randoms = new Randoms();

        int index = randoms.nextInt(minos.size());
        SeparableMino mino = minos.get(index);

        FullOperationSeparableMinoComparator comparator = new FullOperationSeparableMinoComparator();
        assertThat(comparator.compare(mino, mino)).isEqualTo(0);
    }

    @Test
    void compareMinoFieldDiff() {
        List<SeparableMino> minos = separableMinos.getMinos();
        Randoms randoms = new Randoms();
        FullOperationSeparableMinoComparator comparator = new FullOperationSeparableMinoComparator();

        for (int count = 0; count < 100000; count++) {
            int index1 = randoms.nextInt(minos.size() - 1);
            int index2 = randoms.nextInt(minos.size());
            if (index1 == index2)
                index2 += 1;

            // assert is not 0 & sign reversed
            SeparableMino mino1 = minos.get(index1);
            SeparableMino mino2 = minos.get(index2);
            assertThat(comparator.compare(mino1, mino2) * comparator.compare(mino2, mino1)).isLessThan(0);
        }
    }
}