package searcher.pack;

import java.util.Collections;
import java.util.List;

public class ColumnFieldConnections {
    public static final ColumnFieldConnections FILLED = new ColumnFieldConnections();

    private final List<ColumnFieldConnection> connections;

    private ColumnFieldConnections() {
        this(Collections.emptyList());
    }

    public ColumnFieldConnections(List<ColumnFieldConnection> connections) {
        this.connections = connections;
    }

    public List<ColumnFieldConnection> getConnections() {
        return connections;
    }

    public static boolean isFilled(ColumnFieldConnections connections) {
        return connections == FILLED;
    }
}
