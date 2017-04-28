package newfield;

import core.field.Field;

public class ParityField {
    private final long[] boards;

    ParityField(Field field) {
        int boardCount = field.getBoardCount();
        long[] boards = new long[boardCount];
        for (int index = 0; index < boardCount; index++)
            boards[index] = field.getBoard(index);
        this.boards = boards;
    }

    int calculateOddLineParity() {
        long mask = 0x555555555555555L;
        int count = 0;
        for (long board : boards)
            count += Long.bitCount(board & mask);
        return count;
    }

    int calculateEvenLineParity() {
        long mask = 0xaaaaaaaaaaaaaaaL;
        int count = 0;
        for (long board : boards)
            count += Long.bitCount(board & mask);
        return count;
    }

    @Override
    public String toString() {
        return "ParityField{" +
                "OddLine=" + calculateOddLineParity() + "," +
                "EvenLine=" + calculateEvenLineParity() +
                '}';
    }
}
