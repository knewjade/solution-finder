package searcher.pack.connections;

import java.util.List;
import java.util.stream.Stream;

public interface ColumnFieldConnections {
    ColumnFieldConnections FILLED = new ListColumnFieldConnections();

    static boolean isFilled(ColumnFieldConnections connections) {
        return connections == FILLED;
    }

    List<ColumnFieldConnection> getConnections();

    Stream<ColumnFieldConnection> getConnectionStream();
}
