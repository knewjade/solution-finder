package concurrent.checker.invoker;

import common.datastore.Pair;
import common.datastore.blocks.Pieces;
import core.field.Field;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ConcurrentCheckerInvoker {
    List<Pair<Pieces, Boolean>> search(Field field, List<Pieces> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException;
}
