package searcher.pack.solutions;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.ColumnFieldConnections;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.mino_fields.MemorizedRecursiveMinoFields;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.mino_fields.OnDemandRecursiveMinoFields;
import searcher.pack.mino_fields.RecursiveMinoFields;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class OnDemandBasicSolutions implements BasicSolutions, SolutionsCalculator {
    private static final int FIELD_WIDTH = 10;
    private static final int WIDTH_OVER_MINO = 3;

    private final SeparableMinos separableMinos;
    private final SizedBit sizedBit;
    private final BasicReference reference;
    private final SmallField originWallField;
    private final Predicate<ColumnField> memorizedPredicate;
    private final ConcurrentHashMap<ColumnField, RecursiveMinoFields> resultsMap;

    public OnDemandBasicSolutions(SeparableMinos separableMinos, SizedBit sizedBit, Predicate<ColumnField> memorizedPredicate) {
        this.separableMinos = separableMinos;
        assert sizedBit.getHeight() <= 10;
        this.sizedBit = sizedBit;
        this.reference = new BasicReference(sizedBit, separableMinos);
        this.originWallField = createWallField(sizedBit);
        this.memorizedPredicate = memorizedPredicate;
        this.resultsMap = new ConcurrentHashMap<>();
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
        return resultsMap.computeIfAbsent(columnField, this::addColumnSmallField);
    }

    private RecursiveMinoFields addColumnSmallField(ColumnField basicField) {
        Field wallField = createWallField(basicField);
        ColumnSmallField initOuterField = new ColumnSmallField();
        return calculate(basicField, initOuterField, wallField);
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
