package searcher.pack.solutions.serialize;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class SerializableColumnField implements Serializable {
    private static final long serialVersionUID = 3019284750275389502L;

    private final List<Long> boards;

    SerializableColumnField(ColumnField field) {
        int boardCount = field.getBoardCount();
        List<Long> boards = new ArrayList<>();
        for (int index = 0; index < boardCount; index++)
            boards.add(field.getBoard(index));
        this.boards = boards;
    }

    ColumnField toColumnField() {
        return ColumnFieldFactory.createField(this.boards);
    }
}
