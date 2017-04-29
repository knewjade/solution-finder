package newfield;

import core.mino.Block;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class EstimateFactoryTest {
    @Test
    public void testSZO() throws Exception {
        List<List<EstimateMino>> list = EstimateFactory.createSZO(2, 1, 1);
        assertThat(list, hasSize(1));

        List<EstimateMino> szo = list.get(0);

        assertThat(szo, hasSize(4));
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.S).collect(toList()), hasSize(2));
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.Z).collect(toList()), hasSize(1));
        assertThat(szo.stream().filter(mino -> mino.getBlock() == Block.O).collect(toList()), hasSize(1));

        assertThat(szo.stream().filter(mino -> mino.getRotateLimit() == RotateLimit.NoLimit).collect(toList()), hasSize(4));
        assertThat(szo.stream().filter(mino -> mino.getDelta() == Delta.Flat).collect(toList()), hasSize(4));
    }

    @Test
    public void testLJAllOdd() throws Exception {
        List<List<EstimateMino>> list = EstimateFactory.createLJ(1, 2, 3);
        assertThat(list, hasSize(1));

        List<EstimateMino> lj = list.get(0);

        assertThat(lj, hasSize(3));
        assertThat(lj.stream().filter(mino -> mino.getBlock() == Block.L).collect(toList()), hasSize(1));
        assertThat(lj.stream().filter(mino -> mino.getBlock() == Block.J).collect(toList()), hasSize(2));

        assertThat(lj.stream().filter(mino -> mino.getRotateLimit() == RotateLimit.NoLimit).collect(toList()), hasSize(3));
        assertThat(lj.stream().filter(mino -> mino.getDelta() == Delta.OddUp).collect(toList()), hasSize(3));
    }

    @Test
    public void testLJ() throws Exception {
        List<List<EstimateMino>> list = EstimateFactory.createLJ(3, 3, 2);
        assertThat(list, hasSize(7));
        System.out.println(list);
    }

    @Test
    public void testTWithOddUp() throws Exception {
        List<List<EstimateMino>> list = EstimateFactory.createT(4, 1);
        assertThat(list, hasSize(2));
        System.out.println(list);
    }

    @Test
    public void testTWithEvenUp() throws Exception {
        List<List<EstimateMino>> list = EstimateFactory.createT(5, -2);
        assertThat(list, hasSize(2));
        System.out.println(list);
    }

    @Test
    public void testIWithOddUp() throws Exception {
        List<List<EstimateMino>> list = EstimateFactory.createI(4, 1);
        assertThat(list, hasSize(2));
        System.out.println(list);
    }

    @Test
    public void testIWithEvenUp() throws Exception {
        List<List<EstimateMino>> list = EstimateFactory.createI(5, -2);
        assertThat(list, hasSize(2));
        System.out.println(list);
    }
}