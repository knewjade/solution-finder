package searcher.spins.pieces;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import org.junit.jupiter.api.Test;
import searcher.spins.pieces.AllSimpleOriginalPieces;
import searcher.spins.pieces.SimpleOriginalPieceFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleOriginalPieceFactoryTest {
    @Test
    void createHeight2() {
        int fieldHeight = 2;
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, fieldHeight);
        AllSimpleOriginalPieces allPieces = factory.createAllPieces();
        assertThat(allPieces.getOriginalPieces()).hasSize(87);
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