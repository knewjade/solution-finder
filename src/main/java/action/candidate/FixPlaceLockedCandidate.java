package action.candidate;

import action.cache.LockedCache;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import searcher.common.From;
import searcher.common.action.Action;

import java.util.HashSet;
import java.util.Set;

public class FixPlaceLockedCandidate implements Candidate<Action> {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final LockedCache lockedCache;
    private final Field fixedPlace;

    // temporary変数
    private int appearY = 0;

    // 引数fieldのblockが回転軸を許可する場所
    public FixPlaceLockedCandidate(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY, Field fixedPlace) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.lockedCache = new LockedCache(maxY);
        this.fixedPlace = fixedPlace;
    }

    @Override
    public Set<Action> search(Field field, Block block, int appearY) {
        // temporaryの初期化
        this.appearY = appearY;
        lockedCache.clear();

        HashSet<Action> actions = new HashSet<>();

        for (Rotate rotate : Rotate.values()) {
            Mino mino = minoFactory.create(block, rotate);
            for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                for (int y = appearY - mino.getMaxY() - 1; -mino.getMinY() <= y; y--) {
                    if (!fixedPlace.isEmpty(x, y) && field.canPutMino(mino, x, y) && field.isOnGround(mino, x, y)) {
                        if (check(field, mino, x, y, From.None)) {
                            Action action = minoShifter.createTransformedAction(block, x, y, rotate);
                            actions.add(action);
                        } else {
                            lockedCache.visit(x, y, rotate);
                        }
                    }
                }
            }
        }

        return actions;
    }

    private boolean check(Field field, Mino mino, int x, int y, From from) {
        // 一番上までたどり着いたとき
        if (appearY <= y)
            return true;

        Rotate rotate = mino.getRotate();

        // すでに訪問済みのとき
        if (lockedCache.isVisit(x, y, rotate))
            return lockedCache.isFound(x, y, rotate);  // その時の結果を返却。訪問済みだが結果が出てないときは他の探索でカバーできるためfalseを返却

        lockedCache.visit(x, y, rotate);

        // harddropでたどりつけるとき
        if (field.canReachOnHarddrop(mino, x, y)) {
            lockedCache.found(x, y, rotate);
            return true;
        }

        // 上に移動
        int upY = y + 1;
        if (upY < appearY && field.canPutMino(mino, x, upY)) {
            if (check(field, mino, x, upY, From.None)) {
                lockedCache.found(x, y, rotate);
                return true;
            }
        }

        // 左に移動
        int leftX = x - 1;
        if (from != From.Left && -mino.getMinX() <= leftX && field.canPutMino(mino, leftX, y)) {
            if (check(field, mino, leftX, y, From.Right)) {
                lockedCache.found(x, y, rotate);
                return true;
            }
        }

        // 右に移動
        int rightX = x + 1;
        if (from != From.Right && rightX < FIELD_WIDTH - mino.getMaxX() && field.canPutMino(mino, rightX, y)) {
            if (check(field, mino, rightX, y, From.Left)) {
                lockedCache.found(x, y, rotate);
                return true;
            }
        }

        // 右回転でくる可能性がある場所を移動
        if (checkRightRotation(field, mino, x, y)) {
            lockedCache.found(x, y, rotate);
            return true;
        }

        // 左回転でくる可能性がある場所を移動
        if (checkLeftRotation(field, mino, x, y)) {
            lockedCache.found(x, y, rotate);
            return true;
        }

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
                if (kicks != null && pattern[0] == kicks[0] && pattern[1] == kicks[1]) {
                    if (check(field, minoBefore, fromX, fromY, From.None)) {
                        lockedCache.found(x, y, currentRotate);
                        return true;
                    }
                }
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
                if (kicks != null && pattern[0] == kicks[0] && pattern[1] == kicks[1]) {
                    if (check(field, minoBefore, fromX, fromY, From.None)) {
                        lockedCache.found(x, y, currentRotate);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean canPutMinoInField(Field field, Mino mino, int x, int y) {
        return -mino.getMinX() <= x && x < FIELD_WIDTH - mino.getMaxX() && -mino.getMinY() <= y && field.canPutMino(mino, x, y);
    }
}
