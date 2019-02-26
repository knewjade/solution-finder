package searcher.spins;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AllSimpleOriginalPiecesTest {
    @Test
    void createHeight2() {
        int fieldHeight = 2;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        AllSimpleOriginalPieces simpleOriginalPieces = new AllSimpleOriginalPieces(minoFactory, minoShifter, fieldHeight);
        assertThat(simpleOriginalPieces.createList()).hasSize(87);
    }

    @Test
    void createHeight8() {
        int fieldHeight = 8;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        AllSimpleOriginalPieces simpleOriginalPieces = new AllSimpleOriginalPieces(minoFactory, minoShifter, fieldHeight);
        assertThat(simpleOriginalPieces.createList()).hasSize(6832);
    }
}