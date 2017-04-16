package searcher.checkmate2;

import action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import searcher.common.Result;
import searcher.common.action.Action;
import searcher.common.validator.FullValidator;

import java.util.List;

public interface FullCheckmate<T extends Action> {
    List<Result> search(Field initField, List<Block> pieces, Candidate<T> candidate, FullValidator validator, int maxClearLine, int maxDepth);

    List<Result> search(Field initField, Block[] pieces, Candidate<T> candidate, FullValidator validator, int maxClearLine, int maxDepth);
}
