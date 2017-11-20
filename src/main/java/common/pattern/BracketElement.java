package common.pattern;

import common.SyntaxException;
import common.datastore.PieceCounter;
import common.datastore.blocks.Pieces;
import common.datastore.blocks.LongPieces;
import common.iterable.CombinationIterable;
import common.iterable.PermutationIterable;
import core.mino.Piece;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BracketElement implements Element {
    private final int size;
    private final List<Pieces> permutationBlocks;
    private final List<PieceCounter> pieceCounters;

    BracketElement(Token token, int size) throws SyntaxException {
        HashSet<Piece> pieceSet = parseBlockSet(token);

        if (pieceSet.isEmpty())
            throw new SyntaxException("Empty in []", token.getLastIndex());

        if (size <= 0)
            throw new SyntaxException("no pop", token.getLastIndex());
        else if (pieceSet.size() < size)
            throw new SyntaxException("over pop", token.getLastIndex());

        this.size = size;
        this.permutationBlocks = createPermutationBlocks(pieceSet, size);
        this.pieceCounters = createBlockCounters(pieceSet, size);
    }

    private HashSet<Piece> parseBlockSet(Token token) throws SyntaxException {
        HashSet<Piece> pieces = new HashSet<>();
        while (token.isContinue()) {
            Piece piece = token.nextBlock();
            if (pieces.contains(piece))
                throw new SyntaxException(String.format("Duplicate '%s' pieces in []", piece.getName()), token.getLastIndex());
            pieces.add(piece);
        }
        return pieces;
    }

    private ArrayList<Pieces> createPermutationBlocks(HashSet<Piece> pieceList, int size) {
        PermutationIterable<Piece> iterable = new PermutationIterable<>(pieceList, size);
        ArrayList<Pieces> piecesList = new ArrayList<>();
        for (List<Piece> permutation : iterable)
            piecesList.add(new LongPieces(permutation));
        return piecesList;
    }

    private ArrayList<PieceCounter> createBlockCounters(HashSet<Piece> pieceList, int size) {
        CombinationIterable<Piece> iterable = new CombinationIterable<>(pieceList, size);
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
