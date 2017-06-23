package searcher.pack.mino_fields;

import searcher.pack.MinoField;
import searcher.pack.RecursiveMinoField;
import searcher.pack.solutions.ConnectionsToListCallable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Stream;

public class MemorizedRecursiveMinoFields implements RecursiveMinoFields {
    private final FutureTask<List<RecursiveMinoField>> task;

    public MemorizedRecursiveMinoFields(ConnectionsToListCallable callable) {
        this.task = new FutureTask<>(callable);
    }

    @Override
    public Stream<? extends MinoField> stream() {
        return recursiveStream();
    }

    @Override
    public Stream<RecursiveMinoField> recursiveStream() {
        task.run();
        try {
            return task.get().stream();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new IllegalStateException("internal error: task cannot execute");
        }
    }
}