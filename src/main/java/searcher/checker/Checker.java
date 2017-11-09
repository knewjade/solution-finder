package searcher.checker;

import common.datastore.Result;
import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Piece;

import java.util.List;

public interface Checker {
    boolean check(Field initField, List<Piece> pieces, Candidate<? extends Action> candidate, int maxClearLine, int maxDepth);

    boolean check(Field initField, Piece[] pieces, Candidate<? extends Action> candidate, int maxClearLine, int maxDepth);

    Result getResult();
}
