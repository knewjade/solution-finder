package core.action.candidate;

import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.action.reachable.SRSGravityReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SRSGravityCandidateTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = MinoRotation.create();

    private final DeepdropCandidate deepdropCandidate = new DeepdropCandidate(minoFactory, minoShifter);

    @Test
    public void test20gCase1() {
        int gravity = 20;
        SRSGravityCandidate candidate = new SRSGravityCandidate(minoFactory, minoShifter, minoRotation, gravity);
        SRSGravityReachable reachable = new SRSGravityReachable(minoFactory, minoShifter, minoRotation, gravity);

        int height = 6;
        Field field = FieldFactory.createField(height);
        Piece piece = Piece.L;

        Set<Action> results = candidate.search(field, piece, height);
        assertThat(results)
                .hasSize(34)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    int x = action.getX();
                    int y = action.getY();
                    return -mino.getMinX() <= x
                            && x + mino.getMaxX() < 10
                            && -mino.getMinY() <= y
                            && field.canPut(mino, x, y)
                            && field.isOnGround(mino, x, y);
                });

        Set<Action> deep = deepdropCandidate.search(field, piece, height);
        assertThat(deep)
                .hasSize(34)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    int x = action.getX();
                    int y = action.getY();
                    boolean resultIn20G = reachable.checks(field, mino, x, y, height);
                    return resultIn20G == results.contains(action);
                });
    }

    @Test
    public void test20gCase2() {
        int gravity = 20;
        SRSGravityCandidate candidate = new SRSGravityCandidate(minoFactory, minoShifter, minoRotation, gravity);
        SRSGravityReachable reachable = new SRSGravityReachable(minoFactory, minoShifter, minoRotation, gravity);

        int height = 6;
        Field field = FieldFactory.createField(""
                + "__X_______"
                + "__X_______"
                + "__X_______"
                + "__X_______"
                + "__X_______"
        );
        Piece piece = Piece.I;

        Set<Action> results = candidate.search(field, piece, height);
        assertThat(results)
                .hasSize(11)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    int x = action.getX();
                    int y = action.getY();
                    return -mino.getMinX() <= x
                            && x + mino.getMaxX() < 10
                            && -mino.getMinY() <= y
                            && field.canPut(mino, x, y)
                            && field.isOnGround(mino, x, y);
                })
                .contains(MinimalAction.create(3, 1, Rotate.Left))
                .doesNotContain(MinimalAction.create(0, 1, Rotate.Left));

        Set<Action> deep = deepdropCandidate.search(field, piece, height);
        assertThat(deep)
                .hasSize(16)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    int x = action.getX();
                    int y = action.getY();
                    boolean resultIn20G = reachable.checks(field, mino, x, y, height);
                    return resultIn20G == results.contains(action);
                });
    }

    @Test
    public void test7g() {
        int gravity = 7;
        SRSGravityCandidate candidate = new SRSGravityCandidate(minoFactory, minoShifter, minoRotation, gravity);
        SRSGravityReachable reachable = new SRSGravityReachable(minoFactory, minoShifter, minoRotation, gravity);

        int height = 6;
        Field field = FieldFactory.createField(""
                + "_X________"
                + "__X_______"
                + "__X_______"
                + "__X_______"
                + "__X_______"
        );
        Piece piece = Piece.I;

        Set<Action> results = candidate.search(field, piece, height);
        assertThat(results)
                .hasSize(16)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    int x = action.getX();
                    int y = action.getY();
                    return -mino.getMinX() <= x
                            && x + mino.getMaxX() < 10
                            && -mino.getMinY() <= y
                            && field.canPut(mino, x, y)
                            && field.isOnGround(mino, x, y);
                })
                .contains(MinimalAction.create(3, 1, Rotate.Left))
                .contains(MinimalAction.create(0, 1, Rotate.Left));

        Set<Action> deep = deepdropCandidate.search(field, piece, height);
        assertThat(deep)
                .hasSize(16)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    int x = action.getX();
                    int y = action.getY();
                    boolean resultIn20G = reachable.checks(field, mino, x, y, height);
                    return resultIn20G == results.contains(action);
                });
    }

    @Test
    public void test8g() {
        int gravity = 8;
        SRSGravityCandidate candidate = new SRSGravityCandidate(minoFactory, minoShifter, minoRotation, gravity);
        SRSGravityReachable reachable = new SRSGravityReachable(minoFactory, minoShifter, minoRotation, gravity);

        int height = 6;
        Field field = FieldFactory.createField(""
                + "_X________"
                + "__X_______"
                + "__X_______"
                + "__X_______"
                + "__X_______"
        );
        Piece piece = Piece.I;

        Set<Action> results = candidate.search(field, piece, height);
        assertThat(results)
                .hasSize(12)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    int x = action.getX();
                    int y = action.getY();
                    return -mino.getMinX() <= x
                            && x + mino.getMaxX() < 10
                            && -mino.getMinY() <= y
                            && field.canPut(mino, x, y)
                            && field.isOnGround(mino, x, y);
                })
                .contains(MinimalAction.create(3, 1, Rotate.Left))
                .doesNotContain(MinimalAction.create(0, 1, Rotate.Left));

        Set<Action> deep = deepdropCandidate.search(field, piece, height);
        assertThat(deep)
                .hasSize(16)
                .allMatch(action -> {
                    Mino mino = minoFactory.create(piece, action.getRotate());
                    int x = action.getX();
                    int y = action.getY();
                    boolean resultIn20G = reachable.checks(field, mino, x, y, height);
                    return resultIn20G == results.contains(action);
                });
    }
}