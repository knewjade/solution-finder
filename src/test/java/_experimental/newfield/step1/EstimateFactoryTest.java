package _experimental.newfield.step1;

import core.mino.Block;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class EstimateFactoryTest {
    @Test
    public void testSZO() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createSZO(2, 1, 1);
        assertThat(list, hasSize(1));

        List<DeltaLimitedMino> szo = list.get(0);

        assertThat(szo, hasSize(4));
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.S).collect(toList()), hasSize(2));
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.Z).collect(toList()), hasSize(1));
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.O).collect(toList()), hasSize(1));

        assertThat(szo.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.EvenUp).collect(toList()), hasSize(0));
        assertThat(szo.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.OddUp).collect(toList()), hasSize(0));
        assertThat(szo.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.Flat).collect(toList()), hasSize(4));
    }

    @Test
    public void testLJAllOdd() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createLJ(1, 2, 3);
        assertThat(list, hasSize(1));

        List<DeltaLimitedMino> lj = list.get(0);

        assertThat(lj, hasSize(3));
        assertThat(lj.stream().filter(mino -> mino.getBlock() == Block.L).collect(toList()), hasSize(1));
        assertThat(lj.stream().filter(mino -> mino.getBlock() == Block.J).collect(toList()), hasSize(2));

        assertThat(lj.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.EvenUp).collect(toList()), hasSize(0));
        assertThat(lj.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.OddUp).collect(toList()), hasSize(3));
    }

    @Test
    public void testLJ() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createLJ(3, 3, 2);
        assertThat(list, hasSize(7));
        System.out.println(list);
    }

    @Test
    public void testTWithOddUp() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createT(4, 1);
        assertThat(list, hasSize(2));
        System.out.println(list);
    }

    @Test
    public void testTWithEvenUp() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createT(5, -2);
        assertThat(list, hasSize(2));
        System.out.println(list);
    }

    @Test
    public void testIWithOddUp() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createI(4, 1);
        assertThat(list, hasSize(2));
        System.out.println(list);
    }

    @Test
    public void testIWithEvenUp() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createI(5, -2);
        assertThat(list, hasSize(2));
        System.out.println(list);
    }
}