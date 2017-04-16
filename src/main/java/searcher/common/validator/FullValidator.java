package searcher.common.validator;

import core.field.Field;
import core.mino.Block;
import searcher.common.action.Action;

public interface FullValidator {
    int FIELD_WIDTH = 10;

    // 最終条件を満たしたフィールドなら true を返却
    boolean satisfies(Field currentField, Field nextField, int maxY, Block block, Action action, int depth);

    // 有効なフィールドなら true を返却。false なら枝刈り対象
    boolean validate(Field currentField, Field nextField, int maxY, Block block, Action action, int depth);
}
