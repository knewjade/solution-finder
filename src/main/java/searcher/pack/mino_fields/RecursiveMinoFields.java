package searcher.pack.mino_fields;

import searcher.pack.MinoField;
import searcher.pack.RecursiveMinoField;
import searcher.pack.solutions.ConnectionsToListCallable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Stream;

public class RecursiveMinoFields implements MinoFields {
    private final FutureTask<List<RecursiveMinoField>> task;

    public RecursiveMinoFields(ConnectionsToListCallable callable) {
        this.task = new FutureTask<>(callable);
    }

    @Override
    public Stream<? extends MinoField> stream() {
        return recursiveStream();
    }

    public Stream<RecursiveMinoField> recursiveStream() {
//        System.out.println("run");
        task.run();
        try {
            return task.get().stream();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new IllegalStateException("internal error: task cannot execute");
        }
    }
}