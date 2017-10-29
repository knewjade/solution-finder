package concurrent.checker.invoker;

import common.datastore.Pair;
import common.datastore.blocks.Blocks;
import core.field.Field;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ConcurrentCheckerInvoker {
    List<Pair<Blocks, Boolean>> search(Field field, List<Blocks> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException;
}
