package core.action.reachable;

import core.action.cache.MinimalLockedCache;
import core.mino.Block;
import core.field.Field;
import core.srs.MinoRotation;
import core.srs.Rotate;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.common.action.Action;
import searcher.common.From;

import java.util.List;

public class LockedReachable implements Reachable {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final MinimalLockedCache lockedCache;

    // temporary変数
    private int appearY = 0;

    public LockedReachable(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.lockedCache = new MinimalLockedCache(maxY);
    }

    // checksの前に、cansPutの確認が必須
    @Override
    public boolean checks(Field field, Mino mino, int x, int y, int appearY) {
        this.appearY = appearY;

        Block block = mino.getBlock();
        Rotate rotate = mino.getRotate();

        if (check(field, block, x, y, rotate))
            return true;

        List<Action> actions = minoShifter.enumerateSameOtherActions(block, x, y, rotate);
        for (Action action : actions)
            if (check(field, block, action.getX(), action.getY(), action.getRotate()))
                return true;

        return false;
    }

    private boolean check(Field field, Block block, int x, int y, Rotate rotate) {
        lockedCache.clear();
        Mino mino = minoFactory.create(block, rotate);
        return check(field, mino, x, y, From.None);
    }

    private boolean check(Field field, Mino mino, int x, int y, From from) {
        // 一番上までたどり着いたとき
        if (appearY <= y)
            return true;

        Rotate rotate = mino.getRotate();

        // すでに訪問済みのとき
        if (lockedCache.isVisit(x, y, rotate))
            return false;  // 訪問済みだがまだ探索中の場合は、他の探索でカバーできるためfalseを返却

        lockedCache.visit(x, y, rotate);

        // harddropでたどりつけるとき
        if (field.canReachOnHarddrop(mino, x, y))
            return true;

        // 上に移動
        int upY = y + 1;
        if (upY < appearY && field.canPutMino(mino, x, upY))
            if (check(field, mino, x, upY, From.None))
                return true;

        // 左に移動
        int leftX = x - 1;
        if (from != From.Left && -mino.getMinX() <= leftX && field.canPutMino(mino, leftX, y))
            if (check(field, mino, leftX, y, From.Right))
                return true;

        // 右に移動
        int rightX = x + 1;
        if (from != From.Right && rightX < FIELD_WIDTH - mino.getMaxX() && field.canPutMino(mino, rightX, y))
            if (check(field, mino, rightX, y, From.Left))
                return true;

        // 右回転でくる可能性がある場所を移動
        if (checkRightRotation(field, mino, x, y))
            return true;

        // 左回転でくる可能性がある場所を移動
        if (checkLeftRotation(field, mino, x, y))
            return true;

        return false;
    }

    private boolean checkRightRotation(Field field, Mino mino, int x, int y) {
        Rotate currentRotate = mino.getRotate();
        Mino minoBefore = minoFactory.create(mino.getBlock(), currentRotate.getLeftRotate());
        int[][] patterns = minoRotation.getRightPatternsFrom(minoBefore);  // 右回転前のテストパターンを取得
        for (int[] pattern : patterns) {
            int fromX = x - pattern[0];
            int fromY = y - pattern[1];
            if (canPutMinoInField(field, minoBefore, fromX, fromY)) {
                int[] kicks = minoRotation.getKicksWithRightRotation(field, minoBefore, mino, fromX, fromY);
                if (kicks != null && pattern[0] == kicks[0] && pattern[1] == kicks[1])
                    if (check(field, minoBefore, fromX, fromY, From.None))
                        return true;
            }
        }

        return false;
    }

    private boolean checkLeftRotation(Field field, Mino mino, int x, int y) {
        Rotate currentRotate = mino.getRotate();
        Mino minoBefore = minoFactory.create(mino.getBlock(), currentRotate.getRightRotate());
        int[][] patterns = minoRotation.getLeftPatternsFrom(minoBefore);  // 右回転前のテストパターンを取得
        for (int[] pattern : patterns) {
            int fromX = x - pattern[0];
            int fromY = y - pattern[1];
            if (canPutMinoInField(field, minoBefore, fromX, fromY)) {
                int[] kicks = minoRotation.getKicksWithLeftRotation(field, minoBefore, mino, fromX, fromY);
                if (kicks != null && pattern[0] == kicks[0] && pattern[1] == kicks[1])
                    if (check(field, minoBefore, fromX, fromY, From.None))
                        return true;
            }
        }

        return false;
    }

    private boolean canPutMinoInField(Field field, Mino mino, int x, int y) {
        return -mino.getMinX() <= x && x < FIELD_WIDTH - mino.getMaxX() && -mino.getMinY() <= y && field.canPutMino(mino, x, y);
    }
}
