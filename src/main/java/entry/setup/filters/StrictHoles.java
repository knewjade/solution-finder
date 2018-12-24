package entry.setup.filters;

import core.field.Field;
import searcher.common.From;

public class StrictHoles {
    private static final int FIELD_WIDTH = 10;

    public static Field fill(Field field, int maxHeight) {
        Field freeze = field.freeze(maxHeight);
        for (int x = 0; x < FIELD_WIDTH; x++)
            putAndMove(freeze, x, maxHeight - 1, From.None);
        return freeze;
    }

    private static void putAndMove(Field field, int x, int y, From from) {
        // 壁なら終了
        if (!field.isEmpty(x, y))
            return;

        // 自分自身を塗りつぶす
        field.setBlock(x, y);

        // 移動する
        if (0 <= y - 1) {
            putAndMove(field, x, y - 1, From.None);
        }

        if (from != From.Right && x + 1 < FIELD_WIDTH) {
            putAndMove(field, x + 1, y, From.Left);
        }

        if (from != From.Left && 0 <= x - 1) {
            putAndMove(field, x - 1, y, From.Right);
        }
    }
}
