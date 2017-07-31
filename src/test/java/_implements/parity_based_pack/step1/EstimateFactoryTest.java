package _implements.parity_based_pack.step1;

import core.mino.Block;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EstimateFactoryTest {
    @Test
    void testSZO() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createSZO(2, 1, 1);
        assertThat(list).hasSize(1);

        List<DeltaLimitedMino> szo = list.get(0);

        assertThat(szo).hasSize(4);
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.S)).hasSize(2);
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.Z)).hasSize(1);
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.O)).hasSize(1);

        assertThat(szo.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.EvenUp)).hasSize(0);
        assertThat(szo.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.OddUp)).hasSize(0);
        assertThat(szo.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.Flat)).hasSize(4);
    }

    @Test
    void testLJAllOdd() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createLJ(1, 2, 3);
        assertThat(list).hasSize(1);

        List<DeltaLimitedMino> lj = list.get(0);

        assertThat(lj).hasSize(3);
        assertThat(lj.stream().filter(mino -> mino.getBlock() == Block.L)).hasSize(1);
        assertThat(lj.stream().filter(mino -> mino.getBlock() == Block.J)).hasSize(2);

        assertThat(lj.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.EvenUp)).hasSize(0);
        assertThat(lj.stream().filter(mino -> mino.getDeltaLimit() == DeltaLimit.OddUp)).hasSize(3);
    }

    @Test
    void testLJ() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createLJ(3, 3, 2);
        assertThat(list).hasSize(7);
    }

    @Test
    void testTWithOddUp() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createT(4, 1);
        assertThat(list).hasSize(2);
    }

    @Test
    void testTWithEvenUp() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createT(5, -2);
        assertThat(list).hasSize(2);
    }

    @Test
    void testIWithOddUp() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createI(4, 1);
        assertThat(list).hasSize(2);
    }

    @Test
    void testIWithEvenUp() throws Exception {
        List<List<DeltaLimitedMino>> list = EstimateFactory.createI(5, -2);
        assertThat(list).hasSize(2);
    }
}