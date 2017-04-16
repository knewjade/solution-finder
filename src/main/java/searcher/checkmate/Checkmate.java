package searcher.checkmate;

import action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import searcher.common.Result;
import searcher.common.action.Action;

import java.util.List;

public interface Checkmate<T extends Action> {
    List<Result> search(Field initField, List<Block> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);

    List<Result> search(Field initField, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);
}
