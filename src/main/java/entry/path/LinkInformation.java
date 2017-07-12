package entry.path;

import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.datastore.pieces.LongPieces;
import searcher.pack.task.Result;

import java.util.HashSet;
import java.util.List;

class LinkInformation {
    private final Pair<Result, HashSet<LongPieces>> pair;
    private final List<OperationWithKey> sample;

    LinkInformation(Pair<Result, HashSet<LongPieces>> pair, List<OperationWithKey> sample) {
        this.pair = pair;
        this.sample = sample;
    }

    List<OperationWithKey> getSample() {
        return sample;
    }

    boolean containsDeletedLine() {
        return sample.stream().anyMatch(operationWithKey -> operationWithKey.getNeedDeletedKey() != 0L);
    }

     HashSet<LongPieces> getPiecesSet() {
        return pair.getValue();
    }
}
