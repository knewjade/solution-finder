package core.field;

public class FieldHelper {
    // フィールド中で使用されている高さ
    public static int getUsingHeight(Field field) {
        int height = field.getMaxFieldHeight();
        for (int y = height - 1; 0 <= y; y -= 1) {
            if (field.existsBlockCountOnY(y)) {
                return y + 1;
            }
        }
        return -1;
    }
}
