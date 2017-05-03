package concurrent.checker.invoker;

import core.field.Field;
import core.mino.Block;
import common.datastore.Pair;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ConcurrentCheckerInvoker {
    List<Pair<List<Block>, Boolean>> search(Field field, List<List<Block>> searchingPieces, int maxClearLine, int maxDepth) throws ExecutionException, InterruptedException;
}
