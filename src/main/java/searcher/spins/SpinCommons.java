package searcher.spins;

import core.field.Field;
import core.field.KeyOperators;
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
    public static boolean canTSpinWithFilledLine(Field fieldWithoutT, SimpleOriginalPiece operationT) {
        Field freeze = fieldWithoutT.freeze();

        // ラインを消去する
        long filledLineWithoutT = freeze.clearLineReturnKey();

        // 消去されたラインに合わせてyを移動
        Mino mino = operationT.getMino();
        int y = operationT.getY();
        int slideY = Long.bitCount(filledLineWithoutT & KeyOperators.getMaskForKeyBelowY(y + mino.getMinY()));

        return SpinCommons.canTSpin(freeze, operationT.getX(), y - slideY);
    }

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

        TSpins spin = getTSpin(spinResult, filledTFront);

        return new Spin(spin, name, clearedLine);
    }

    private static TSpins getTSpin(SpinResult spinResult, boolean filledTFront) {
        // 前提: Tスピンとなる条件（Tの隅に3つ以上ブロックが存在している）はこの時点で満たしている

        if (filledTFront) {
            // Tの凸側のブロックが両方揃っている
            return TSpins.Regular;
        }

        // Tの凸側のブロックが両方揃っていない
        if (spinResult.isPrivilegeSpins()) {
            // TSTフォームのような特権がある場合はRegularと判定する
            // e.g. SRSでは「接着時にTが横向き and 回転テストパターンが最後のケース」の場合はRegular
            return TSpins.Regular;
        }

        // 通常はMini
        return TSpins.Mini;
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

    // Tの凸側のブロックが両方とも埋まっているか
    // `true`のとき、T-SpinはRegularになる。
    // `false`のとき、MiniかRegularか判別するにはさらに条件が必要
    public static boolean isFilledTFront(Field field, Rotate rotate, int x, int y) {
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
}
