package core.action.candidate;

import common.datastore.action.Action;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import searcher.common.From;

import java.util.HashSet;
import java.util.Set;

public class LimitIterationCandidate implements Candidate<Action> {
    private static final int FIELD_WIDTH = 10;

    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final MinoRotation minoRotation;
    private final int maxIteration;

    // temporary変数
    private int appearY = 0;

    public LimitIterationCandidate(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxIteration, boolean use180Rotation) {
        if (use180Rotation) {
            throw new UnsupportedOperationException("This class does not support 180 rotation.");
        }

        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.minoRotation = minoRotation;
        this.maxIteration = maxIteration;
    }

    @Override
    public Set<Action> search(Field field, Piece piece, int validHeight) {
        // temporaryの初期化
        this.appearY = validHeight;

        HashSet<Action> actions = new HashSet<>();

        for (Rotate rotate : Rotate.values()) {
            Mino mino = minoFactory.create(piece, rotate);
            for (int x = -mino.getMinX(); x < FIELD_WIDTH - mino.getMaxX(); x++) {
                for (int y = validHeight - mino.getMaxY() - 1; -mino.getMinY() <= y; y--) {
                    if (field.canPut(mino, x, y) && field.isOnGround(mino, x, y)) {
                        if (check(field, mino, x, y, From.None, 0)) {
                            Action action = minoShifter.createTransformedAction(piece, rotate, x, y);
                            actions.add(action);
                        }
                    }
                }
            }
        }

        return actions;
    }

    private boolean check(Field field, Mino mino, int x, int y, From from, int iteration) {
        // 一番上までたどり着いたとき
        if (appearY <= y)
            return true;

        // harddropでたどりつけるとき
        if (field.canReachOnHarddrop(mino, x, y))
            return true;

        // 規定回数以上動かした
        if (maxIteration <= iteration)
            return false;

        // 上に移動
        int upY = y + 1;
        if (upY < appearY && field.canPut(mino, x, upY))
            if (check(field, mino, x, upY, From.None, iteration + 1))
                return true;

        // 左に移動
        int leftX = x - 1;
        if (from != From.Left && 0 <= leftX && field.canPut(mino, leftX, y))
            if (check(field, mino, leftX, y, From.Right, iteration + 1))
                return true;

        // 右に移動
        int rightX = x + 1;
        if (from != From.Right && rightX < FIELD_WIDTH && field.canPut(mino, rightX, y))
            if (check(field, mino, rightX, y, From.Left, iteration + 1))
                return true;

        // 右回転でくる可能性がある場所を移動
        if (checkRightRotation(field, mino, x, y, iteration))
            return true;

        // 左回転でくる可能性がある場所を移動
        return checkLeftRotation(field, mino, x, y, iteration);
    }

    private boolean checkRightRotation(Field field, Mino mino, int x, int y, int iteration) {
        Mino minoBefore = minoFactory.create(mino.getPiece(), mino.getRotate().getLeftRotate());
        int[][] patterns = minoRotation.getRightPatternsFrom(minoBefore);  // 右回転前のテストパターンを取得
        for (int[] pattern : patterns) {
            int fromX = x - pattern[0];
            int fromY = y - pattern[1];
            if (canPutMinoInField(field, minoBefore, fromX, fromY)) {
                int[] kicks = minoRotation.getKicksWithRightRotation(field, minoBefore, mino, fromX, fromY);
                if (kicks != null && pattern[0] == kicks[0] && pattern[1] == kicks[1])
                    if (check(field, minoBefore, fromX, fromY, From.None, iteration + 1))
                        return true;
            }
        }
        return false;
    }

    private boolean checkLeftRotation(Field field, Mino mino, int x, int y, int iteration) {
        Mino minoBefore = minoFactory.create(mino.getPiece(), mino.getRotate().getRightRotate());
        int[][] patterns = minoRotation.getLeftPatternsFrom(minoBefore);  // 右回転前のテストパターンを取得
        for (int[] pattern : patterns) {
            int fromX = x - pattern[0];
            int fromY = y - pattern[1];
            if (canPutMinoInField(field, minoBefore, fromX, fromY)) {
                int[] kicks = minoRotation.getKicksWithLeftRotation(field, minoBefore, mino, fromX, fromY);
                if (kicks != null && pattern[0] == kicks[0] && pattern[1] == kicks[1])
                    if (check(field, minoBefore, fromX, fromY, From.None, iteration + 1))
                        return true;
            }
        }
        return false;
    }

    private boolean canPutMinoInField(Field field, Mino mino, int x, int y) {
        return -mino.getMinX() <= x && x < FIELD_WIDTH - mino.getMaxX() && -mino.getMinY() <= y && field.canPut(mino, x, y);
    }
}
