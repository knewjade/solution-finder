package searcher.pack.task;

import java.util.stream.Stream;

public interface PackingTask {
    Stream<Result> compute();
}
