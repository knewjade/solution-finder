package common.datastore.blocks;

import core.mino.Piece;

import java.util.List;
import java.util.stream.Stream;

public interface Pieces {
    Piece[] getPieceArray();

    List<Piece> getPieces();

    Stream<Piece> blockStream();

    Pieces addAndReturnNew(List<Piece> pieces);

    Pieces addAndReturnNew(Piece piece);

    Pieces addAndReturnNew(Stream<Piece> blocks);
}
