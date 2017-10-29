package common.pattern;

import common.datastore.PieceCounter;
import common.datastore.blocks.Pieces;
import common.datastore.blocks.LongPieces;
import common.iterable.CombinationIterable;
import common.iterable.PermutationIterable;
import core.mino.Piece;

import java.util.ArrayList;
import java.util.List;

public class WildcardElement implements Element {
    private final int size;
    private final List<Pieces> permutationBlocks;
    private final List<PieceCounter> pieceCounters;

    WildcardElement(int size) {
        this.size = size;
        this.permutationBlocks = createPermutationBlocks(size);
        this.pieceCounters = createBlockCounters(size);
    }

    private ArrayList<Pieces> createPermutationBlocks(int size) {
        PermutationIterable<Piece> iterable = new PermutationIterable<>(Piece.valueList(), size);
        ArrayList<Pieces> piecesList = new ArrayList<>();
        for (List<Piece> permutation : iterable)
            piecesList.add(new LongPieces(permutation));
        return piecesList;
    }

    private ArrayList<PieceCounter> createBlockCounters(int size) {
        CombinationIterable<Piece> iterable = new CombinationIterable<>(Piece.valueList(), size);
        ArrayList<PieceCounter> pieceCounterList = new ArrayList<>();
        for (List<Piece> combination : iterable)
            pieceCounterList.add(new PieceCounter(combination));
        return pieceCounterList;
    }

    @Override
    public int getPopCount() {
        return size;
    }

    @Override
    public List<Pieces> getPermutationBlocks() {
        return permutationBlocks;
    }

    @Override
    public List<PieceCounter> getPieceCounters() {
        return pieceCounters;
    }
}
