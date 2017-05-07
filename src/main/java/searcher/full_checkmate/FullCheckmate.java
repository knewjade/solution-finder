package searcher.full_checkmate;

import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import common.datastore.Result;
import common.datastore.action.Action;
import searcher.common.validator.FullValidator;

import java.util.List;

// 探索時のチェックに通常より多くの情報にアクセスできるサーチャー
public interface FullCheckmate<T extends Action> {
    List<Result> search(Field initField, List<Block> pieces, Candidate<T> candidate, FullValidator validator, int maxClearLine, int maxDepth);

    List<Result> search(Field initField, Block[] pieces, Candidate<T> candidate, FullValidator validator, int maxClearLine, int maxDepth);
}
