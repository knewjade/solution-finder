package core.field;

public class BitOperators {
    // y行より下の1列ブロックマスクを取得する（y行を含まない）
    static long getColumnOneLineBelowY(int maxY) {
        assert 0 <= maxY && maxY <= 6 : maxY;
        switch (maxY) {
            case 0:
                return 0;
            case 1:
                return 0x1L;
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
        }
        throw new IllegalStateException("No reachable");
    }

    // x列より右の列を選択するマスクを作成（x列を含む）
    static long getColumnMaskRightX(int minX) {
        assert 0 <= minX && minX <= 10 : minX;
        switch (minX) {
            case 0:
                return 0xfffffffffffffffL;
            case 1:
                return 0xffbfeffbfeffbfeL;
            case 2:
                return 0xff3fcff3fcff3fcL;
            case 3:
                return 0xfe3f8fe3f8fe3f8L;
            case 4:
                return 0xfc3f0fc3f0fc3f0L;
            case 5:
                return 0xf83e0f83e0f83e0L;
            case 6:
                return 0xf03c0f03c0f03c0L;
            case 7:
                return 0xe0380e0380e0380L;
            case 8:
                return 0xc0300c0300c0300L;
            case 9:
                return 0x802008020080200L;
            case 10:
                return 0L;
        }
        throw new IllegalStateException("No reachable");
    }

    // TODO: write unittest
    // x列より左の列を選択するマスクを作成（x列を含む）
    static long getColumnMaskLeftX(int minX) {
        assert 0 <= minX && minX <= 10 : minX;
        switch (minX) {
            case 0:
                return 0L;
            case 1:
                return 0x004010040100401L;
            case 2:
                return 0x00c0300c0300c03L;
            case 3:
                return 0x01c0701c0701c07L;
            case 4:
                return 0x03c0f03c0f03c0fL;
            case 5:
                return 0x07c1f07c1f07c1fL;
            case 6:
                return 0x0fc3f0fc3f0fc3fL;
            case 7:
                return 0x1fc7f1fc7f1fc7fL;
            case 8:
                return 0x3fcff3fcff3fcffL;
            case 9:
                return 0x7fdff7fdff7fdffL;
            case 10:
                return 0xfffffffffffffffL;
        }
        throw new IllegalStateException("No reachable: " + minX);
    }

    // yより下の行を選択するマスクを作成 (y行は含まない)
    static long getRowMaskBelowY(int y) {
        assert 0 <= y && y <= 6 : y;
        switch (y) {
            case 0:
                return 0L;
            case 1:
                return 0x3ffL;
            case 2:
                return 0xfffffL;
            case 3:
                return 0x3fffffffL;
            case 4:
                return 0xffffffffffL;
            case 5:
                return 0x3ffffffffffffL;
            case 6:
                return 0xfffffffffffffffL;
        }
        throw new IllegalArgumentException("No reachable");
    }

    // yより上の行を選択するマスクを作成 (y行を含む)
    static long getRowMaskAboveY(int y) {
        assert 0 <= y && y <= 6 : y;
        switch (y) {
            case 0:
                return 0xfffffffffffffffL;
            case 1:
                return 0xffffffffffffc00L;
            case 2:
                return 0xffffffffff00000L;
            case 3:
                return 0xfffffffc0000000L;
            case 4:
                return 0xfffff0000000000L;
            case 5:
                return 0xffc000000000000L;
            case 6:
                return 0L;
        }
        throw new IllegalArgumentException("No reachable");
    }

    // boardのうち1ビットがオンになっているとき、そのビットのy座標を返却
    static int bitToY(long bit) {
        assert Long.bitCount(bit) == 1 : bit;
        assert bit < (1L << 60);
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

    // x列とその左の列の間が壁（隙間がない）とき true を返却。1 <= xであること
    static boolean isWallBetweenLeft(int x, int maxY, long board) {
        long maskHigh = BitOperators.getColumnOneLineBelowY(maxY);
        long reverseXBoardHigh = ~board;
        long columnHigh = maskHigh << x;
        long rightHigh = reverseXBoardHigh & columnHigh;
        long leftHigh = reverseXBoardHigh & (columnHigh >>> 1);
        return ((leftHigh << 1) & rightHigh) == 0L;
    }

    public static int bitToX(long bit) {
        assert Long.bitCount(bit) == 1 : bit;
        assert bit < (1L << 10) : Long.toBinaryString(bit);
        if (bit < 0b10000L) {  // 0-3
            if (bit < 0b100L) {  // 0 or 1
                if (bit < 0b10L)
                    return 0;
                return 1;
            } else {  // 2 or 3
                if (bit < 0b1000L)
                    return 2;
                return 3;
            }
        } else {  // 4-9
            if (bit < 0b1000000L) {  // 4 or 5
                if (bit < 0b100000L)
                    return 4;
                return 5;
            } else {  // 6-9
                if (bit < 0b100000000L) { // 6-7
                    if (bit < 0b10000000L)
                        return 6;
                    return 7;
                } else { // 8-9
                    if (bit < 0b1000000000L)
                        return 8;
                    return 9;
                }
            }
        }
    }
}
