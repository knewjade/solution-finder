package _experimental.allcomb.solutions.serialize;

import _experimental.allcomb.MinoField;
import core.column_field.ColumnField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SerializableBasicSolutions implements Serializable {
    private static final long serialVersionUID = 8274852713942394L;

    private final SerializableColumnField[] keys;
    private final SerializableMinoField[][] values;
    private final int height;

    public SerializableBasicSolutions(Map<ColumnField, Set<MinoField>> solutions, int height) {
        int size = solutions.size();
        this.keys = new SerializableColumnField[size];
        this.values = new SerializableMinoField[size][];

        int index = 0;
        for (Map.Entry<ColumnField, Set<MinoField>> entry : solutions.entrySet()) {
            this.keys[index] = new SerializableColumnField(entry.getKey());
            Set<MinoField> value = entry.getValue();
            this.values[index] = new SerializableMinoField[value.size()];
            int i = 0;
            for (MinoField minoField : value) {
                this.values[index][i] = new SerializableMinoField(minoField);
                i++;
            }
            index++;
        }
        assert index == size;
        this.height = height;
    }

    public Map<ColumnField, Set<MinoField>> toSolutions() {
        HashMap<ColumnField, Set<MinoField>> solutions = new HashMap<>();
        assert keys.length == values.length;
        for (int index = 0; index < keys.length; index++) {
            SerializableColumnField key = keys[index];
            SerializableMinoField[] value = values[index];
            HashSet<MinoField> minoFields = new HashSet<>();
            for (SerializableMinoField aValue : value)
                minoFields.add(aValue.toMinoField(height));
            solutions.put(key.toColumnField(), minoFields);
        }
        return solutions;
    }
}
