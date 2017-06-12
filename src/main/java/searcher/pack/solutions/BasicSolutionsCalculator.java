package searcher.pack.solutions;

import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.ColumnFieldConnections;
import searcher.pack.RecursiveMinoField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * マルチスレッド非対応
 */
public class BasicSolutionsCalculator {
    public static final int FIELD_WIDTH = 10;
    public static final int WIDTH_OVER_MINO = 3;

    private final SizedBit sizedBit;
    private final BasicReference reference;
    private final SmallField wallField;

    private HashMap<ColumnField, List<RecursiveMinoField>> resultsMap = new HashMap<>();
    private final CalculatorCoreThreadLocal coreThreadLocal;

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit) {
        assert sizedBit.getHeight() <= 10;
        this.sizedBit = sizedBit;
        this.reference = new BasicReference(sizedBit, separableMinos);
        this.coreThreadLocal = new CalculatorCoreThreadLocal(sizedBit, reference, separableMinos, resultsMap);
        this.wallField = createWallField(sizedBit);
    }

    private SmallField createWallField(SizedBit sizedBit) {
        SmallField wallField = new SmallField();
        // 横向きIをおいたとき、3ブロック分あふれる
        for (int y = 0; y < sizedBit.getHeight(); y++)
            for (int x = sizedBit.getWidth() + WIDTH_OVER_MINO; x < FIELD_WIDTH; x++)
                wallField.setBlock(x, y);
        return wallField;
    }

    public Map<ColumnField, List<RecursiveMinoField>> calculate() {
        assert resultsMap.isEmpty();

        List<ColumnSmallField> sortedBasicFields = reference.getSortedBasicFields();
        for (ColumnSmallField basicField : sortedBasicFields) {
            Field wallField = createWallField(basicField);
            ColumnSmallField initOuterField = new ColumnSmallField();
            List<RecursiveMinoField> calculate = calculate(basicField, initOuterField, wallField);
            resultsMap.put(basicField, calculate);
        }

        return this.resultsMap;
    }

    // innerと探索に関係ないブロックが埋まっているフィールド
    private Field createWallField(ColumnField columnField) {
        Field freeze = wallField.freeze(sizedBit.getHeight());
        Field innerField = reference.parseInnerField(columnField);
        freeze.merge(innerField);
        return freeze;
    }

    // columnField = inner + outer
    // outerColumnField = outer only
    private List<RecursiveMinoField> calculate(ColumnField columnField, ColumnField outerColumnField, Field wallField) {
        ColumnFieldConnections connections = reference.getConnections(columnField);

        // まだ探索したことのないフィールドのとき
        // innerに対しておける可能性がある手順を取得
        return connections.getConnections().parallelStream()
                .flatMap(connection -> {
                    // outerで重なりがないか確認する
                    ColumnField nextOuterField = connection.getOuterField();
                    if (nextOuterField.canMerge(outerColumnField)) {
                        CalculatorCore core = coreThreadLocal.get();
                        ColumnField freeze = nextOuterField.freeze(sizedBit.getHeight());

                        // フィールドとミノ順を進める
                        SeparableMino currentMino = connection.getMino();
                        freeze.merge(outerColumnField);

                        // 新しいフィールドを基に探索
                        ColumnField innerField = connection.getInnerField();
                        return core.calculate(innerField, freeze, wallField, currentMino);
                    }
                    return Stream.empty();
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static class CalculatorCoreThreadLocal extends ThreadLocal<CalculatorCore> {
        private final SizedBit sizedBit;
        private final BasicReference reference;
        private final SeparableMinos separableMinos;
        private final HashMap<ColumnField, List<RecursiveMinoField>> resultsMap;

        public CalculatorCoreThreadLocal(SizedBit sizedBit, BasicReference reference, SeparableMinos separableMinos, HashMap<ColumnField, List<RecursiveMinoField>> resultsMap) {
            this.sizedBit = sizedBit;
            this.reference = reference;
            this.separableMinos = separableMinos;
            this.resultsMap = resultsMap;
        }

        @Override
        protected CalculatorCore initialValue() {
            return new CalculatorCore(sizedBit, reference, separableMinos, resultsMap);
        }
    }

    private static class CalculatorCore {
        private final SizedBit sizedBit;
        private final BasicReference reference;
        private final SeparableMinos separableMinos;
        private final HashMap<ColumnField, List<RecursiveMinoField>> resultsMap;

        public CalculatorCore(SizedBit sizedBit, BasicReference reference, SeparableMinos separableMinos, HashMap<ColumnField, List<RecursiveMinoField>> resultsMap) {
            this.sizedBit = sizedBit;
            this.reference = reference;
            this.separableMinos = separableMinos;
            this.resultsMap = resultsMap;
        }

        // columnField = inner + outer
        // outerColumnField = outer only
        private Stream<RecursiveMinoField> calculate(ColumnField columnField, ColumnField outerColumnField, Field wallField, SeparableMino currentMino) {
            ColumnFieldConnections connections = reference.getConnections(columnField);

            // 最初の関数呼び出しで通ることはない
            // 全てが埋まったとき、それまでの手順を解とする
            if (ColumnFieldConnections.isFilled(connections)) {
                // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
                Field freeze = wallField.freeze(sizedBit.getHeight());
                Field invertedOuterField = reference.parseInvertedOuterField(outerColumnField);
                freeze.merge(invertedOuterField);

                // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
                RecursiveMinoField result = new RecursiveMinoField(currentMino, outerColumnField.freeze(sizedBit.getHeight()), separableMinos);
                return Stream.of(result);
            }

            // 最初の関数呼び出しで通ることはない
            // すでに探索済みのフィールドなら、その情報を利用する
            List<RecursiveMinoField> minoFieldSet = resultsMap.getOrDefault(columnField, null);
            if (minoFieldSet != null) {
                Stream.Builder<RecursiveMinoField> builder = Stream.builder();

                int index = separableMinos.toIndex(currentMino);

                for (RecursiveMinoField minoField : minoFieldSet) {
                    if (index < minoField.getMaxIndex())
                        continue;

                    // outerで、最終的に使用されるブロック と すでに使っているブロックが重ならないことを確認
                    ColumnField lastOuterField = minoField.getOuterField();
                    if (lastOuterField.canMerge(outerColumnField)) {
                        OperationWithKey currentOperations = currentMino.toOperation();
                        long currentDeleteKey = currentOperations.getNeedDeletedKey();
                        long currentUsingKey = currentOperations.getUsingKey();

                        // いま置こうとしているミノと、それまでの結果に矛盾があるか確認
                        boolean isContradiction = currentDeleteKey != 0L && minoField.getOperationsStream()
                                .anyMatch(operationWithKey -> {
                                    long deletedKey = operationWithKey.getNeedDeletedKey();
                                    long usingKey = operationWithKey.getUsingKey();
                                    return (currentUsingKey & deletedKey) != 0L && (usingKey & currentDeleteKey) != 0L;
                                });

                        // 矛盾があるときはスキップ
                        if (isContradiction)
                            continue;

                        // 使用されるブロックを算出
                        ColumnField usingBlock = lastOuterField.freeze(sizedBit.getHeight());
                        usingBlock.merge(outerColumnField);

                        // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
                        Field freeze = wallField.freeze(sizedBit.getHeight());
                        Field invertedOuterField = reference.parseInvertedOuterField(usingBlock);
                        freeze.merge(invertedOuterField);

                        // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
                        RecursiveMinoField result = new RecursiveMinoField(currentMino, minoField, usingBlock, separableMinos);
                        builder.accept(result);
                    }
                }

                return builder.build();
            }

            return Stream.empty();
        }
    }
}
