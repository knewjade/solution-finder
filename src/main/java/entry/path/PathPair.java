package entry.path;

import common.datastore.pieces.LongBlocks;
import searcher.pack.task.Result;

import java.util.HashSet;
import java.util.Set;

public class PathPair implements HaveSet<LongBlocks> {
    static final PathPair EMPTY_PAIR = new PathPair(null, new HashSet<>());

    private final Result result;
    private final HashSet<LongBlocks> pieces;

    PathPair(Result result, HashSet<LongBlocks> pieces) {
        this.result = result;
        this.pieces = pieces;
    }

    public HashSet<LongBlocks> getBuildBlocks() {
        return pieces;
    }

    @Override
    public Set<LongBlocks> getSet() {
        return getBuildBlocks();
    }

    public Result getResult() {
        return result;
    }
}
