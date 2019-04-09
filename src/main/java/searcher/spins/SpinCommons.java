package searcher.spins;

import core.field.Field;
import core.mino.Mino;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;
import core.srs.RotateDirection;
import core.srs.SpinResult;
import searcher.spins.spin.Spin;
import searcher.spins.spin.TSpinNames;
import searcher.spins.spin.TSpins;

import java.util.stream.Stream;

public class SpinCommons {
    public static boolean existsOnGround(Field initField, Field allMergedField, long allMergedFillLine, long onePieceFilledLine, SimpleOriginalPiece operation) {
        long usingKey = operation.getUsingKey();

        // operationで使われているラインは揃わない
        long fillLine = allMergedFillLine & ~usingKey;

        // operationを置くのに消えている必要があるライン
        long needDeletedKey = operation.getNeedDeletedKey();
        if ((fillLine & needDeletedKey) != needDeletedKey) {
            return false;
        }

        // operationより下で消えるラインで、1ミノで即消えるライン上にはおけないので消去する
        long onePieceFilledBelowOperation = (~usingKey & (usingKey - 1)) & onePieceFilledLine;
        Mino mino = operation.getMino();
        int x = operation.getX();
        int y = operation.getY();

        // 最初から置くことができる
        {
            Field freeze = initField.freeze();
            freeze.deleteLineWithKey(needDeletedKey);
            freeze.remove(mino, x, y);
            boolean isOnGround = freeze.isOnGround(mino, x, y);
            if (isOnGround) {
                return true;
            }
        }

        // operationが地面の上なのか
        {
            Field freeze = allMergedField.freeze();
            freeze.deleteLineWithKey(needDeletedKey | onePieceFilledBelowOperation);
            int ny = y - Long.bitCount(onePieceFilledBelowOperation);
            assert 0 <= ny + mino.getMinY();
            freeze.remove(mino, x, ny);
            boolean isOnGround = freeze.isOnGround(mino, x, ny);
            if (isOnGround) {
                return true;
            }
        }

        return false;
    }

    // Tスピンか判定
    public static boolean canTSpin(Field field, int x, int y) {
        return 3L <= Stream.of(
                isBlock(field, x - 1, y - 1),
                isBlock(field, x - 1, y + 1),
                isBlock(field, x + 1, y - 1),
                isBlock(field, x + 1, y + 1)
        ).filter(Boolean::booleanValue).count();
    }

    private static boolean isBlock(Field field, int x, int y) {
        if (x < 0 || 10 <= x || y < 0) {
            return true;
        }
        return !field.isEmpty(x, y);
    }

    public static Spin getSpins(Field before, SpinResult spinResult, int clearedLine) {
        assert spinResult != SpinResult.NONE;
//        assert canTSpin(before, spinResult.getToX(), spinResult.getToY());

        Rotate toRotate = spinResult.getToRotate();
        int toX = spinResult.getToX();
        int toY = spinResult.getToY();

        boolean filledTFront = isFilledTFront(before, toRotate, toX, toY);

        RotateDirection direction = spinResult.getDirection();
        TSpinNames name = getTSpinName(spinResult, toRotate, filledTFront, direction);

        TSpins spin = getTSpin(spinResult, toRotate, filledTFront, name);

        return new Spin(spin, name, clearedLine);
    }

    private static TSpins getTSpin(SpinResult spinResult, Rotate toRotate, boolean filledTFront, TSpinNames name) {
        if (name != TSpinNames.NoName) {
            return TSpins.Regular;
        }

        if (!filledTFront) {
            // 正面側に2つブロックがない
            // Mini判定の可能性がある
            if (isHorizontal(toRotate) || spinResult.getTestPatternIndex() != 4) {
                // 接着時にTが横向き or 回転テストパターンが最後のケースではない
                return TSpins.Mini;
            }

            // TSTの形のみ、Regularとなる
        }

        return TSpins.Regular;
    }

    private static TSpinNames getTSpinName(SpinResult spinResult, Rotate toRotate, boolean filledTFront, RotateDirection direction) {
        if ((direction == RotateDirection.Left && toRotate == Rotate.Right) || (direction == RotateDirection.Right && toRotate == Rotate.Left)) {
            // 裏返した状態から回転させたとき
            switch (spinResult.getTestPatternIndex()) {
                case 3: {
                    if (filledTFront) {
                        // 正面側に2つブロックがある
                        return TSpinNames.Iso;
                    }
                    return TSpinNames.Neo;
                }
                case 4: {
                    return TSpinNames.Fin;
                }
            }
        }

        return TSpinNames.NoName;
    }

    private static boolean isFilledTFront(Field field, Rotate rotate, int x, int y) {
        switch (rotate) {
            case Spawn: {
                return isBlock(field, x - 1, y + 1) && isBlock(field, x + 1, y + 1);
            }
            case Reverse: {
                return isBlock(field, x - 1, y - 1) && isBlock(field, x + 1, y - 1);
            }
            case Left: {
                return isBlock(field, x - 1, y - 1) && isBlock(field, x - 1, y + 1);
            }
            case Right: {
                return isBlock(field, x + 1, y - 1) && isBlock(field, x + 1, y + 1);
            }
        }
        throw new IllegalStateException();
    }

    private static boolean isHorizontal(Rotate rotate) {
        return rotate == Rotate.Spawn || rotate == Rotate.Reverse;
    }
}
