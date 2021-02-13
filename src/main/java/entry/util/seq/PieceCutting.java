package entry.util.seq;

import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import core.mino.Piece;

import java.util.stream.Stream;

interface PieceCutting {
    Pieces get(Pieces pieces);

    Pieces get(Stream<Piece> pieces);

    int toDepth(int fromDepth);
}

class UsePieceCutting implements PieceCutting {
    private final int limit;

    public UsePieceCutting(int limit) {
        assert 0 < limit;
        this.limit = limit;
    }

    @Override
    public Pieces get(Pieces pieces) {
        return get(pieces.blockStream());
    }

    @Override
    public Pieces get(Stream<Piece> pieces) {
        return new LongPieces(pieces.limit(limit));
    }

    @Override
    public int toDepth(int fromDepth) {
        return Math.min(fromDepth, limit);
    }
}

class NoPieceCutting implements PieceCutting {
    @Override
    public Pieces get(Pieces pieces) {
        return pieces;
    }

    @Override
    public Pieces get(Stream<Piece> pieces) {
        return new LongPieces(pieces);
    }

    @Override
    public int toDepth(int fromDepth) {
        return fromDepth;
    }
}
