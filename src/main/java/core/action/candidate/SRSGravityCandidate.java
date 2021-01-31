package core.action.candidate;

import common.datastore.action.Action;
import core.action.cache.LockedCache;
import core.field.Field;
import core.field.LargeField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import searcher.common.From;

import java.util.HashSet;
import java.util.Set;

/**
 * マルチスレッド非対応
 * 出現位置は(x,y)=(4,20)で固定。
 * 先行入力は、横移動・回転ともに考慮しない = 出現位置からはじめに重力移動が行われる。
 * 回転→横移動→重力移動の順番で処理される。
 */
public class SRSGravityCandidate implements Candidate<Action> {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final LockedCache lockedCache;
    private final int gravity;

    // temporary変数
    private int appearY = 0;

    public SRSGravityCandidate(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int gravity) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.lockedCache = new LockedCache(24);
        this.gravity = gravity;
    }

    @Override
    public Set<Action> search(Field field, Piece piece, int validHeight) {
        LargeField largeField = new LargeField();
        largeField.merge(field);
        return search(largeField, piece, validHeight);
    }

    private Set<Action> search(LargeField field, Piece piece, int validHeight) {
        // temporaryの初期化
        this.appearY = validHeight;
        lockedCache.clear();

        Mino spawn = minoFactory.create(piece, Rotate.Spawn);

        int x = 4;
        int y = 20;
        assert field.canPut(spawn, x, y);

        int startY = getGravityY(field, spawn, x, y);
        assert field.canPut(spawn, x, startY);

        HashSet<Action> actions = new HashSet<>();
        move(field, spawn, x, startY, From.None, actions);

        return actions;
    }

    private void move(Field field, Mino mino, int x, int y, From from, Set<Action> results) {
        Rotate rotate = mino.getRotate();

        // すでに訪問済みのとき
        if (lockedCache.isVisit(x, y, rotate)) {
            return;
        }
        lockedCache.visit(x, y, rotate);

        // 置くことができる場所で、有効な領域内である
        if (field.isOnGround(mino, x, y) && y + mino.getMaxY() < appearY) {
            Piece piece = mino.getPiece();
            Action action = minoShifter.createTransformedAction(piece, rotate, x, y);
            results.add(action);
        }

        // 左に移動
        moveLeft(field, mino, x, y, from, results);

        // 右に移動
        moveRight(field, mino, x, y, from, results);

        // 左回転
        moveLeftRotation(field, mino, x, y, results);

        // 右回転
        moveRightRotation(field, mino, x, y, results);
    }

    private void moveLeft(Field field, Mino mino, int x, int y, From from, Set<Action> results) {
        int leftX = x - 1;
        if (from != From.Left && -mino.getMinX() <= leftX && field.canPut(mino, leftX, y)) {
            int gravityY = getGravityY(field, mino, leftX, y);
            From nextFrom = y == gravityY ? From.Right : From.None;
            move(field, mino, leftX, gravityY, nextFrom, results);
        }
    }

    private void moveRight(Field field, Mino mino, int x, int y, From from, Set<Action> results) {
        int rightX = x + 1;
        if (from != From.Right && rightX < FIELD_WIDTH - mino.getMaxX() && field.canPut(mino, rightX, y)) {
            int gravityY = getGravityY(field, mino, rightX, y);
            From nextFrom = y == gravityY ? From.Left : From.None;
            move(field, mino, rightX, gravityY, nextFrom, results);
        }
    }

    private void moveLeftRotation(Field field, Mino mino, int x, int y, Set<Action> results) {
        Mino minoAfter = minoFactory.create(mino.getPiece(), mino.getRotate().getLeftRotate());
        int[] kicks = minoRotation.getKicksWithLeftRotation(field, mino, minoAfter, x, y);
        if (kicks == null) {
            return;
        }

        int toX = x + kicks[0];
        int toY = y + kicks[1];

        int gravityToY = getGravityY(field, minoAfter, toX, toY);
        move(field, minoAfter, toX, gravityToY, From.None, results);

        // 回転後、重力で移動する前に横移動
        moveLeft(field, minoAfter, toX, toY, From.None, results);
        moveRight(field, minoAfter, toX, toY, From.None, results);
    }

    private void moveRightRotation(Field field, Mino mino, int x, int y, Set<Action> results) {
        Mino minoAfter = minoFactory.create(mino.getPiece(), mino.getRotate().getRightRotate());
        int[] kicks = minoRotation.getKicksWithRightRotation(field, mino, minoAfter, x, y);
        if (kicks == null) {
            return;
        }

        int toX = x + kicks[0];
        int toY = y + kicks[1];
        int gravityToY = getGravityY(field, minoAfter, toX, toY);
        move(field, minoAfter, toX, gravityToY, From.None, results);

        // 回転後、重力で移動する前に横移動
        moveLeft(field, minoAfter, toX, toY, From.None, results);
        moveRight(field, minoAfter, toX, toY, From.None, results);
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
