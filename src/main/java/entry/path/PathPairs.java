package entry.path;

import common.datastore.blocks.LongPieces;

import java.util.List;

public class PathPairs {
    private final List<PathPair> pathPairList;
    private final long numOfAllPatternSequences;

    PathPairs(List<PathPair> pathPairList, long numOfAllPatternSequences) {
        this.pathPairList = pathPairList;
        this.numOfAllPatternSequences = numOfAllPatternSequences;
    }

    public long getNumOfAllPatternSequences() {
        return numOfAllPatternSequences;
    }

    public List<PathPair> getUniquePathPairList() {
        return pathPairList;
    }

    public List<PathPair> getMinimalPathPairList() {
        Selector<PathPair, LongPieces> selector = new Selector<>(pathPairList);
        return selector.select();
    }
}
