package searcher.pack.separable_mino;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldView;
import core.field.KeyOperators;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class AllSeparableMinoFactoryTest {
    @Test
    void create2x3() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        int width = 2;
        int height = 3;
        Set<SeparableMino> minos = getSeparableMinos(minoFactory, minoShifter, width, height);

        assertThat(minos.stream()).hasSize(76);
        assertThat(minos.stream().filter(createBlockPredicate(Block.T))).hasSize(16);
        assertThat(minos.stream().filter(createBlockPredicate(Block.I))).hasSize(6);
        assertThat(minos.stream().filter(createBlockPredicate(Block.S))).hasSize(8);
        assertThat(minos.stream().filter(createBlockPredicate(Block.Z))).hasSize(8);
        assertThat(minos.stream().filter(createBlockPredicate(Block.O))).hasSize(6);
        assertThat(minos.stream().filter(createBlockPredicate(Block.L))).hasSize(16);
        assertThat(minos.stream().filter(createBlockPredicate(Block.J))).hasSize(16);
    }

    @Test
    void create2x4() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        int width = 2;
        int height = 4;
        Set<SeparableMino> minos = getSeparableMinos(minoFactory, minoShifter, width, height);

        for (SeparableMino separableMino : minos) {

            ColumnField field = separableMino.getField();
            System.out.println(ColumnFieldView.toString(field, 5, 4));
            System.out.println("===");
        }

        assertThat(minos.stream()).hasSize(182);
        assertThat(minos.stream().filter(createBlockPredicate(Block.T))).hasSize(40);
        assertThat(minos.stream().filter(createBlockPredicate(Block.I))).hasSize(10);
        assertThat(minos.stream().filter(createBlockPredicate(Block.S))).hasSize(20);
        assertThat(minos.stream().filter(createBlockPredicate(Block.Z))).hasSize(20);
        assertThat(minos.stream().filter(createBlockPredicate(Block.O))).hasSize(12);
        assertThat(minos.stream().filter(createBlockPredicate(Block.L))).hasSize(40);
        assertThat(minos.stream().filter(createBlockPredicate(Block.J))).hasSize(40);
    }

    @Test
    void create2x5() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        int width = 2;
        int height = 5;
        Set<SeparableMino> minos = getSeparableMinos(minoFactory, minoShifter, width, height);

        assertThat(minos.stream()).hasSize(360);
        assertThat(minos.stream().filter(createBlockPredicate(Block.T))).hasSize(80);
        assertThat(minos.stream().filter(createBlockPredicate(Block.I))).hasSize(20);
        assertThat(minos.stream().filter(createBlockPredicate(Block.S))).hasSize(40);
        assertThat(minos.stream().filter(createBlockPredicate(Block.Z))).hasSize(40);
        assertThat(minos.stream().filter(createBlockPredicate(Block.O))).hasSize(20);
        assertThat(minos.stream().filter(createBlockPredicate(Block.L))).hasSize(80);
        assertThat(minos.stream().filter(createBlockPredicate(Block.J))).hasSize(80);
    }

    @Test
    void create3x3() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        int width = 3;
        int height = 3;
        Set<SeparableMino> minos = getSeparableMinos(minoFactory, minoShifter, width, height);

        assertThat(minos.stream()).hasSize(114);
        assertThat(minos.stream().filter(createBlockPredicate(Block.T))).hasSize(24);
        assertThat(minos.stream().filter(createBlockPredicate(Block.I))).hasSize(9);
        assertThat(minos.stream().filter(createBlockPredicate(Block.S))).hasSize(12);
        assertThat(minos.stream().filter(createBlockPredicate(Block.Z))).hasSize(12);
        assertThat(minos.stream().filter(createBlockPredicate(Block.O))).hasSize(9);
        assertThat(minos.stream().filter(createBlockPredicate(Block.L))).hasSize(24);
        assertThat(minos.stream().filter(createBlockPredicate(Block.J))).hasSize(24);
    }

    @Test
    void create3x4() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        int width = 3;
        int height = 4;
        Set<SeparableMino> minos = getSeparableMinos(minoFactory, minoShifter, width, height);

        assertThat(minos.stream()).hasSize(273);
        assertThat(minos.stream().filter(createBlockPredicate(Block.T))).hasSize(60);
        assertThat(minos.stream().filter(createBlockPredicate(Block.I))).hasSize(15);
        assertThat(minos.stream().filter(createBlockPredicate(Block.S))).hasSize(30);
        assertThat(minos.stream().filter(createBlockPredicate(Block.Z))).hasSize(30);
        assertThat(minos.stream().filter(createBlockPredicate(Block.O))).hasSize(18);
        assertThat(minos.stream().filter(createBlockPredicate(Block.L))).hasSize(60);
        assertThat(minos.stream().filter(createBlockPredicate(Block.J))).hasSize(60);
    }

    @Test
    void create3x5() throws Exception {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        int width = 3;
        int height = 5;
        Set<SeparableMino> minos = getSeparableMinos(minoFactory, minoShifter, width, height);

        assertThat(minos.stream()).hasSize(540);
        assertThat(minos.stream().filter(createBlockPredicate(Block.T))).hasSize(120);
        assertThat(minos.stream().filter(createBlockPredicate(Block.I))).hasSize(30);
        assertThat(minos.stream().filter(createBlockPredicate(Block.S))).hasSize(60);
        assertThat(minos.stream().filter(createBlockPredicate(Block.Z))).hasSize(60);
        assertThat(minos.stream().filter(createBlockPredicate(Block.O))).hasSize(30);
        assertThat(minos.stream().filter(createBlockPredicate(Block.L))).hasSize(120);
        assertThat(minos.stream().filter(createBlockPredicate(Block.J))).hasSize(120);
    }

    private Predicate<SeparableMino> createBlockPredicate(Block block) {
        return separableMino -> separableMino.toMinoOperationWithKey().getBlock() == block;
    }

    private Set<SeparableMino> getSeparableMinos(MinoFactory minoFactory, MinoShifter minoShifter, int width, int height) {
        long mask = KeyOperators.getMaskForKeyBelowY(height);
        AllSeparableMinoFactory factory = new AllSeparableMinoFactory(minoFactory, minoShifter, width, height, mask);
        return factory.create();
    }
}