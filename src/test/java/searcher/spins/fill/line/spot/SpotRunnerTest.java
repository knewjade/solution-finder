package searcher.spins.fill.line.spot;

import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import org.junit.jupiter.api.Test;
import searcher.spins.pieces.SimpleOriginalPieceFactory;
import searcher.spins.pieces.SimpleOriginalPieces;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpotRunnerTest {
    @Test
    void case1() {
        int maxHeight = 8;
        SpotRunner spotRunner = createSpotRunner(maxHeight);

        List<SpotResult> spots = spotRunner.search(Arrays.asList(
                new PieceBlockCount(Piece.I, 4),
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.T, 2)
        ));

        assertThat(spots).hasSize(24);

        verify(spots);
    }

    @Test
    void case2() {
        int maxHeight = 8;
        SpotRunner spotRunner = createSpotRunner(maxHeight);

        List<SpotResult> spots = spotRunner.search(Arrays.asList(
                new PieceBlockCount(Piece.J, 1),
                new PieceBlockCount(Piece.L, 1),
                new PieceBlockCount(Piece.I, 4)
        ));

        assertThat(spots).hasSize(156);

        verify(spots);
    }

    @Test
    void case3() {
        int maxHeight = 8;
        SpotRunner spotRunner = createSpotRunner(maxHeight);

        List<SpotResult> spots = spotRunner.search(Collections.singletonList(new PieceBlockCount(Piece.J, 1)));

        assertThat(spots).hasSize(6);

        verify(spots);
    }

    @Test
    void case4() {
        int maxHeight = 8;
        SpotRunner spotRunner = createSpotRunner(maxHeight);

        List<SpotResult> spots = spotRunner.search(Arrays.asList(
                new PieceBlockCount(Piece.I, 4),
                new PieceBlockCount(Piece.I, 4),
                new PieceBlockCount(Piece.I, 4)
        ));

        assertThat(spots).hasSize(0);

        verify(spots);
    }

    @Test
    void case5() {
        int maxHeight = 8;
        SpotRunner spotRunner = createSpotRunner(maxHeight);

        List<SpotResult> spots = spotRunner.search(Arrays.asList(
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.O, 2)
        ));

        assertThat(spots).hasSize(32);

        verify(spots);
    }

    private SpotRunner createSpotRunner(int maxTargetHeight) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        LinePools pools = LinePools.create(minoFactory, minoShifter);

        SimpleOriginalPieceFactory factory = new SimpleOriginalPieceFactory(minoFactory, minoShifter, maxTargetHeight);
        SimpleOriginalPieces simpleOriginalPieces = SimpleOriginalPieces.create(factory.createAllPieces());

        return new SpotRunner(pools.getPieceBlockCountToMinoDiffs(), simpleOriginalPieces);
    }

    private void verify(List<SpotResult> spots) {
        for (SpotResult spot : spots) {
            verify(spot);
        }
    }

    private void verify(SpotResult spot) {
        Field usingField = spot.getUsingField();
        int startX = spot.getStartX();

        // `startX` よりは左は埋まっていない
        for (int x = 0; x < startX; x++) {
            assertThat(usingField.isEmpty(x, 3)).isTrue();
        }

        // `startX` から `blockCount` 分は埋まっている
        int endX = startX + spot.getUsingBlockCount();
        for (int x = startX; x < endX; x++) {
            assertThat(usingField.isEmpty(x, 3)).isFalse();
        }

        // `endX` よりは右は埋まっていない
        for (int x = endX; x < 10; x++) {
            assertThat(usingField.isEmpty(x, 3)).isTrue();
        }

        // すべてのミノがフィールド内にある
        for (SimpleOriginalPiece operation : spot.getOperations()) {
            assertThat(Field.isIn(operation.getMino(), operation.getX(), operation.getY())).isTrue();
        }

        // 最も下のブロック
        assertThat(spot.getMinY()).isEqualTo(usingField.getLowerY());

        // 最も右のブロック
        int maxFieldHeight = usingField.getMaxFieldHeight();
        int expectedRightX = -1;
        for (int x = 9; 0 <= x; x--) {
            if (0 < usingField.getBlockCountBelowOnX(x, maxFieldHeight)) {
                expectedRightX = x;
                break;
            }
        }
        assertThat(spot.getRightX()).isEqualTo(expectedRightX);
    }
}