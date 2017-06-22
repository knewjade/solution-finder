package searcher.pack.solutions;

import searcher.pack.ColumnFieldConnection;
import searcher.pack.ColumnFieldConnections;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.separable_mino.SeparableMino;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

class BasicReference {
    public static final BinaryOperator<ColumnFieldConnections> EXCEPTION_IN_MERGE_FUNCTION = (columnFieldConnections, columnFieldConnections2) -> {
        throw new IllegalStateException("Duplicate key");
    };

    public static final int WIDTH_OVER_MINO = 3;
    private final SizedBit sizedBit;
    private final SeparableMinos separableMinos;
    private final List<ColumnSmallField> sortedBasicFields;

    private final HashMap<ColumnField, ColumnFieldConnections> fieldToConnections;
    private final HashMap<Long, Field> normalToField;
    private final HashMap<Long, Field> invertedToField;

    BasicReference(SizedBit sizedBit, SeparableMinos separableMinos) {
        this.sizedBit = sizedBit;
        this.separableMinos = separableMinos;
        this.sortedBasicFields = createBasicFields(sizedBit);
        this.fieldToConnections = new HashMap<>();
        this.normalToField = new HashMap<>();
        this.invertedToField = new HashMap<>();
        init();
    }

    // 存在する基本フィールドをすべて列挙
    private List<ColumnSmallField> createBasicFields(SizedBit sizedBit) {
        return LongStream.range(0, sizedBit.getFillBoard())
                .boxed()
                .sorted(Comparator.comparingLong(Long::bitCount).reversed())
                .map(ColumnSmallField::new)
                .collect(Collectors.toList());
    }

    private void init() {
        // InnerFieldのマップをつくる
        // すべてのブロックが埋まった状態を保存
        addInner(new ColumnSmallField(sizedBit.getFillBoard()));
        for (ColumnSmallField columnField : sortedBasicFields) {
            addInner(columnField);
        }

        // OuterFieldのマップをつくる
        // すべてのブロックが埋まった状態を保存
        int height = sizedBit.getHeight();
        SizedBit outerSizeBit = new SizedBit(WIDTH_OVER_MINO, height);
        addOuter(new ColumnSmallField(outerSizeBit.getFillBoard()), outerSizeBit);
        for (ColumnSmallField columnField : createBasicFields(outerSizeBit)) {
            addOuter(columnField, outerSizeBit);
        }

        Map<ColumnField, ColumnFieldConnections> collect = sortedBasicFields.parallelStream()
                .collect(Collectors.toMap(Function.identity(), this::createConnections, EXCEPTION_IN_MERGE_FUNCTION, HashMap::new));

        this.fieldToConnections.putAll(collect);
        this.fieldToConnections.put(new ColumnSmallField(this.sizedBit.getFillBoard()), ColumnFieldConnections.FILLED);
    }

    // ColumnFieldの一部からFieldに変換するマップを登録
    private void addInner(ColumnSmallField columnField) {
        long board = columnField.getBoard(0);

        SmallField normalField = new SmallField();
        for (int y = 0; y < sizedBit.getHeight(); y++) {
            for (int x = 0; x < sizedBit.getWidth(); x++) {
                if (!columnField.isEmpty(x, y, sizedBit.getHeight()))
                    normalField.setBlock(x, y);
            }
        }

        normalToField.put(board, normalField);
    }

    private void addOuter(ColumnSmallField columnField, SizedBit outerSizeBit) {
        long board = columnField.getBoard(0);

        SmallField invertedField = new SmallField();
        for (int y = 0; y < outerSizeBit.getHeight(); y++) {
            for (int x = 0; x < outerSizeBit.getWidth(); x++) {
                if (columnField.isEmpty(x, y, outerSizeBit.getHeight()))
                    invertedField.setBlock(x + sizedBit.getWidth(), y);
            }
        }

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

                ColumnFieldConnection connection = new ColumnFieldConnection(mino, freeze, sizedBit);
                connectionList.add(connection);
            }
        }

        return new ColumnFieldConnections(connectionList);
    }

    List<ColumnSmallField> getSortedBasicFields() {
        return sortedBasicFields;
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
