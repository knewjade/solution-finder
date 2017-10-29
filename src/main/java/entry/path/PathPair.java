package entry.path;

import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.datastore.blocks.LongBlocks;
import core.mino.Block;
import searcher.pack.task.Result;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathPair implements HaveSet<LongBlocks> {
    static final PathPair EMPTY_PAIR = new PathPair(null, new HashSet<>(), null, "", Collections.emptyList());

    private final Result result;
    private final HashSet<LongBlocks> piecesSolution;
    private final HashSet<LongBlocks> piecesPattern;
    private final String fumen;
    private final List<OperationWithKey> sampleOperations;
    private final boolean deletedLine;

    public PathPair(Result result, HashSet<LongBlocks> piecesSolution, HashSet<LongBlocks> piecesPattern, String fumen, List<OperationWithKey> sampleOperations) {
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
    public Set<LongBlocks> getSet() {
        return blocksHashSetForPattern();
    }

    public Result getResult() {
        return result;
    }

    public String getFumen() {
        return fumen;
    }

    public Stream<LongBlocks> blocksStreamForPattern() {
        return piecesPattern.stream();
    }

    public Stream<LongBlocks> blocksStreamForSolution() {
        return piecesSolution.stream();
    }

    public HashSet<LongBlocks> blocksHashSetForPattern() {
        return piecesPattern;
    }

    public HashSet<LongBlocks> blocksHashSetForSolution() {
        return piecesSolution;
    }

    public List<OperationWithKey> getSampleOperations() {
        return sampleOperations;
    }

    public String getUsingBlockName() {
        return sampleOperations.stream()
                .map(OperationWithKey::getBlock)
                .sorted()
                .map(Block::getName)
                .collect(Collectors.joining());
    }

    public int getPatternSize() {
        return blocksHashSetForPattern().size();
    }

    public BlockCounter getBlockCounter() {
        return new BlockCounter(sampleOperations.stream().map(OperationWithKey::getBlock));
    }

    public boolean isDeletedLine() {
        return deletedLine;
    }
}
