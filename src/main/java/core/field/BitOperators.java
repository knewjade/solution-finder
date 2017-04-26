package core.field;

class BitOperators {
    // y行より1列ブロックをマスクを取得する（y行を含まない）
    static long getColumnBelowY(int maxY) {
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

    // yより下の行を選択するマスクを作成 (y行は含まない)
    static long getRowMaskBelowY(int y) {
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

    static int bitToY(long bit) {
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
