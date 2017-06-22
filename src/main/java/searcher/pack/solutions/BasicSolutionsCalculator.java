package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.ColumnFieldConnections;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.mino_fields.RecursiveMinoFields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit) {
        this.separableMinos = separableMinos;
        assert sizedBit.getHeight() <= 10;
        this.sizedBit = sizedBit;
        this.reference = new BasicReference(sizedBit, separableMinos);
        this.originWallField = createWallField(sizedBit);
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
        for (ColumnSmallField basicField : sortedBasicFields) {
            Field wallField = createWallField(basicField);
            ColumnSmallField initOuterField = new ColumnSmallField();
            RecursiveMinoFields calculate = calculate(basicField, initOuterField, wallField);
            resultsMap.put(basicField, calculate);
        }

        return this.resultsMap;
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
        ConnectionsToListCallable callable = new ConnectionsToListCallable(this, columnField, outerColumnField, wallField);
        return new RecursiveMinoFields(callable);
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
