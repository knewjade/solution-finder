package searcher;

import searcher.core.ValidationParameter;
import searcher.core.ValidationResultState;

public interface SearchValidator {
    // 最終条件を満たしたフィールドなら Result を返却
    // 最終的な解ではないが、有効なフィールドなら Valid を返却
    // Prune なら枝刈り対象
    ValidationResultState check(ValidationParameter parameter);
}
