package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.neighbor.Neighbor;
import core.neighbor.OriginalPiece;
import core.neighbor.OriginalPieceFactory;
import core.srs.MinoRotation;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import lib.Randoms;
import lib.Stopwatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LockedNeighborCandidateTest {
    private ILockedCandidate createLockedCandidate(int maxClearLine) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        return CandidateFacade.create90Locked(minoFactory, minoShifter, minoRotation, maxClearLine);
    }

    private LockedNeighborCandidate createLockedNeighborCandidate(int maxClearLine) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(maxClearLine + 3);
        return new LockedNeighborCandidate(minoFactory, minoShifter, minoRotation, pieceFactory);
    }

    private MinimalAction createMinimalAction(OriginalPiece piece) {
        return MinimalAction.create(piece.getX(), piece.getY(), piece.getMino().getRotate());
    }

    @Test
    void random() {
        int maxClearLine = 3;
        ILockedCandidate candidate1 = createLockedCandidate(maxClearLine);
        LockedNeighborCandidate candidate2 = createLockedNeighborCandidate(maxClearLine);
        MinoShifter minoShifter = new MinoShifter();

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();
        Stopwatch stopwatch2 = Stopwatch.createStartedStopwatch();

        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            Field field = randoms.field(maxClearLine, 7);
            for (Piece piece : Piece.values()) {
                // LockedCandidate
                stopwatch1.start();
                Set<Action> search1 = candidate1.search(field, piece, maxClearLine);
                stopwatch1.stop();

                // LockedNeighborCandidate
                stopwatch2.start();
                Set<Neighbor> neighbors = candidate2.search(field, piece, maxClearLine);
                stopwatch2.stop();
                Set<Action> search2 = neighbors.stream()
                        .map(Neighbor::getPiece)
                        .map(this::createMinimalAction)
                        .map(action -> minoShifter.createTransformedAction(piece, action))
                        .collect(Collectors.toSet());

                assertThat(search2).isEqualTo(search1);
            }
        }

        System.out.println(stopwatch1.toMessage(TimeUnit.NANOSECONDS));
        System.out.println(stopwatch2.toMessage(TimeUnit.NANOSECONDS));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldTestCase.class)
    void testField(Field field, Piece piece) {
        int maxClearLine = 4;
        ILockedCandidate candidate1 = createLockedCandidate(maxClearLine);
        LockedNeighborCandidate candidate2 = createLockedNeighborCandidate(maxClearLine);
        MinoShifter minoShifter = new MinoShifter();

        // LockedCandidate
        Set<Action> search1 = candidate1.search(field, piece, maxClearLine);

        // LockedNeighborCandidate
        Set<Neighbor> neighbors = candidate2.search(field, piece, maxClearLine);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(this::createMinimalAction)
                .map(action -> minoShifter.createTransformedAction(piece, action))
                .collect(Collectors.toSet());

        assertThat(search2).isEqualTo(search1);
    }

    private static class FieldTestCase implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Arguments arguments1 = Arguments.of(FieldFactory.createSmallField("" +
                    "X________X" +
                    "________XX" +
                    "__XXXXXXXX" +
                    "__________" +
                    ""
            ), Piece.T);
            Arguments arguments2 = Arguments.of(FieldFactory.createSmallField("" +
                    "XXXXXX__XX" +
                    "_________X" +
                    "_______XXX" +
                    "__________" +
                    ""
            ), Piece.T);
            Arguments arguments3 = Arguments.of(FieldFactory.createSmallField("" +
                    "X________X" +
                    "________XX" +
                    "XXXXXXX__X" +
                    "__________" +
                    ""
            ), Piece.T);
            Arguments arguments4 = Arguments.of(FieldFactory.createSmallField("" +
                    "XXXXXX_XXX" +
                    "_________X" +
                    "__________" +
                    "X________X" +
                    ""
            ), Piece.S);
            Arguments arguments5 = Arguments.of(FieldFactory.createSmallField("" +
                    "_________X" +
                    "XX______XX" +
                    "XXXXX____X" +
                    "_________X" +
                    ""
            ), Piece.I);
            return Stream.of(
                    arguments1,
                    arguments2,
                    arguments3,
                    arguments4,
                    arguments5
            );
        }
    }
}