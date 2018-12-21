package core.field;

public class BlockCounter {
    /**
     * @param field フィールドを表すビット列
     *              --- 最上位ビット ->
     *              0 1 2 3 4 5 6 7 8 9
     *              0 1 2 3 4 5 6 7 8 9
     *              0 1 2 3 4 5 6 7 8 9
     *              0 1 2 3 4 5 6 7 8 9
     *              0 1 2 3 4 5 6 7 8 9
     *              0 1 2 3 4 5 6 7 8 9
     *              <- 最下位ビット ---
     * @return 列ごとに6bitずつブロック数を集計したビット列
     * --- 最上位ビット ->
     * 5 5 5 5 5 5
     * 8 8 8 8 8 8
     * 2 2 2 2 2 2
     * 7 7 7 7 7 7
     * 1 1 1 1 1 1
     * 4 4 4 4 4 4
     * 9 9 9 9 9 9
     * 3 3 3 3 3 3
     * 6 6 6 6 6 6
     * 0 0 0 0 0 0
     * <- 最下位ビット ---
     */
    public static long countBlocks(long field) {
        long bits = swap(field);

        long count1 = (bits & 0b010101010101010101010101010101010101010101010101010101010101L)
                + ((bits & 0b101010101010101010101010101010101010101010101010101010101010L) >> 1);

        return (count1 & 0b000011000011000011000011000011000011000011000011000011000011L)
                + ((count1 & 0b001100001100001100001100001100001100001100001100001100001100L) >> 2)
                + ((count1 & 0b110000110000110000110000110000110000110000110000110000110000L) >> 4);
    }

    private static long swap(long bits) {
        // (1 1 3 3 5 5 7 7 9 9 0 0 2 2 4 4 6 6 8 8) x3
        long bitPair = BlockCounter.deltaSwap(bits, 0b000000000010101010100000000000101010101000000000001010101010L, 9);

        // (2 2 4 4 6 6 8 8 1 1 3 3 5 5 7 7 9 9) x3
        long bit1to9 = (bitPair & 0b111111111111111111000000000000000000000000000000000000000000L)
                | (bitPair << 2 & 0b111111111111111111000000000000000000000000L)
                | (bitPair << 4 & 0b111111111111111111000000L);

        // 0 0 0 0 0 0
        long bit0 = (bitPair >> 36 & 0b110000) | (bitPair >> 18 & 0b1100) | (bitPair & 0b11);

        // (2 2 4 4 6 6 8 8 1 1 3 3 5 5 7 7 9 9) x3
        // 0 0 0 0 0 0
        long bit0to9 = bit1to9 | bit0;

        // 2 2 2 2 6 6 8 8 8 8 3 3 5 5 5 5 9 9
        // 4 4 4 4 4 4 1 1 1 1 1 1 7 7 7 7 7 7
        // 2 2 6 6 6 6 8 8 3 3 3 3 5 5 9 9 9 9
        // 0 0 0 0 0 0
        long bitSwap1 = BlockCounter.deltaSwap(bit0to9, 0b000011000011000011001100001100001100000000L, 20);

        // 2 2 2 2 2 2 8 8 8 8 8 8 5 5 5 5 5 5
        // 4 4 4 4 4 4 1 1 1 1 1 1 7 7 7 7 7 7
        // 6 6 6 6 6 6 3 3 3 3 3 3 9 9 9 9 9 9
        // 0 0 0 0 0 0
        return BlockCounter.deltaSwap(bitSwap1, 0b000011000011000011000000L, 40);
    }

    private static long deltaSwap(long bits, long mask, int delta) {
        long x = (bits ^ (bits >> delta)) & mask;
        return bits ^ x ^ (x << delta);
    }

    public static long[] parseToArray(long bits) {
        return new long[]{
                (bits) & 0b111111L,
                (bits >> 5 * 6) & 0b111111L,
                (bits >> 7 * 6) & 0b111111L,
                (bits >> 2 * 6) & 0b111111L,
                (bits >> 4 * 6) & 0b111111L,
                (bits >> 9 * 6) & 0b111111L,
                (bits >> 1 * 6) & 0b111111L,
                (bits >> 6 * 6) & 0b111111L,
                (bits >> 8 * 6) & 0b111111L,
                (bits >> 3 * 6) & 0b111111L,
        };
    }
}
