package util.fig;

import core.mino.Piece;

import java.util.LinkedList;
import java.util.List;

public class Bag {
    private final LinkedList<Piece> pieces;
    private Piece hold;

    public Bag(List<Piece> pieces, Piece hold) {
        this.pieces = new LinkedList<>(pieces);
        this.hold = hold;
    }

    public void use(Piece piece) {
        if (piece == null)
            return;

        Piece peek = pieces.peek();
        if (piece == peek) {
            pieces.pollFirst();
        } else if (piece == hold) {
            hold = pieces.pollFirst();
        } else if (hold == null) {
            hold = pieces.pollFirst();
            use(piece);
        } else {
            throw new IllegalStateException("No reachable");
        }
    }

    @Override
    public String toString() {
        return "Bag{" +
                "pieces=" + pieces +
                ", hold=" + hold +
                '}';
    }

    public List<Piece> getNext(int nextBoxCount) {
        if (nextBoxCount <= pieces.size())
            return pieces.subList(0, nextBoxCount);
        return pieces;
    }

    public Piece getHold() {
        return hold;
    }
}
