package entry.path;

import common.datastore.OperationWithKey;
import common.datastore.pieces.LongBlocks;
import core.field.Field;
import entry.path.output.FumenParser;
import searcher.pack.SizedBit;
import searcher.pack.task.Result;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathPair implements HaveSet<LongBlocks> {
    static final PathPair EMPTY_PAIR = new PathPair(null, new HashSet<>(), "");

    public static PathPair createPathPair(Result result, HashSet<LongBlocks> pieces, FumenParser parser, Field field, SizedBit sizedBit) {
        int width = sizedBit.getWidth();
        List<OperationWithKey> operations = result.getMemento().getOperationsStream(width).collect(Collectors.toList());

        int height = sizedBit.getHeight();
        String fumen = parser.parse(operations, field, height);

        return new PathPair(result, pieces, fumen);
    }

    private final Result result;
    private final HashSet<LongBlocks> pieces;
    private final String fumen;

    private PathPair(Result result, HashSet<LongBlocks> pieces, String fumen) {
        this.result = result;
        this.pieces = pieces;
        this.fumen = fumen;
    }

    public HashSet<LongBlocks> getBuildBlocks() {
        return pieces;
    }

    public Result getResult() {
        return result;
    }

    @Override
    public Set<LongBlocks> getSet() {
        return getBuildBlocks();
    }

    public String getFumen() {
        return fumen;
    }
}
