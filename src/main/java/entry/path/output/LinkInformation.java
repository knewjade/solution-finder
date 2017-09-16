package entry.path.output;

import common.datastore.OperationWithKey;
import common.datastore.pieces.LongBlocks;
import entry.path.PathPair;

import java.util.HashSet;
import java.util.List;

class LinkInformation {
    private final PathPair pair;
    private final List<OperationWithKey> sample;

    LinkInformation(PathPair pair, List<OperationWithKey> sample) {
        this.pair = pair;
        this.sample = sample;
    }

    List<OperationWithKey> getSample() {
        return sample;
    }

    boolean containsDeletedLine() {
        return sample.stream().anyMatch(operationWithKey -> operationWithKey.getNeedDeletedKey() != 0L);
    }

    HashSet<LongBlocks> getPiecesSet() {
        return pair.getBuildBlocks();
    }
}
