package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Piece;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.piece.Neighbor;
import core.mino.piece.OriginalPieceFactory;
import core.srs.MinoRotation;
import lib.Randoms;
import lib.Stopwatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LockedNeighborCandidateTest {
    @Test
    void random() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 3;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(height + 3);
        LockedNeighborCandidate candidate2 = new LockedNeighborCandidate(minoFactory, minoShifter, minoRotation, pieceFactory);

        Randoms randoms = new Randoms();

        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(height, 7);
            field = FieldFactory.createSmallField();
            for (Piece piece : Piece.values()) {
                Set<Action> search1 = candidate1.search(field, piece, height);
                Set<Neighbor> neighbors = candidate2.search(field, piece, height);
                Set<Action> search2 = neighbors.stream()
                        .map(Neighbor::getPiece)
                        .map(piece2 -> MinimalAction.create(piece2.getX(), piece2.getY(), piece2.getMino().getRotate()))
                        .map(action -> minoShifter.createTransformedAction(piece, action))
                        .collect(Collectors.toSet());

                if (!search1.equals(search2)) {
                    System.out.println(FieldView.toString(field));
                    System.out.println(piece);
                    for (Action action : search1) {
                        System.out.println(action);
                    }
                    System.out.println("===");
                    for (Action action : search2) {
                        System.out.println(action);
                    }

                }

                assertThat(search2).isEqualTo(search1);
            }
        }

        Stopwatch stopwatch1 = Stopwatch.createStartedStopwatch();
        Stopwatch stopwatch2 = Stopwatch.createStartedStopwatch();

        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(height, 7);
            field = FieldFactory.createSmallField();
            for (Piece piece : Piece.values()) {
                stopwatch1.start();
                Set<Action> search1 = candidate1.search(field, piece, height);
                stopwatch1.stop();

                stopwatch2.start();
                Set<Neighbor> neighbors = candidate2.search(field, piece, height);
                stopwatch2.stop();
                Set<Action> search2 = neighbors.stream()
                        .map(Neighbor::getPiece)
                        .map(piece2 -> MinimalAction.create(piece2.getX(), piece2.getY(), piece2.getMino().getRotate()))
                        .map(action -> minoShifter.createTransformedAction(piece, action))
                        .collect(Collectors.toSet());

                if (!search1.equals(search2)) {
                    System.out.println(FieldView.toString(field));
                    System.out.println(piece);
                    for (Action action : search1) {
                        System.out.println(action);
                    }
                    System.out.println("===");
                    for (Action action : search2) {
                        System.out.println(action);
                    }

                }

                assertThat(search2).isEqualTo(search1);
            }
        }

        System.out.println(stopwatch1.toMessage(TimeUnit.NANOSECONDS));
        System.out.println(stopwatch2.toMessage(TimeUnit.NANOSECONDS));
    }

    @Disabled
    @Test
    void random2() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(height);
        LockedNeighborCandidate candidate2 = new LockedNeighborCandidate(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "X________X" +
                "________XX" +
                "__XXXXXXXX" +
                "__________" +
                ""
        );
        Piece piece = Piece.T;
        Set<Action> search1 = candidate1.search(field, piece, height);
        Set<Neighbor> neighbors = candidate2.search(field, piece, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece2 -> MinimalAction.create(piece2.getX(), piece2.getY(), piece2.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(piece, action))
                .collect(Collectors.toSet());
        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(piece);
            for (Action action : search1) {
                System.out.println(action);
            }
            System.out.println("===");
            for (Action action : search2) {
                System.out.println(action);
            }

        }
        assertThat(search2).isEqualTo(search1);
    }

    @Disabled
    @Test
    void random3() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(height);
        LockedNeighborCandidate candidate2 = new LockedNeighborCandidate(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "XXXXXX__XX" +
                "_________X" +
                "_______XXX" +
                "__________" +
                ""
        );
        Piece piece = Piece.T;
        Set<Action> search1 = candidate1.search(field, piece, height);
        Set<Neighbor> neighbors = candidate2.search(field, piece, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece2 -> MinimalAction.create(piece2.getX(), piece2.getY(), piece2.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(piece, action))
                .collect(Collectors.toSet());

        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(piece);
            for (Action action : search1) {
                System.out.println(action);
            }
            System.out.println("===");
            for (Action action : search2) {
                System.out.println(action);
            }

        }
        assertThat(search2).isEqualTo(search1);
    }

    @Disabled
    @Test
    void random4() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(height + 3);
        LockedNeighborCandidate candidate2 = new LockedNeighborCandidate(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "X________X" +
                "________XX" +
                "XXXXXXX__X" +
                "__________" +
                ""
        );
        Piece piece = Piece.T;
        Set<Action> search1 = candidate1.search(field, piece, height);
        Set<Neighbor> neighbors = candidate2.search(field, piece, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece2 -> MinimalAction.create(piece2.getX(), piece2.getY(), piece2.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(piece, action))
                .collect(Collectors.toSet());

        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(piece);
            for (Action action : search1) {
                System.out.println(action);
            }
            System.out.println("===");
            for (Action action : search2) {
                System.out.println(action);
            }

        }
        assertThat(search2).isEqualTo(search1);
    }

    @Disabled
    @Test
    void random5() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(height + 3);
        LockedNeighborCandidate candidate2 = new LockedNeighborCandidate(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "XXXXXX_XXX" +
                "_________X" +
                "__________" +
                "X________X" +
                ""
        );
        Piece piece = Piece.S;
        Set<Action> search1 = candidate1.search(field, piece, height);
        Set<Neighbor> neighbors = candidate2.search(field, piece, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece2 -> MinimalAction.create(piece2.getX(), piece2.getY(), piece2.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(piece, action))
                .collect(Collectors.toSet());

        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(piece);
            for (Action action : search1) {
                System.out.println(action);
            }
            System.out.println("===");
            for (Action action : search2) {
                System.out.println(action);
            }
        }
        assertThat(search2).isEqualTo(search1);
    }

    @Disabled
    @Test
    void random6() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        OriginalPieceFactory pieceFactory = new OriginalPieceFactory(height + 3);
        LockedNeighborCandidate candidate2 = new LockedNeighborCandidate(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "_________X" +
                "XX______XX" +
                "XXXXX____X" +
                "_________X" +
                ""
        );
        Piece piece = Piece.I;
        Set<Action> search1 = candidate1.search(field, piece, height);
        Set<Neighbor> neighbors = candidate2.search(field, piece, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece2 -> MinimalAction.create(piece2.getX(), piece2.getY(), piece2.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(piece, action))
                .collect(Collectors.toSet());

        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(piece);
            for (Action action : search1) {
                System.out.println(action);
            }
            System.out.println("===");
            for (Action action : search2) {
                System.out.println(action);
            }
        }
        assertThat(search2).isEqualTo(search1);
    }
}