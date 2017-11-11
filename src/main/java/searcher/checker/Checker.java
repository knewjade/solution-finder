package searcher.checker;

import common.datastore.blocks.Pieces;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Piece;
import common.datastore.Result;
import common.datastore.action.Action;

import java.util.List;

public interface Checker<T extends Action> {
    boolean check(Field initField, Pieces pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);

    boolean check(Field initField, List<Piece> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);

    boolean check(Field initField, Piece[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth);

    Result getResult();
}
