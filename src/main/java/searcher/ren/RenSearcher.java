package searcher.ren;

import common.datastore.RenResult;
import common.datastore.blocks.Pieces;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Piece;

import java.util.List;

public interface RenSearcher<T> {
    List<RenResult> check(Field initField, Pieces pieces, Candidate<T> candidate, int maxDepth);

    List<RenResult> check(Field initField, List<Piece> pieces, Candidate<T> candidate, int maxDepth);

    List<RenResult> check(Field initField, Piece[] pieces, Candidate<T> candidate, int maxDepth);
}
