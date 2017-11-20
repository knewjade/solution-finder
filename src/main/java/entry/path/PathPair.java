package entry.path;

import common.datastore.PieceCounter;
import common.datastore.OperationWithKey;
import common.datastore.blocks.LongPieces;
import core.mino.Piece;
import searcher.pack.task.Result;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathPair implements HaveSet<LongPieces> {
    static final PathPair EMPTY_PAIR = new PathPair(null, new HashSet<>(), null, "", Collections.emptyList());

    private final Result result;
    private final HashSet<LongPieces> piecesSolution;
    private final HashSet<LongPieces> piecesPattern;
    private final String fumen;
    private final List<OperationWithKey> sampleOperations;
    private final boolean deletedLine;

    public PathPair(Result result, HashSet<LongPieces> piecesSolution, HashSet<LongPieces> piecesPattern, String fumen, List<OperationWithKey> sampleOperations) {
        this.result = result;
        this.piecesSolution = piecesSolution;
        this.piecesPattern = piecesPattern;
        this.fumen = fumen;
        this.sampleOperations = sampleOperations;
        this.deletedLine = result != null && containsDeletedLine();
    }

    private boolean containsDeletedLine() {
        return result.getMemento().getRawOperationsStream()
                .anyMatch(operationWithKey -> operationWithKey.getNeedDeletedKey() != 0L);
    }

    @Override
    public Set<LongPieces> getSet() {
        return blocksHashSetForPattern();
    }

    public Result getResult() {
        return result;
    }

    public String getFumen() {
        return fumen;
    }

    public Stream<LongPieces> blocksStreamForPattern() {
        return piecesPattern.stream();
    }

    public Stream<LongPieces> blocksStreamForSolution() {
        return piecesSolution.stream();
    }

    public HashSet<LongPieces> blocksHashSetForPattern() {
        return piecesPattern;
    }

    public HashSet<LongPieces> blocksHashSetForSolution() {
        return piecesSolution;
    }

    public List<OperationWithKey> getSampleOperations() {
        return sampleOperations;
    }

    public String getUsingBlockName() {
        return sampleOperations.stream()
                .map(OperationWithKey::getPiece)
                .sorted()
                .map(Piece::getName)
                .collect(Collectors.joining());
    }

    public int getPatternSize() {
        return blocksHashSetForPattern().size();
    }

    public PieceCounter getBlockCounter() {
        return new PieceCounter(sampleOperations.stream().map(OperationWithKey::getPiece));
    }

    public boolean isDeletedLine() {
        return deletedLine;
    }
}
