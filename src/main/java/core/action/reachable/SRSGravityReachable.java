package core.action.reachable;

import common.datastore.SimpleMinoOperation;
import common.datastore.action.Action;
import core.action.cache.MinimalLockedCache;
import core.field.Field;
import core.field.LargeField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToIntFunction;

/**
 * マルチスレッド非対応
 * 出現位置は(x,y)=(4,20)で固定。
 * 先行入力は、横移動・回転ともに考慮しない = 出現位置からはじめに重力移動が行われる。
 * 回転→横移動→重力移動の順番で処理される。
 */
public class SRSGravityReachable implements Reachable {
    private static class L2Norm implements ToIntFunction<SimpleMinoOperation> {
        private final int goalX;
        private final int goalY;

        private L2Norm(int goalX, int goalY) {
            this.goalX = goalX;
            this.goalY = goalY;
        }

        @Override
        public int applyAsInt(SimpleMinoOperation operation) {
            int dx = operation.getX() - goalX;
            int dy = operation.getY() - goalY;
            return dx * dx + dy * dy;
        }
    }

    private static final int FIELD_WIDTH = 10;
    private static final int START_X = 4;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final MinimalLockedCache lockedCache;
    private final int gravity;

    public SRSGravityReachable(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int gravity) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.lockedCache = new MinimalLockedCache(24);
        this.gravity = gravity;
    }

    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int validHeight) {
        assert field.canPut(mino, x, y) && field.isOnGround(mino, x, y);

        LargeField largeField = new LargeField();
        largeField.merge(field);

        return checks(largeField, mino, x, y);
    }

    private boolean checks(LargeField field, Mino mino, int x, int y) {
        assert field.canPut(mino, x, y) && field.isOnGround(mino, x, y);

        lockedCache.clear();

        Piece piece = mino.getPiece();
        Mino spawn = minoFactory.create(piece, Rotate.Spawn);

        int startY = getGravityY(field, spawn, START_X, 20);
        assert field.canPut(spawn, START_X, startY);

        Action goal = minoShifter.createTransformedAction(piece, mino.getRotate(), x, y);

        return checks(field, startY, piece, goal);
    }

    private boolean checks(Field field, int startY, Piece piece, Action goal) {
        L2Norm l2Norm = new L2Norm(goal.getX(), goal.getY());
        PriorityQueue<SimpleMinoOperation> candidates = new PriorityQueue<>(Comparator.comparingInt(l2Norm));
        candidates.add(new SimpleMinoOperation(minoFactory.create(piece, Rotate.Spawn), START_X, startY));

        while (!candidates.isEmpty()) {
            SimpleMinoOperation operation = candidates.poll();

            Mino mino = operation.getMino();
            int x = operation.getX();
            int y = operation.getY();

            Action transformed = minoShifter.createTransformedAction(piece, operation);

            // ゴールにたどり着いたとき
            if (goal.equals(transformed)) {
                return true;
            }

            move(field, mino, x, y, candidates);
        }

        return false;
    }

    private void move(Field field, Mino mino, int x, int y, PriorityQueue<SimpleMinoOperation> candidates) {
        // 左に移動
        moveLeft(field, mino, x, y, candidates);

        // 右に移動
        moveRight(field, mino, x, y, candidates);

        // 左回転
        moveLeftRotation(field, mino, x, y, candidates);

        // 右回転
        moveRightRotation(field, mino, x, y, candidates);
    }

    private void moveLeft(Field field, Mino mino, int x, int y, PriorityQueue<SimpleMinoOperation> candidates) {
        int leftX = x - 1;
        if (-mino.getMinX() <= leftX && field.canPut(mino, leftX, y)) {
            addAfterGravity(field, mino, leftX, y, candidates);
        }
    }

    private void moveRight(Field field, Mino mino, int x, int y, PriorityQueue<SimpleMinoOperation> candidates) {
        int rightX = x + 1;
        if (rightX < FIELD_WIDTH - mino.getMaxX() && field.canPut(mino, rightX, y)) {
            addAfterGravity(field, mino, rightX, y, candidates);
        }
    }

    private void moveLeftRotation(Field field, Mino mino, int x, int y, PriorityQueue<SimpleMinoOperation> candidates) {
        Mino minoAfter = minoFactory.create(mino.getPiece(), mino.getRotate().getLeftRotate());
        int[] kicks = minoRotation.getKicksWithLeftRotation(field, mino, minoAfter, x, y);
        if (kicks == null) {
            return;
        }

        int toX = x + kicks[0];
        int toY = y + kicks[1];
        addAfterGravity(field, minoAfter, toX, toY, candidates);

        // 回転後、重力で移動する前に横移動
        moveLeft(field, minoAfter, toX, toY, candidates);
        moveRight(field, minoAfter, toX, toY, candidates);
    }

    private void moveRightRotation(Field field, Mino mino, int x, int y, PriorityQueue<SimpleMinoOperation> candidates) {
        Rotate rotateAfter = mino.getRotate().getRightRotate();
        Mino minoAfter = minoFactory.create(mino.getPiece(), rotateAfter);
        int[] kicks = minoRotation.getKicksWithRightRotation(field, mino, minoAfter, x, y);
        if (kicks == null) {
            return;
        }

        int toX = x + kicks[0];
        int toY = y + kicks[1];
        addAfterGravity(field, minoAfter, toX, toY, candidates);

        // 回転後、重力で移動する前に横移動
        moveLeft(field, minoAfter, toX, toY, candidates);
        moveRight(field, minoAfter, toX, toY, candidates);
    }

    private void addAfterGravity(Field field, Mino mino, int x, int y, PriorityQueue<SimpleMinoOperation> candidates) {
        Rotate rotate = mino.getRotate();
        int gravityY = getGravityY(field, mino, x, y);
        if (!lockedCache.isVisit(x, gravityY, rotate)) {
            candidates.add(new SimpleMinoOperation(mino, x, gravityY));
            lockedCache.visit(x, gravityY, rotate);
        }
    }

    private int getGravityY(Field field, Mino mino, int x, int y) {
        assert field.canPut(mino, x, y);
        int ng = Math.min(gravity, y + mino.getMinY());
        for (int dy = 1; dy <= ng; dy++) {
            int ny = y - dy;
            if (!field.canPut(mino, x, ny)) {
                return ny + 1;
            }
        }
        return y - ng;
    }
}
