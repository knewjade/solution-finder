import core.action.reachable.LockedReachable;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.FieldFactory;
import core.field.SmallField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import misc.iterable.PermutationIterable;
import searcher.common.Operation;
import searcher.common.Operations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static searcher.common.OperationsFactory.createOperations;

public class Main2 {
    private static class Obj {
        private final Mino mino;
        private final int x;
        private final int y;
        private final long needKey;

        public Obj(Mino mino, int x, long needKey, int lowerY) {
            this.mino = mino;
            this.x = x;
            this.y = lowerY - mino.getMinY();
            this.needKey = needKey;
        }

        @Override
        public String toString() {
            return "Obj{" +
                    "mino=" + mino +
                    ", x=" + x +
                    ", y=" + y +
                    ", needKey=" + needKey +
                    '}';
        }
    }

    public static void main(String[] args) {
        Field fieldOrigin = FieldFactory.createField("" +
                "XXXX____XX" +
                "XXXX___XXX" +
                "XXXX__XXXX" +
                "XXXX___XXX" +
                ""
        );
//        Operations operations = createOperations("J,0,5,0", "T,2,5,1", "I,0,5,0");
        Operations operations = createOperations("T,R,4,1", "S,0,6,1", "Z,0,5,0");
        MinoFactory minoFactory = new MinoFactory();
        int height = 4;
        ArrayList<Obj> objs = createObjs(fieldOrigin, operations, minoFactory, height);

        System.out.println("---");
        for (Obj obj : objs) {
            System.out.println(obj);
        }

        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
        PermutationIterable<Obj> iterable = new PermutationIterable<>(objs, objs.size());
        for (List<Obj> list : iterable) {
            System.out.println(list.stream().map(o -> o.mino.getBlock().getName()).collect(Collectors.joining("")));
            boolean isSuccess = checkObjs(fieldOrigin, list, height, reachable);
            System.out.println(isSuccess);
        }
    }

    private static boolean checkObjs(Field fieldOrigin, List<Obj> objs, int height, Reachable reachable) {
        Field field = fieldOrigin.freeze(height);
        for (Obj obj : objs) {
            long deleteKey = field.clearLineReturnKey();
            if ((deleteKey & obj.needKey) != obj.needKey) {
                // 必要な列が消えていない
                return false;
            }

            int deletedLines = Long.bitCount(getMaskForKeyBelowY(obj.y) & deleteKey);
            Mino mino = obj.mino;
            int x = obj.x;
            int y = obj.y - deletedLines;

            if (field.isOnGround(mino, x, y) && field.canPutMino(mino, x, y) && reachable.checks(field, mino, x, y, height)) {
                field.putMino(mino, x, y);
                field.insertBlackLineWithKey(deleteKey);
            } else {
                return false;
            }
        }
        return true;
    }

    private static ArrayList<Obj> createObjs(Field fieldOrigin, Operations operations, MinoFactory minoFactory, int height) {
        ArrayList<Obj> objs = new ArrayList<>();
        Field field = fieldOrigin.freeze(height);
        for (Operation op : operations.getOperations()) {
            Mino mino = minoFactory.create(op.getBlock(), op.getRotate());
            int x = op.getX();
            int y = op.getY();

            long deleteKey = field.clearLineReturnKey();

            // 一番上と一番下のy座標を抽出
            SmallField vanilla = new SmallField();
            vanilla.putMino(mino, x, y);
            vanilla.insertWhiteLineWithKey(deleteKey);
            long board = vanilla.getBoard(0);
            long lowerBit = board & (-board);
            int lowerY = bitToY(lowerBit);

            board = board & (board - 1);  // 下から3bitオフする
            board = board & (board - 1);
            long upperBit = board & (board - 1);
            int upperY = bitToY(upperBit);

            long aboveLowerY = getMaskForKeyAboveY(lowerY);
            long belowUpperY = getMaskForKeyBelowY(upperY);
            long needKey = deleteKey & aboveLowerY & belowUpperY;

            Obj obj = new Obj(mino, x, needKey, lowerY);
            objs.add(obj);

            // 次のフィールドを作成
            field.putMino(mino, x, y);
            field.insertBlackLineWithKey(deleteKey);
        }
        return objs;
    }

    // y行上のブロックは対象に含まない
    private static long getMaskForKeyBelowY(int y) {
        switch (y) {
            case 0:
                return 0L;
            case 1:
                return 1L;
            case 2:
                return 0x401L;
            case 3:
                return 0x100401L;
            case 4:
                return 0x40100401L;
            case 5:
                return 0x10040100401L;
            case 6:
                return 0x4010040100401L;
            case 7:
                return 0x4010040100403L;
            case 8:
                return 0x4010040100c03L;
            case 9:
                return 0x4010040300c03L;
            case 10:
                return 0x40100c0300c03L;
            case 11:
                return 0x40300c0300c03L;
            case 12:
                return 0xc0300c0300c03L;
            case 13:
                return 0xc0300c0300c07L;
            case 14:
                return 0xc0300c0301c07L;
            case 15:
                return 0xc0300c0701c07L;
            case 16:
                return 0xc0301c0701c07L;
            case 17:
                return 0xc0701c0701c07L;
            case 18:
                return 0x1c0701c0701c07L;
            case 19:
                return 0x1c0701c0701c0fL;
            case 20:
                return 0x1c0701c0703c0fL;
            case 21:
                return 0x1c0701c0f03c0fL;
            case 22:
                return 0x1c0703c0f03c0fL;
            case 23:
                return 0x1c0f03c0f03c0fL;
            case 24:
                return 0x3c0f03c0f03c0fL;
        }
        throw new IllegalArgumentException("No reachable");
    }

    // y行上のブロックは対象に含む
    private static long getMaskForKeyAboveY(int y) {
        switch (y) {
            case 0:
                return 0x3c0f03c0f03c0fL;
            case 1:
                return 0x3c0f03c0f03c0eL;
            case 2:
                return 0x3c0f03c0f0380eL;
            case 3:
                return 0x3c0f03c0e0380eL;
            case 4:
                return 0x3c0f0380e0380eL;
            case 5:
                return 0x3c0e0380e0380eL;
            case 6:
                return 0x380e0380e0380eL;
            case 7:
                return 0x380e0380e0380cL;
            case 8:
                return 0x380e0380e0300cL;
            case 9:
                return 0x380e0380c0300cL;
            case 10:
                return 0x380e0300c0300cL;
            case 11:
                return 0x380c0300c0300cL;
            case 12:
                return 0x300c0300c0300cL;
            case 13:
                return 0x300c0300c03008L;
            case 14:
                return 0x300c0300c02008L;
            case 15:
                return 0x300c0300802008L;
            case 16:
                return 0x300c0200802008L;
            case 17:
                return 0x30080200802008L;
            case 18:
                return 0x20080200802008L;
            case 19:
                return 0x20080200802000L;
            case 20:
                return 0x20080200800000L;
            case 21:
                return 0x20080200000000L;
            case 22:
                return 0x20080000000000L;
            case 23:
                return 0x20000000000000L;
            case 24:
                return 0L;
        }
        throw new IllegalArgumentException("No reachable");
    }

    private static int bitToY(long bit) {
        assert bit < 0x100000000000000L;
        if (bit < 0x40000000L) {
            if (bit < 0x400L)
                return 0;
            else if (bit < 0x100000L)
                return 1;
            return 2;
        } else {
            if (bit < 0x10000000000L)
                return 3;
            else if (bit < 0x4000000000000L)
                return 4;
            return 5;
        }
    }
}
