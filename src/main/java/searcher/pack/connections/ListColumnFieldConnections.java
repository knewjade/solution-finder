package searcher.pack.connections;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ListColumnFieldConnections implements ColumnFieldConnections {
    private final List<ColumnFieldConnection> connections;

    ListColumnFieldConnections() {
        this(Collections.emptyList());
    }

    public ListColumnFieldConnections(List<ColumnFieldConnection> connections) {
        this.connections = connections;
    }

    @Override
    public List<ColumnFieldConnection> getConnections() {
        return connections;
    }

    @Override
    public Stream<ColumnFieldConnection> getConnectionStream() {
        return connections.stream();
    }
}
