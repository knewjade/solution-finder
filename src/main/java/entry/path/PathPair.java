package entry.path;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.PieceCounter;
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
    static final PathPair EMPTY_PAIR = new PathPair(null, new HashSet<>(), null, "", Collections.emptyList(), new HashSet<>());

    private final Result result;
    private final HashSet<LongPieces> piecesSolution;
    private final HashSet<LongPieces> piecesPattern;
    private final String fumen;
    private final List<MinoOperationWithKey> sampleOperations;
    private final boolean deletedLine;
    private final HashSet<LongPieces> validPieces;

    public PathPair(Result result, HashSet<LongPieces> piecesSolution, HashSet<LongPieces> piecesPattern, String fumen, List<MinoOperationWithKey> sampleOperations, HashSet<LongPieces> validPieces) {
        this.result = result;
        this.piecesSolution = piecesSolution;
        this.piecesPattern = piecesPattern;
        this.fumen = fumen;
        this.sampleOperations = sampleOperations;
        this.deletedLine = result != null && containsDeletedLine();
        this.validPieces = validPieces;
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

    // すべての入力パターンの中で、その手順で対応できるツモ
    // パターンで6ミノを設定したとき、このツモは6ミノになる
    public HashSet<LongPieces> blocksHashSetForPattern() {
        return piecesPattern;
    }

    public Stream<LongPieces> blocksStreamForPattern() {
        return piecesPattern.stream();
    }

    // その地形を積み込むことができるツモ (入力パターンには依存しない)
    // パターンで6ミノを設定した場合でも、地形で5ミノしか使わないときは、このツモは5ミノになる
    public Stream<LongPieces> blocksStreamForSolution() {
        return piecesSolution.stream();
    }

    public HashSet<LongPieces> blocksHashSetForSolution() {
        return piecesSolution;
    }

    // その地形を積み込むことができるツモ (入力パターンには依存しない)のうち、パターンで対応できるツモ
    // パターンで6ミノを設定した場合でも、地形で5ミノしか使わないときは、このツモは5ミノになる
    public Stream<LongPieces> blocksStreamForValidSolution() {
        return piecesSolution.stream().filter(validPieces::contains);
    }

    public List<MinoOperationWithKey> getSampleOperations() {
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
