package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.field.Field;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.*;
import searcher.pack.connections.ColumnFieldConnections;
import searcher.pack.mino_fields.MemorizedRecursiveMinoFields;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.mino_fields.OnDemandRecursiveMinoFields;
import searcher.pack.mino_fields.RecursiveMinoFields;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class OnDemandBasicSolutions implements BasicSolutions, SolutionsCalculator {
    private final SeparableMinos separableMinos;
    private final ColumnSmallField limitOuterField;
    private final long needFillBoard;
    private final SizedBit sizedBit;
    private final BasicReference reference;
    private final Predicate<ColumnField> memorizedPredicate;
    private final ConcurrentHashMap<ColumnField, RecursiveMinoFields> resultsMap;

    public OnDemandBasicSolutions(SeparableMinos separableMinos, SizedBit sizedBit, long needFillBoard) {
        this(separableMinos, sizedBit, ColumnFieldFactory.createField(), columnField -> true, needFillBoard);
    }

    public OnDemandBasicSolutions(SeparableMinos separableMinos, SizedBit sizedBit, Predicate<ColumnField> memorizedPredicate) {
        this(separableMinos, sizedBit, ColumnFieldFactory.createField(), memorizedPredicate, sizedBit.getFillBoard());
    }

    public OnDemandBasicSolutions(SeparableMinos separableMinos, SizedBit sizedBit, ColumnSmallField limitOuterField, Predicate<ColumnField> memorizedPredicate) {
        this(separableMinos, sizedBit, limitOuterField, memorizedPredicate, sizedBit.getFillBoard());
    }

    public OnDemandBasicSolutions(SeparableMinos separableMinos, SizedBit sizedBit, ColumnSmallField limitOuterField, Predicate<ColumnField> memorizedPredicate, long needFillBoard) {
        this.separableMinos = separableMinos;
        this.limitOuterField = limitOuterField;
        this.needFillBoard = needFillBoard;
        assert sizedBit.getHeight() <= 10;
        this.sizedBit = sizedBit;
        this.reference = createBasicReference(sizedBit, separableMinos);
        this.memorizedPredicate = memorizedPredicate;
        this.resultsMap = new ConcurrentHashMap<>();
    }

    private BasicReference createBasicReference(SizedBit sizedBit, SeparableMinos separableMinos) {
        if (sizedBit.getHeight() <= 6)
            return new BasicReferenceHeight6(sizedBit, separableMinos);
        return new BasicReferenceHeight12(sizedBit, separableMinos);
    }

    @Override
    public MinoFields parse(ColumnField columnField) {
        return getRecursiveMinoFields(columnField);
    }

    private RecursiveMinoFields addColumnSmallField(ColumnField basicField) {
        ColumnSmallField initOuterField = ColumnFieldFactory.createField();
        return calculate(basicField, initOuterField);
    }

    // columnField = inner
    // outerColumnField = outer only
    private RecursiveMinoFields calculate(ColumnField columnField, ColumnField outerColumnField) {
        // まだ探索したことのないフィールドのとき
        // innerに対しておける可能性がある手順を取得
        // 計算をインスタンス化して遅延させる
        boolean isMemorized = memorizedPredicate.test(columnField);
        return createRecursiveMinoFields(columnField, outerColumnField, isMemorized);
    }

    private RecursiveMinoFields createRecursiveMinoFields(ColumnField columnField, ColumnField outerColumnField, boolean isMemorized) {
        if (isMemorized) {
            ConnectionsToListCallable callable = new ConnectionsToListCallable(this, columnField, outerColumnField, limitOuterField);
            return new MemorizedRecursiveMinoFields(callable);
        } else {
            ConnectionsToStreamCallable callable = new ConnectionsToStreamCallable(this, columnField, outerColumnField, limitOuterField);
            return new OnDemandRecursiveMinoFields(callable);
        }
    }

    @Override
    public int getHeight() {
        return sizedBit.getHeight();
    }

    @Override
    public boolean isFilled(ColumnField columnField) {
        return (columnField.getBoard(0) & needFillBoard) == needFillBoard;
    }

    @Override
    public ColumnFieldConnections getConnections(ColumnField columnField) {
        return reference.getConnections(columnField);
    }

    @Override
    public Field parseInvertedOuterField(ColumnField outerColumnField) {
        return reference.parseInvertedOuterField(outerColumnField);
    }

    @Override
    public SeparableMinos getSeparableMinos() {
        return separableMinos;
    }

    @Override
    public RecursiveMinoFields getRecursiveMinoFields(ColumnField columnField) {
        return resultsMap.computeIfAbsent(columnField, this::addColumnSmallField);
    }

    public ConcurrentHashMap<ColumnField, RecursiveMinoFields> getSolutions() {
        return resultsMap;
    }
}
