package _experimental.newfield;

import core.field.Field;

public class ParityField {
    private final Field field;

    public ParityField(Field field) {
        this.field = field;
    }

    public int calculateOddColumnParity() {
        long mask = 0x555555555555555L;
        int count = 0;
        for (int index = 0, max = field.getBoardCount(); index < max; index++)
            count += Long.bitCount(field.getBoard(index) & mask);
        return count;
    }

    public int calculateEvenColumnParity() {
        long mask = 0xaaaaaaaaaaaaaaaL;
        int count = 0;
        for (int index = 0, max = field.getBoardCount(); index < max; index++)
            count += Long.bitCount(field.getBoard(index) & mask);
        return count;
    }

    public int calculateOddParity() {
        long mask = 0xaa955aa955aa955L;
        int count = 0;
        for (int index = 0, max = field.getBoardCount(); index < max; index++)
            count += Long.bitCount(field.getBoard(index) & mask);
        return count;
    }

    public int calculateEvenParity() {
        long mask = 0x556aa556aa556aaL;
        int count = 0;
        for (int index = 0, max = field.getBoardCount(); index < max; index++)
            count += Long.bitCount(field.getBoard(index) & mask);
        return count;
    }
    @Override
    public String toString() {
        return "ParityField{" +
                "OddLine=" + calculateOddColumnParity() + "," +
                "EvenLine=" + calculateEvenColumnParity() +
                '}';
    }
}
