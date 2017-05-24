package _experimental.allcomb.solutions.serialize;

import _experimental.allcomb.MinoField;
import common.datastore.OperationWithKey;
import core.column_field.ColumnField;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SerializableMinoField implements Serializable {
    private static final long serialVersionUID = 21937473153215434L;

    private final SerializableOperationWithKey[] operations;
    private final SerializableColumnField outerField;

    SerializableMinoField(MinoField minoField) {
        List<OperationWithKey> operations = minoField.getOperations();
        this.operations = new SerializableOperationWithKey[operations.size()];
        operations.stream()
                .map(SerializableOperationWithKey::new)
                .collect(Collectors.toList())
                .toArray(this.operations);

        this.outerField = new SerializableColumnField(minoField.getOuterField());
    }

    MinoField toMinoField(int height) {
        List<OperationWithKey> operations = Stream.of(this.operations)
                .map(SerializableOperationWithKey::toOperationWithKey)
                .collect(Collectors.toList());

        ColumnField field = outerField.toColumnField();

        return new MinoField(operations, field, height);
    }
}
