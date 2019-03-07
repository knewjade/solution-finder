package searcher.spins.pieces;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleOriginalPieceFactoryTest {
    @Test
    void createHeight2() {
        int fieldHeight = 2;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        AllSimpleOriginalPieces allPieces = factory.createAllPieces();
        assertThat(allPieces.getOriginalPieces()).hasSize(4452);  // height=7として扱う
    }

    @Test
    void createHeight8() {
        int fieldHeight = 8;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        AllSimpleOriginalPieces allPieces = factory.createAllPieces();
        assertThat(allPieces.getOriginalPieces()).hasSize(6832);
    }
}