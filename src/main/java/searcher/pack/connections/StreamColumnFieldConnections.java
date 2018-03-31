package searcher.pack.connections;

import core.column_field.ColumnField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;
import java.util.stream.Stream;

public class StreamColumnFieldConnections implements ColumnFieldConnections {
    private final List<SeparableMino> separableMinos;
    private final ColumnField columnField;
    private final SizedBit sizedBit;

    public StreamColumnFieldConnections(SeparableMinos separableMinos, ColumnField columnField, SizedBit sizedBit) {
        this(separableMinos.getMinos(), columnField, sizedBit);
    }

    public StreamColumnFieldConnections(List<SeparableMino> separableMinos, ColumnField columnField, SizedBit sizedBit) {
        this.separableMinos = separableMinos;
        this.columnField = columnField;
        this.sizedBit = sizedBit;
    }

    @Override
    public Stream<ColumnFieldConnection> getConnectionStream() {
        Stream.Builder<ColumnFieldConnection> builder = Stream.builder();
        for (SeparableMino mino : separableMinos) {
            ColumnField minoField = mino.getColumnField();
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
