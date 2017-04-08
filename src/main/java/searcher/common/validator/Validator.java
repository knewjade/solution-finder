package searcher.common.validator;

import core.field.Field;

public interface Validator {
    int FIELD_WIDTH = 10;

    // 最終条件を満たしたフィールドなら true を返却
    boolean satisfies(Field field, int maxClearLine);

    // 有効なフィールドなら true を返却。false なら枝刈り対象
    boolean validate(Field field, int maxClearLine);
}
