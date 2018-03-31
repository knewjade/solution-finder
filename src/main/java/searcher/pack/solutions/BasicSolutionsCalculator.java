package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.*;
import searcher.pack.connections.ColumnFieldConnections;
import searcher.pack.mino_fields.MemorizedRecursiveMinoFields;
import searcher.pack.mino_fields.OnDemandRecursiveMinoFields;
import searcher.pack.mino_fields.RecursiveMinoFields;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * マルチスレッド非対応
 */
public class BasicSolutionsCalculator implements SolutionsCalculator {
    private static final int FIELD_WIDTH = 10;
    private static final int WIDTH_OVER_MINO = 3;

    private final SizedBit sizedBit;
    private final BasicReference reference;
    private final SeparableMinos separableMinos;
    private final ColumnSmallField limitOuterField;
    private final long needFillBoard;
    private final HashMap<ColumnField, RecursiveMinoFields> resultsMap = new HashMap<>();
    private final SmallField originWallField;
    private final Predicate<ColumnField> memorizedPredicate;

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit) {
        this(separableMinos, sizedBit, ColumnFieldFactory.createField(), columnField -> true);
    }

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit, long needFillBoard) {
        this(separableMinos, sizedBit, ColumnFieldFactory.createField(), columnField -> true, needFillBoard);
    }

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit, ColumnSmallField limitOuterField, Predicate<ColumnField> memorizedPredicate) {
        this(separableMinos, sizedBit, limitOuterField, memorizedPredicate, sizedBit.getFillBoard());
    }

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit, ColumnSmallField limitOuterField, Predicate<ColumnField> memorizedPredicate, long needFillBoard) {
        this.separableMinos = separableMinos;
        this.limitOuterField = limitOuterField;
        this.needFillBoard = needFillBoard;
        assert sizedBit.getHeight() <= 10;
        this.sizedBit = sizedBit;
        this.reference = createBasicReference(sizedBit, separableMinos);
        this.originWallField = createInitialWallField(sizedBit);
        this.memorizedPredicate = memorizedPredicate;
    }

    private BasicReference createBasicReference(SizedBit sizedBit, SeparableMinos separableMinos) {
        if (sizedBit.getHeight() <= 6)
            return new BasicReferenceHeight6(sizedBit, separableMinos);
        return new BasicReferenceHeight12(sizedBit, separableMinos);
    }

    private SmallField createInitialWallField(SizedBit sizedBit) {
        SmallField wallField = new SmallField();
        // 横向きIをおいたとき、3ブロック分あふれる
        for (int y = 0; y < sizedBit.getHeight(); y++)
            for (int x = sizedBit.getWidth() + WIDTH_OVER_MINO; x < FIELD_WIDTH; x++)
                wallField.setBlock(x, y);
        return wallField;
    }

    public Map<ColumnField, RecursiveMinoFields> calculate() {
        assert resultsMap.isEmpty();

        reference.getBasicFieldsSortedByBitCount().sequential().forEach(this::addColumnSmallField);

        return this.resultsMap;
    }

    private void addColumnSmallField(ColumnSmallField basicField) {
        Field wallField = createWallField(basicField);
        ColumnSmallField initOuterField = ColumnFieldFactory.createField();
        RecursiveMinoFields calculate = calculate(basicField, initOuterField, wallField);
        resultsMap.put(basicField, calculate);
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
    private RecursiveMinoFields calculate(ColumnSmallField columnField, ColumnField outerColumnField, Field wallField) {
        // まだ探索したことのないフィールドのとき
        // innerに対しておける可能性がある手順を取得
        // 計算をインスタンス化して遅延させる
        boolean isMemorized = memorizedPredicate.test(columnField);
        return createRecursiveMinoFields(columnField, outerColumnField, wallField, isMemorized);
    }

    private RecursiveMinoFields createRecursiveMinoFields(ColumnSmallField columnField, ColumnField outerColumnField, Field wallField, boolean isMemorized) {
        if (isMemorized) {
            ConnectionsToListCallable callable = new ConnectionsToListCallable(this, columnField, outerColumnField, wallField, limitOuterField);
            return new MemorizedRecursiveMinoFields(callable);
        } else {
            ConnectionsToStreamCallable callable = new ConnectionsToStreamCallable(this, columnField, outerColumnField, wallField, limitOuterField);
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
        return resultsMap.getOrDefault(columnField, null);
    }
}
