package common.pattern;

import common.datastore.PieceCounter;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import core.mino.Piece;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class SingleElement implements Element {
    private final Piece piece;

    SingleElement(Piece piece) {
        this.piece = piece;
    }

    @Override
    public int getPopCount() {
        return 1;
    }

    @Override
    public List<Pieces> getPermutationBlocks() {
        LongPieces blocks = new LongPieces(Stream.of(piece));
        return Collections.singletonList(blocks);
    }

    @Override
    public List<PieceCounter> getPieceCounters() {
        return Collections.singletonList(new PieceCounter(Stream.of(piece)));
    }
}
