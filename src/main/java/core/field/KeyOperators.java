package core.field;

// TODO: write unittest
public class KeyOperators {
    static long getDeleteKey(long board) {
        long a1010101010 = 768614336404564650L;
        long b1 = (board & a1010101010) >>> 1 & board;
        long a0101010000 = 378672165735973200L;
        long b2 = (b1 & a0101010000) >>> 4 & b1;
        long a0000010100 = 22540009865236500L;
        long b3 = (b2 & a0000010100) >>> 2 & b2;
        long a0000000100 = 4508001973047300L;
        return (b3 & a0000000100) >>> 2 & b3;
    }

    // y行上のブロックは対象に含まない
    public static long getMaskForKeyBelowY(int y) {
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
    public static long getMaskForKeyAboveY(int y) {
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

    public static long getDeleteBitKey(int y) {
        switch (y) {
            case 0:
                return 1L;
            case 1:
                return 1024L;
            case 2:
                return 1048576L;
            case 3:
                return 1073741824L;
            case 4:
                return 1099511627776L;
            case 5:
                return 1125899906842624L;
            case 6:
                return 2L;
            case 7:
                return 2048L;
            case 8:
                return 2097152L;
            case 9:
                return 2147483648L;
            case 10:
                return 2199023255552L;
            case 11:
                return 2251799813685248L;
            case 12:
                return 4L;
            case 13:
                return 4096L;
            case 14:
                return 4194304L;
            case 15:
                return 4294967296L;
            case 16:
                return 4398046511104L;
            case 17:
                return 4503599627370496L;
            case 18:
                return 8L;
            case 19:
                return 8192L;
            case 20:
                return 8388608L;
            case 21:
                return 8589934592L;
            case 22:
                return 8796093022208L;
            case 23:
                return 9007199254740992L;
        }
        throw new IllegalArgumentException("No reachable");
    }
}
