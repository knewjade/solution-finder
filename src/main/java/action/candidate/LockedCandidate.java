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

/**
 * マルチスレッド非対応
 */
public class LockedCandidate implements Candidate<Action> {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final LockedCache lockedCache;

    private final Mino[] minoStore = new Mino[4];  // ミノを一時的に保存しておく変数
    private final int[][] minMaxStore = new int[4][4];  // 境界値を一時的に保存しておく変数 0:minX, 1:maxX, 2:minY, 3:maxY

    // temporary変数
    private int appearY = 0;

    public LockedCandidate(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxY) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.lockedCache = new LockedCache(maxY);
    }

    @Override
    public Set<Action> search(Field field, Block block, int appearY) {
        // temporaryの初期化
        this.appearY = appearY;
        lockedCache.clear();

        // ミノと境界値の取得
        for (Rotate rotate : Rotate.values()) {
            Mino mino = minoFactory.create(block, rotate);
            int index = rotate.getNumber();
            minoStore[index] = mino;
            minMaxStore[index][0] = -mino.getMinX(); //include
            minMaxStore[index][1] = FIELD_WIDTH - mino.getMaxX();  // exclude
            minMaxStore[index][2] = -mino.getMinY();  // include
            minMaxStore[index][3] = appearY - mino.getMaxY();  // exclude
        }

        // 探索
        HashSet<Action> actions = new HashSet<>();
        for (int y = 0; y < appearY; y++) {
            for (int minoIndex = 0, minoStoreLength = minoStore.length; minoIndex < minoStoreLength; minoIndex++) {
                // yが適正範囲
                if (minMaxStore[minoIndex][2] <= y && y < minMaxStore[minoIndex][3]) {
                    Mino mino = minoStore[minoIndex];
                    Rotate rotate = mino.getRotate();
                    // 適正範囲のxを探索
                    for (int x = minMaxStore[minoIndex][0]; x < minMaxStore[minoIndex][1]; x++) {
                        if (field.canPutMino(mino, x, y) && field.isOnGround(mino, x, y)) {
                            if (check(field, mino, x, y, From.None)) {
                                Action action = minoShifter.createTransformedAction(block, x, y, rotate);
                                actions.add(action);
                            } else {
                                lockedCache.visit(x, y, rotate);
                            }
                            lockedCache.resetTrail();
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
        if (lockedCache.isVisit(x, y, rotate)) {
            // 結果が確定済みなら、その時の結果を返却
            // 訪問済みだが結果が出てないときは他の探索でカバーできるためfalseを返却
            return lockedCache.isFound(x, y, rotate);
        }

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
