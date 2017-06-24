package searcher.pack.calculator;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.ColumnOperators;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.connections.ColumnFieldConnections;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.connections.StreamColumnFieldConnections;

import java.util.Comparator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class BasicReferenceHeight6 implements BasicReference {
    private static final int WIDTH_OVER_MINO = 3;

    private final SizedBit sizedBit;
    private final SeparableMinos separableMinos;

    public BasicReferenceHeight6(SizedBit sizedBit, SeparableMinos separableMinos) {
        this.sizedBit = sizedBit;
        this.separableMinos = separableMinos;
    }

    // 存在する基本フィールドをすべて列挙
    @Override
    public Stream<ColumnSmallField> getBasicFieldsSortedByBitCount() {
        return LongStream.range(0, sizedBit.getFillBoard())
                .boxed()
                .sorted(Comparator.comparingLong(Long::bitCount).reversed())
                .map(ColumnSmallField::new);
    }

    // ColumnFieldの一部からFieldに変換する
    private SmallField createInner(ColumnField columnField) {
        long board = columnField.getBoard(0);
        return createInner(board, sizedBit.getWidth());
    }

    private SmallField createInner(long board, int width) {
        int height = sizedBit.getHeight();
        int columnFilled = (1 << height) - 1;

        long low = 0L;
        for (int x = 0; x < width; x++) {
            int slide = height * x;
            int column = (int) ((board >> slide) & columnFilled);
            low |= ColumnOperators.parseToBoardWidth6(column) << x;
        }

        return new SmallField(low);
    }

    private SmallField createOuter(ColumnField slidedColumnField) {
        long board = slidedColumnField.getBoard(0) >> sizedBit.getMaxBitDigit();
        return createOuter(board, WIDTH_OVER_MINO);
    }

    private SmallField createOuter(long board, int width) {
        int height = sizedBit.getHeight();
        int columnFilled = (1 << height) - 1;

        long low = 0L;
        for (int x = 0; x < width; x++) {
            int slide = height * x;
            int column = (int) ((board >> slide) & columnFilled);
            low |= ColumnOperators.parseToInvertedBoardWidth6(column) << x;
        }

        return new SmallField(low);
    }

    @Override
    public Field parseInnerField(ColumnField field) {
        assert field.getBoardCount() == 1;
        return createInner(field);
    }

    @Override
    public Field parseInvertedOuterField(ColumnField field) {
        assert field.getBoardCount() == 1;
        return createOuter(field);
    }

    // ある地形から1ミノだけ置いてできる地形
    @Override
    public ColumnFieldConnections getConnections(ColumnField columnField) {
        if (columnField.getBoard(0) == this.sizedBit.getFillBoard())
            return ColumnFieldConnections.FILLED;
        return new StreamColumnFieldConnections(separableMinos, columnField, sizedBit);
    }
}
