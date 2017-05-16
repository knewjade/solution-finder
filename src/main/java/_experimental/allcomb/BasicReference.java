package _experimental.allcomb;

import core.field.Field;
import core.field.SmallField;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class BasicReference {
    private final Bit bit;
    private final List<SeparableMino> candidateMinos;
    private final List<ColumnSmallField> basicFields;
    private final HashMap<ColumnField, ColumnFieldConnections> fieldToConnections;
    private final HashMap<Long, Field> normalToField;
    private final HashMap<Long, Field> invertedToField;

    BasicReference(Bit bit, List<SeparableMino> candidateMinos) {
        this.bit = bit;
        this.candidateMinos = candidateMinos;
        this.basicFields = createBasicFields();
        this.fieldToConnections = new HashMap<>();
        this.normalToField = new HashMap<>();
        this.invertedToField = new HashMap<>();
        init();
    }

    private void init() {
        // すべてのブロックが埋まった状態を保存
        fieldToConnections.put(new ColumnSmallField(bit.fillBoard), ColumnFieldConnections.FILLED);
        addInnerAndOuter(new ColumnSmallField(bit.fillBoard));

        for (ColumnSmallField columnField : basicFields) {
            addInnerAndOuter(columnField);
            addConnections(columnField);
        }
    }

    // ColumnFieldの一部からFieldに変換するマップを登録
    private void addInnerAndOuter(ColumnSmallField columnField) {
        long board = columnField.getBoard(0);

        SmallField normalField = new SmallField();
        SmallField invertedField = new SmallField();
        for (int y = 0; y < bit.height; y++) {
            for (int x = 0; x < bit.width; x++) {
                if (columnField.isEmpty(x, y, bit.height)) {
                    invertedField.setBlock(x + bit.width, y);
                } else {
                    normalField.setBlock(x, y);
                }
            }
        }

        normalToField.put(board, normalField);
        invertedToField.put(board << bit.maxBit, invertedField);
    }

    // ある地形から1ミノだけ置いてできる地形を登録する
    private void addConnections(ColumnSmallField columnField) {
        ArrayList<ColumnFieldConnection> connectionList = new ArrayList<>();
        for (SeparableMino mino : candidateMinos) {
            ColumnField minoField = mino.getField();
            if (columnField.canMerge(minoField)) {
                ColumnField freeze = columnField.freeze(bit.height);
                freeze.merge(minoField);

                ColumnFieldConnection connection = new ColumnFieldConnection(mino, freeze, bit.height);
                connectionList.add(connection);
            }
        }
        fieldToConnections.put(columnField, new ColumnFieldConnections(connectionList));
    }

    private List<ColumnSmallField> createBasicFields() {
        return LongStream.range(0, bit.fillBoard)
                .boxed()
                .sorted(Comparator.comparingLong(Long::bitCount).reversed())
                .map(ColumnSmallField::new)
                .collect(Collectors.toList());
    }

    List<ColumnSmallField> getBasicFields() {
        return basicFields;
    }

    Field parseInnerField(ColumnField field) {
        assert field.getBoardCount() == 1;
        long board = field.getBoard(0);
        return normalToField.get(board);
    }

    Field parseInvertedOuterField(ColumnField field) {
        assert field.getBoardCount() == 1;
        long board = field.getBoard(0);
        return invertedToField.get(board);
    }

    ColumnFieldConnections getConnections(ColumnField columnField) {
        return fieldToConnections.get(columnField);
    }
}
