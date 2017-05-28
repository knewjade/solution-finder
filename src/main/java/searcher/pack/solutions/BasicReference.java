package searcher.pack.solutions;

import common.datastore.Pair;
import searcher.pack.ColumnFieldConnection;
import searcher.pack.ColumnFieldConnections;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import pack.separable_mino.SeparableMino;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

class BasicReference {
    private final SizedBit sizedBit;
    private final SeparableMinos separableMinos;
    private final List<ColumnSmallField> basicFields;

    private final HashMap<ColumnField, ColumnFieldConnections> fieldToConnections;
    private final HashMap<Long, Field> normalToField;
    private final HashMap<Long, Field> invertedToField;

    BasicReference(SizedBit sizedBit, SeparableMinos separableMinos) {
        this.sizedBit = sizedBit;
        this.separableMinos = separableMinos;
        this.basicFields = createBasicFields();
        this.fieldToConnections = new HashMap<>();
        this.normalToField = new HashMap<>();
        this.invertedToField = new HashMap<>();
        init();
    }

    // 存在する基本フィールドをすべて列挙
    private List<ColumnSmallField> createBasicFields() {
        return LongStream.range(0, sizedBit.getFillBoard())
                .boxed()
                .sorted(Comparator.comparingLong(Long::bitCount).reversed())
                .map(ColumnSmallField::new)
                .collect(Collectors.toList());
    }

    private void init() {
        // すべてのブロックが埋まった状態を保存
        addInnerAndOuter(new ColumnSmallField(sizedBit.getFillBoard()));

        for (ColumnSmallField columnField : basicFields) {
            addInnerAndOuter(columnField);
        }

        Map<ColumnField, ColumnFieldConnections> collect = basicFields.parallelStream()
                .map(columnSmallField -> {
                    ColumnFieldConnections connections = createConnections(columnSmallField);
                    return new Pair<>(columnSmallField, connections);
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (u, u2) -> {
                    throw new IllegalStateException();
                }));
        this.fieldToConnections.putAll(collect);
        this.fieldToConnections.put(new ColumnSmallField(sizedBit.getFillBoard()), ColumnFieldConnections.FILLED);
    }

    // ColumnFieldの一部からFieldに変換するマップを登録
    private void addInnerAndOuter(ColumnSmallField columnField) {
        long board = columnField.getBoard(0);

        SmallField normalField = new SmallField();
        SmallField invertedField = new SmallField();
        for (int y = 0; y < sizedBit.getHeight(); y++) {
            for (int x = 0; x < sizedBit.getWidth(); x++) {
                if (columnField.isEmpty(x, y, sizedBit.getHeight())) {
                    invertedField.setBlock(x + sizedBit.getWidth(), y);
                } else {
                    normalField.setBlock(x, y);
                }
            }
        }

        normalToField.put(board, normalField);
        invertedToField.put(board << sizedBit.getMaxBitDigit(), invertedField);
    }

    // ある地形から1ミノだけ置いてできる地形を登録する
    private ColumnFieldConnections createConnections(ColumnSmallField columnField) {
        ArrayList<ColumnFieldConnection> connectionList = new ArrayList<>();
        for (SeparableMino mino : separableMinos.getMinos()) {
            ColumnField minoField = mino.getField();
            if (columnField.canMerge(minoField)) {
                ColumnField freeze = columnField.freeze(sizedBit.getHeight());
                freeze.merge(minoField);

                ColumnFieldConnection connection = new ColumnFieldConnection(mino, freeze, sizedBit.getHeight());
                connectionList.add(connection);
            }
        }
//        System.out.println(connectionList.size());
        return new ColumnFieldConnections(connectionList);
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
