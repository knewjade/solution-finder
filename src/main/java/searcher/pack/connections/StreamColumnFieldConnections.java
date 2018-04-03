package searcher.pack.connections;

import core.column_field.ColumnField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;
import java.util.stream.Stream;

public class StreamColumnFieldConnections implements ColumnFieldConnections {
    private final List<SeparableMino> separableMinoList;
    private final ColumnField columnField;
    private final SizedBit sizedBit;

    public StreamColumnFieldConnections(SeparableMinos separableMinoList, ColumnField columnField, SizedBit sizedBit) {
        this(separableMinoList.getMinos(), columnField, sizedBit);
    }

    public StreamColumnFieldConnections(List<SeparableMino> separableMinoList, ColumnField columnField, SizedBit sizedBit) {
        this.separableMinoList = separableMinoList;
        this.columnField = columnField;
        this.sizedBit = sizedBit;
    }

    @Override
    public Stream<ColumnFieldConnection> getConnectionStream() {
        Stream.Builder<ColumnFieldConnection> builder = Stream.builder();
        for (SeparableMino mino : separableMinoList) {
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
