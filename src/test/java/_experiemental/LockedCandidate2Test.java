package _experiemental;

import _experimental.LockedCandidate2;
import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.*;
import core.srs.MinoRotation;
import lib.Randoms;
import lib.Stopwatch;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LockedCandidate2Test {
    @Test
    void random() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 3;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        PieceFactory pieceFactory = new PieceFactory(height + 3);
        LockedCandidate2 candidate2 = new LockedCandidate2(minoFactory, minoShifter, minoRotation, pieceFactory);

        Randoms randoms = new Randoms();

        for (int count = 0; count < 100000; count++) {
            Field field = randoms.field(height, 7);
            field = FieldFactory.createSmallField();
            for (Block block : Block.values()) {
                Set<Action> search1 = candidate1.search(field, block, height);
                Set<Neighbor> neighbors = candidate2.search(field, block, height);
                Set<Action> search2 = neighbors.stream()
                        .map(Neighbor::getPiece)
                        .map(piece -> MinimalAction.create(piece.getX(), piece.getY(), piece.getMino().getRotate()))
                        .map(action -> minoShifter.createTransformedAction(block, action))
                        .collect(Collectors.toSet());

                if (!search1.equals(search2)) {
                    System.out.println(FieldView.toString(field));
                    System.out.println(block);
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
            for (Block block : Block.values()) {
                stopwatch1.start();
                Set<Action> search1 = candidate1.search(field, block, height);
                stopwatch1.stop();

                stopwatch2.start();
                Set<Neighbor> neighbors = candidate2.search(field, block, height);
                stopwatch2.stop();
                Set<Action> search2 = neighbors.stream()
                        .map(Neighbor::getPiece)
                        .map(piece -> MinimalAction.create(piece.getX(), piece.getY(), piece.getMino().getRotate()))
                        .map(action -> minoShifter.createTransformedAction(block, action))
                        .collect(Collectors.toSet());

                if (!search1.equals(search2)) {
                    System.out.println(FieldView.toString(field));
                    System.out.println(block);
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

        System.out.println(stopwatch1.toMessage(TimeUnit.MICROSECONDS));
        System.out.println(stopwatch2.toMessage(TimeUnit.MICROSECONDS));
    }

    @Test
    void random2() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        PieceFactory pieceFactory = new PieceFactory(height);
        LockedCandidate2 candidate2 = new LockedCandidate2(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "X________X" +
                "________XX" +
                "__XXXXXXXX" +
                "__________" +
                ""
        );
        Block block = Block.T;
        Set<Action> search1 = candidate1.search(field, block, height);
        Set<Neighbor> neighbors = candidate2.search(field, block, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece -> MinimalAction.create(piece.getX(), piece.getY(), piece.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(block, action))
                .collect(Collectors.toSet());
        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(block);
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

    @Test
    void random3() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        PieceFactory pieceFactory = new PieceFactory(height);
        LockedCandidate2 candidate2 = new LockedCandidate2(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "XXXXXX__XX" +
                "_________X" +
                "_______XXX" +
                "__________" +
                ""
        );
        Block block = Block.T;
        Set<Action> search1 = candidate1.search(field, block, height);
        Set<Neighbor> neighbors = candidate2.search(field, block, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece -> MinimalAction.create(piece.getX(), piece.getY(), piece.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(block, action))
                .collect(Collectors.toSet());

        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(block);
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

    @Test
    void random4() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        PieceFactory pieceFactory = new PieceFactory(height + 3);
        LockedCandidate2 candidate2 = new LockedCandidate2(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "X________X" +
                "________XX" +
                "XXXXXXX__X" +
                "__________" +
                ""
        );
        Block block = Block.T;
        Set<Action> search1 = candidate1.search(field, block, height);
        Set<Neighbor> neighbors = candidate2.search(field, block, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece -> MinimalAction.create(piece.getX(), piece.getY(), piece.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(block, action))
                .collect(Collectors.toSet());

        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(block);
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

    @Test
    void random5() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        PieceFactory pieceFactory = new PieceFactory(height + 3);
        LockedCandidate2 candidate2 = new LockedCandidate2(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "XXXXXX_XXX" +
                "_________X" +
                "__________" +
                "X________X" +
                ""
        );
        Block block = Block.S;
        Set<Action> search1 = candidate1.search(field, block, height);
        Set<Neighbor> neighbors = candidate2.search(field, block, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece -> MinimalAction.create(piece.getX(), piece.getY(), piece.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(block, action))
                .collect(Collectors.toSet());

        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(block);
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

    @Test
    void random6() {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        int height = 4;
        LockedCandidate candidate1 = new LockedCandidate(minoFactory, minoShifter, minoRotation, height);

        PieceFactory pieceFactory = new PieceFactory(height + 3);
        LockedCandidate2 candidate2 = new LockedCandidate2(minoFactory, minoShifter, minoRotation, pieceFactory);

        Field field = FieldFactory.createSmallField("" +
                "_________X"+
                "XX______XX"+
                "XXXXX____X"+
                "_________X"+
                ""
        );
        Block block = Block.I;
        Set<Action> search1 = candidate1.search(field, block, height);
        Set<Neighbor> neighbors = candidate2.search(field, block, height);
        Set<Action> search2 = neighbors.stream()
                .map(Neighbor::getPiece)
                .map(piece -> MinimalAction.create(piece.getX(), piece.getY(), piece.getMino().getRotate()))
                .map(action -> minoShifter.createTransformedAction(block, action))
                .collect(Collectors.toSet());

        if (!search1.equals(search2)) {
            System.out.println(FieldView.toString(field));
            System.out.println(block);
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