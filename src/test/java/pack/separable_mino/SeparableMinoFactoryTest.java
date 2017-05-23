package pack.separable_mino;

import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SeparableMinoFactoryTest {
    @Test
    public void create3x4() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        int width = 3;
        int height = 4;
        SeparableMinoFactory factory = new SeparableMinoFactory(minoFactory, minoShifter, width, height);
        List<SeparableMino> minos = factory.create();

        assertThat(minos, hasSize(273));
        assertThat(minos.stream().filter(createBlockPredicate(Block.T)).count(), is(60L));
        assertThat(minos.stream().filter(createBlockPredicate(Block.I)).count(), is(15L));
        assertThat(minos.stream().filter(createBlockPredicate(Block.S)).count(), is(30L));
        assertThat(minos.stream().filter(createBlockPredicate(Block.Z)).count(), is(30L));
        assertThat(minos.stream().filter(createBlockPredicate(Block.O)).count(), is(18L));
        assertThat(minos.stream().filter(createBlockPredicate(Block.L)).count(), is(60L));
        assertThat(minos.stream().filter(createBlockPredicate(Block.J)).count(), is(60L));
    }

    private Predicate<SeparableMino> createBlockPredicate(Block block) {
        return separableMino -> separableMino.getMino().getBlock() == block;
    }
}