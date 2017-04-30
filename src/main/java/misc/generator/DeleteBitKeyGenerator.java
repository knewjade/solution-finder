package misc.generator;

import core.field.KeyOperators;

public class DeleteBitKeyGenerator {
    public static void main(String[] args) {
        System.out.println("switch (y) {");
        for (int y = 0; y < 24; y++) {
            long bitKey = KeyOperators.getMaskForKeyAboveY(y) & KeyOperators.getMaskForKeyBelowY(y + 1);
            assert Long.bitCount(bitKey) == 1;
            System.out.printf("    case %d:%n", y);
            System.out.printf("        return %dL;%n", bitKey);
        }
        System.out.println("}");
    }
}
