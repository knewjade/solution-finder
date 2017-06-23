package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.ColumnFieldConnections;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.mino_fields.MemorizedRecursiveMinoFields;
import searcher.pack.mino_fields.OnDemandRecursiveMinoFields;
import searcher.pack.mino_fields.RecursiveMinoFields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * マルチスレッド非対応
 */
public class BasicSolutionsCalculator implements SolutionsCalculator {
    public static final int FIELD_WIDTH = 10;
    public static final int WIDTH_OVER_MINO = 3;

    private final SizedBit sizedBit;
    private final BasicReference reference;
    private final SeparableMinos separableMinos;
    private final HashMap<ColumnField, RecursiveMinoFields> resultsMap = new HashMap<>();
    private final SmallField originWallField;
    private final Predicate<ColumnField> memorizedPredicate;

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit) {
        this(separableMinos, sizedBit, columnField -> true);
    }

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit, Predicate<ColumnField> memorizedPredicate) {
        this.separableMinos = separableMinos;
        assert sizedBit.getHeight() <= 10;
        this.sizedBit = sizedBit;
        this.reference = new BasicReference(sizedBit, separableMinos);
        this.originWallField = createWallField(sizedBit);
        this.memorizedPredicate = memorizedPredicate;
    }

    private SmallField createWallField(SizedBit sizedBit) {
        SmallField wallField = new SmallField();
        // 横向きIをおいたとき、3ブロック分あふれる
        for (int y = 0; y < sizedBit.getHeight(); y++)
            for (int x = sizedBit.getWidth() + WIDTH_OVER_MINO; x < FIELD_WIDTH; x++)
                wallField.setBlock(x, y);
        return wallField;
    }

    public Map<ColumnField, RecursiveMinoFields> calculate() {
        assert resultsMap.isEmpty();

        List<ColumnSmallField> sortedBasicFields = reference.getSortedBasicFields();
        for (ColumnSmallField basicField : sortedBasicFields)
            addColumnSmallField(basicField);

        return this.resultsMap;
    }

    private void addColumnSmallField(ColumnSmallField basicField) {
        Field wallField = createWallField(basicField);
        ColumnSmallField initOuterField = new ColumnSmallField();
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
            ConnectionsToListCallable callable = new ConnectionsToListCallable(this, columnField, outerColumnField, wallField);
            return new MemorizedRecursiveMinoFields(callable);
        } else {
            ConnectionsToStreamCallable callable = new ConnectionsToStreamCallable(this, columnField, outerColumnField, wallField);
            return new OnDemandRecursiveMinoFields(callable);
        }
    }

    @Override
    public int getHeight() {
        return sizedBit.getHeight();
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
