package searcher.checker;

import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import searcher.common.Result;
import common.datastore.action.Action;

import java.util.List;

public interface Checker<T extends Action> {
    boolean check(Field initField, List<Block> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);

    boolean check(Field initField, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);

    Result getResult();
}
