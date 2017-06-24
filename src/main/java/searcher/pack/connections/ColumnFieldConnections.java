package searcher.pack.connections;

import java.util.stream.Stream;

public interface ColumnFieldConnections {
    ColumnFieldConnections FILLED = new ListColumnFieldConnections();

    Stream<ColumnFieldConnection> getConnectionStream();
}
