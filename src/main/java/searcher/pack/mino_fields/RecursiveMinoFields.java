package searcher.pack.mino_fields;

import searcher.pack.MinoField;
import searcher.pack.RecursiveMinoField;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecursiveMinoFields implements MinoFields {
    private final FutureTask<List<RecursiveMinoField>> task;

    public RecursiveMinoFields(Stream<RecursiveMinoField> minoFields) {
        this.task = new FutureTask<>(() -> minoFields.collect(Collectors.toList()));
    }

    @Override
    public Stream<? extends MinoField> stream() {
        return recursiveStream();
    }

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