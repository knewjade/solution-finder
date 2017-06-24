package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.*;
import searcher.pack.connections.ColumnFieldConnections;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class FilterOnDemandBasicSolutions implements BasicSolutions, SolutionsCalculator {
    private static final int FIELD_WIDTH = 10;
    private static final int WIDTH_OVER_MINO = 3;

    private final SeparableMinos separableMinos;
    private final ColumnSmallField initOuterField;
    private final SolutionFilter solutionFilter;
    private final SizedBit sizedBit;
    private final BasicReference reference;
    private final SmallField originWallField;
    private final Predicate<ColumnField> memorizedPredicate;
    private final ConcurrentHashMap<ColumnField, RecursiveMinoFields> resultsMap;

    public FilterOnDemandBasicSolutions(SeparableMinos separableMinos, SizedBit sizedBit, Predicate<ColumnField> memorizedPredicate, SolutionFilter solutionFilter) {
        this(separableMinos, sizedBit, new ColumnSmallField(), memorizedPredicate, solutionFilter);
    }

    public FilterOnDemandBasicSolutions(SeparableMinos separableMinos, SizedBit sizedBit, ColumnSmallField initOuterField, Predicate<ColumnField> memorizedPredicate, SolutionFilter solutionFilter) {
        this.separableMinos = separableMinos;
        this.initOuterField = initOuterField;
        this.solutionFilter = solutionFilter;
        assert sizedBit.getHeight() <= 10;
        this.sizedBit = sizedBit;
        this.reference = createBasicReference(sizedBit, separableMinos);
        this.originWallField = createWallField(sizedBit);
        this.memorizedPredicate = memorizedPredicate;
        this.resultsMap = new ConcurrentHashMap<>();
    }

    private BasicReference createBasicReference(SizedBit sizedBit, SeparableMinos separableMinos) {
        if (sizedBit.getHeight() <= 6)
            return new BasicReferenceHeight6(sizedBit, separableMinos);
        return new BasicReferenceHeight12(sizedBit, separableMinos);
    }

    private SmallField createWallField(SizedBit sizedBit) {
        SmallField wallField = new SmallField();
        // 横向きIをおいたとき、3ブロック分あふれる
        for (int y = 0; y < sizedBit.getHeight(); y++)
            for (int x = sizedBit.getWidth() + WIDTH_OVER_MINO; x < FIELD_WIDTH; x++)
                wallField.setBlock(x, y);
        return wallField;
    }

    @Override
    public MinoFields parse(ColumnField columnField) {
        return getRecursiveMinoFields(columnField);
    }

    private RecursiveMinoFields addColumnSmallField(ColumnField basicField) {
        Field wallField = createWallField(basicField);
        ColumnSmallField initOuterField = new ColumnSmallField();
        RecursiveMinoFields recursiveMinoFields = calculate(basicField, initOuterField, wallField);
        return new FilteredRecursiveMinoFields(recursiveMinoFields, solutionFilter);
    }

    // innerと探索に関係ないブロックが埋まっているフィールド
    private Field createWallField(ColumnField columnField) {
        Field freeze = originWallField.freeze(sizedBit.getHeight());
        Field innerField = reference.parseInnerField(columnField);
        freeze.merge(innerField);
        return freeze;
    }

    // columnField = inner + outer
    // outerColumnField = outer only
    private RecursiveMinoFields calculate(ColumnField columnField, ColumnField outerColumnField, Field wallField) {
        // まだ探索したことのないフィールドのとき
        // innerに対しておける可能性がある手順を取得
        // 計算をインスタンス化して遅延させる
        boolean isMemorized = memorizedPredicate.test(columnField);
        return createRecursiveMinoFields(columnField, outerColumnField, wallField, isMemorized);
    }

    private RecursiveMinoFields createRecursiveMinoFields(ColumnField columnField, ColumnField outerColumnField, Field wallField, boolean isMemorized) {
        if (isMemorized) {
            ConnectionsToListCallable callable = new ConnectionsToListCallable(this, columnField, outerColumnField, wallField, initOuterField);
            return new MemorizedRecursiveMinoFields(callable);
        } else {
            ConnectionsToStreamCallable callable = new ConnectionsToStreamCallable(this, columnField, outerColumnField, wallField, initOuterField);
            return new OnDemandRecursiveMinoFields(callable);
        }
    }

    @Override
    public int getHeight() {
        return sizedBit.getHeight();
    }

    @Override
    public boolean isFilled(ColumnField columnField) {
        return columnField.getBoard(0) == sizedBit.getFillBoard();
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

