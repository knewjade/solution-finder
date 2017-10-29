package searcher.checkmate;

import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Piece;
import common.datastore.Result;
import common.datastore.action.Action;

import java.util.List;

public interface Checkmate<T extends Action> {
    List<Result> search(Field initField, List<Piece> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);

    List<Result> search(Field initField, Piece[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);
}
