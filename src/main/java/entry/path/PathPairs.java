package entry.path;

import common.datastore.blocks.LongPieces;

import java.util.List;

public class PathPairs {
    private final List<PathPair> pathPairList;
    private final int numOfAllPatternSequences;

    PathPairs(List<PathPair> pathPairList, int numOfAllPatternSequences) {
        this.pathPairList = pathPairList;
        this.numOfAllPatternSequences = numOfAllPatternSequences;
    }

    public int getNumOfAllPatternSequences() {
        return numOfAllPatternSequences;
    }

    public List<PathPair> getUniquePathPairList() {
        return pathPairList;
    }

    public List<PathPair> getMinimalPathPairList(boolean specified_only) {
        Selector<PathPair, LongPieces> selector = new Selector<>(pathPairList);
        return selector.select(specified_only);
    }
}
