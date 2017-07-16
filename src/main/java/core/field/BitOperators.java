package core.field;

class BitOperators {
    // y行より下の1列ブロックマスクを取得する（y行を含まない）
    static long getColumnOneLineBelowY(int maxY) {
        assert 0 <= maxY && maxY <= 6;
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
        assert 0 <= minX && minX <= 10;
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

    // yより下の行を選択するマスクを作成 (y行は含まない)
    static long getRowMaskBelowY(int y) {
        assert 0 <= y && y <= 6;
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
        assert 0 <= y && y <= 6;
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

    // 1ビットがオンになっているとき、そのビットのy座標を返却
    static int bitToY(long bit) {
        assert Long.bitCount(bit) == 1;
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
}
