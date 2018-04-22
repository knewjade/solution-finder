package entry.setup.filters;

import core.field.Field;
import searcher.common.From;

public class StrictHolesWithMarginFilter implements SetupSolutionFilter {
    private static final int FIELD_WIDTH = 10;

    private final int maxHeight;
    private final Field marginField;

    public StrictHolesWithMarginFilter(int maxHeight, Field marginField) {
        this.maxHeight = maxHeight;
        this.marginField = marginField;
    }

    @Override
    public boolean test(SetupResult result) {
        Field freeze = result.getTestField().freeze(maxHeight);
        for (int x = 0; x < FIELD_WIDTH; x++)
            putAndMove(freeze, x, maxHeight - 1, From.None);

        // マージン上で空白の部分（ホール）があれば塗りつぶす
        freeze.merge(marginField);

        // すべてが塗りつぶされていないときは除外
        return freeze.getNumOfAllBlocks() == maxHeight * 10;
    }

    private void putAndMove(Field field, int x, int y, From from) {
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
