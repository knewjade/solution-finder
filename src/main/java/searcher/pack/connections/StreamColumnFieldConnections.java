package searcher.pack.connections;

import core.column_field.ColumnField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamColumnFieldConnections implements ColumnFieldConnections {
    private final SeparableMinos separableMinos;
    private final ColumnField columnField;
    private final SizedBit sizedBit;

    public StreamColumnFieldConnections(SeparableMinos separableMinos, ColumnField columnField, SizedBit sizedBit) {
        this.separableMinos = separableMinos;
        this.columnField = columnField;
        this.sizedBit = sizedBit;
    }

    @Override
    public List<ColumnFieldConnection> getConnections() {
        return getConnectionStream().collect(Collectors.toList());
    }

    @Override
    public Stream<ColumnFieldConnection> getConnectionStream() {
        Stream.Builder<ColumnFieldConnection> builder = Stream.builder();
        for (SeparableMino mino : separableMinos.getMinos()) {
            ColumnField minoField = mino.getField();
            if (columnField.canMerge(minoField)) {
                ColumnField freeze = columnField.freeze(sizedBit.getHeight());
                freeze.merge(minoField);

                ColumnFieldConnection connection = new ColumnFieldConnection(mino, freeze, sizedBit);
                builder.accept(connection);
            }
        }
        return builder.build();
    }
}
