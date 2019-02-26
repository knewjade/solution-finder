package searcher.spins.fill.line;

import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import org.junit.jupiter.api.Test;
import searcher.spins.AllSimpleOriginalPieces;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SpotRunnerTest {
    @Test
    void case1() {
        SpotRunner spotRunner = createSpotRunner();

        List<SpotResult> spots = spotRunner.toNew(Arrays.asList(
                new PieceBlockCount(Piece.I, 4),
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.T, 2)
        ));

        assertThat(spots).hasSize(24);

        verify(spots);
    }

    @Test
    void case2() {
        SpotRunner spotRunner = createSpotRunner();

        List<SpotResult> spots = spotRunner.toNew(Arrays.asList(
                new PieceBlockCount(Piece.J, 1),
                new PieceBlockCount(Piece.L, 1),
                new PieceBlockCount(Piece.I, 4)
        ));

        assertThat(spots).hasSize(156);

        verify(spots);
    }

    @Test
    void case3() {
        SpotRunner spotRunner = createSpotRunner();

        List<SpotResult> spots = spotRunner.toNew(Collections.singletonList(new PieceBlockCount(Piece.J, 1)));

        assertThat(spots).hasSize(6);

        verify(spots);
    }

    @Test
    void case4() {
        SpotRunner spotRunner = createSpotRunner();

        List<SpotResult> spots = spotRunner.toNew(Arrays.asList(
                new PieceBlockCount(Piece.I, 4),
                new PieceBlockCount(Piece.I, 4),
                new PieceBlockCount(Piece.I, 4)
        ));

        assertThat(spots).hasSize(0);

        verify(spots);
    }

    @Test
    void case5() {
        SpotRunner spotRunner = createSpotRunner();

        List<SpotResult> spots = spotRunner.toNew(Arrays.asList(
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.O, 2),
                new PieceBlockCount(Piece.O, 2)
        ));

        assertThat(spots).hasSize(32);

        verify(spots);
    }

    private SpotRunner createSpotRunner() {
        int maxHeight = 8;
        Field initField = FieldFactory.createField(maxHeight);
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        AllSimpleOriginalPieces allSimpleOriginalPieces = new AllSimpleOriginalPieces(minoFactory, minoShifter, 10, maxHeight);
        List<SimpleOriginalPiece> originalPieces = allSimpleOriginalPieces.createList().stream()
                .filter(simpleOriginalPiece -> initField.canMerge(simpleOriginalPiece.getMinoField()))
                .collect(Collectors.toList());

        LinePools pools = LinePools.create(minoFactory, minoShifter, originalPieces, maxHeight);

        return new SpotRunner(pools.getPieceBlockCountToMinoDiffs(), pools.getKeyToOriginPiece());
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
    }
}