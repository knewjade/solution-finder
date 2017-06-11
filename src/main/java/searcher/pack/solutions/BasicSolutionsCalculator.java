package searcher.pack.solutions;

import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.SmallField;
import searcher.pack.*;
import searcher.pack.separable_mino.SeparableMino;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * マルチスレッド非対応
 */
public class BasicSolutionsCalculator {
    public static final int FIELD_WIDTH = 10;
    public static final int WIDTH_OVER_MINO = 3;

    private final SizedBit sizedBit;
    private final BasicReference reference;
    private final ThreadLocal<CalculatorCore> coreThreadLocal;

    private HashMap<ColumnField, Set<IMinoField>> resultsMap = new HashMap<>();

    public BasicSolutionsCalculator(SeparableMinos separableMinos, SizedBit sizedBit) {
        assert sizedBit.getHeight() <= 10;
        this.sizedBit = sizedBit;
        this.reference = new BasicReference(sizedBit, separableMinos);
        this.coreThreadLocal = new CalculatorCoreThreadLocal(sizedBit, reference, separableMinos, resultsMap);
    }

    public Map<ColumnField, Set<IMinoField>> calculate() {
        List<ColumnSmallField> sortedBasicFields = reference.getSortedBasicFields();
        return calculateResults(sortedBasicFields);
    }

    private HashMap<ColumnField, Set<IMinoField>> calculateResults(List<ColumnSmallField> basicFields) {
        assert resultsMap.isEmpty();

        Map<Integer, List<ColumnSmallField>> fieldEachBlocks = basicFields.stream()
                .collect(Collectors.groupingBy(columnField -> Long.bitCount(columnField.getBoard(0))));

        List<Integer> keys = fieldEachBlocks.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        for (Integer key : keys) {
            System.out.println(key);
            List<ColumnSmallField> fields = fieldEachBlocks.get(key);
            Map<ColumnSmallField, HashSet<IMinoField>> map = fields.parallelStream()
                    .collect(Collectors.toMap(Function.identity(), this::calculate));
            resultsMap.putAll(map);
        }

        return this.resultsMap;
    }

    private HashSet<IMinoField> calculate(ColumnField columnField) {
        CalculatorCore calculatorCore = coreThreadLocal.get();
        return calculatorCore.calculate(columnField);
    }

    private static class CalculatorCoreThreadLocal extends ThreadLocal<CalculatorCore> {
        private final SizedBit sizedBit;
        private final BasicReference reference;
        private final SeparableMinos separableMinos;
        private final HashMap<ColumnField, Set<IMinoField>> resultsMap;

        public CalculatorCoreThreadLocal(SizedBit sizedBit, BasicReference reference, SeparableMinos separableMinos, HashMap<ColumnField, Set<IMinoField>> resultsMap) {
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
        private final HashMap<ColumnField, Set<IMinoField>> resultsMap;

        private SeparableMino currentMino = null;
        private HashSet<IMinoField> results = new HashSet<>();
        private SmallField wallField = new SmallField();

        public CalculatorCore(SizedBit sizedBit, BasicReference reference, SeparableMinos separableMinos, HashMap<ColumnField, Set<IMinoField>> resultsMap) {
            this.sizedBit = sizedBit;
            this.reference = reference;
            this.separableMinos = separableMinos;
            this.resultsMap = resultsMap;
        }

        private HashSet<IMinoField> calculate(ColumnField columnField) {
            // 初期化
            this.currentMino = null;
            this.results = new HashSet<>();
            this.wallField = createWallField(columnField);

            ColumnSmallField initOuterField = new ColumnSmallField();
            calculateResult(columnField, initOuterField);
            return results;
        }

        // innerと探索に関係ないブロックが埋まっているフィールド
        private SmallField createWallField(ColumnField columnField) {
            Field innerField = reference.parseInnerField(columnField);

            SmallField wallField = new SmallField();
            // 横向きIをおいたとき、3ブロック分あふれる
            for (int y = 0; y < sizedBit.getHeight(); y++)
                for (int x = sizedBit.getWidth() + WIDTH_OVER_MINO; x < FIELD_WIDTH; x++)
                    wallField.setBlock(x, y);
            wallField.merge(innerField);

            return wallField;
        }

        // columnField = inner + outer
        // outerColumnField = outer only
        private void calculateResult(ColumnField columnField, ColumnField outerColumnField) {
            ColumnFieldConnections connections = reference.getConnections(columnField);

            // 最初の関数呼び出しで通ることはない
            // 全てが埋まったとき、それまでの手順を解とする
            if (ColumnFieldConnections.isFilled(connections)) {
                // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
                Field freeze = wallField.freeze(sizedBit.getHeight());
                Field invertedOuterField = reference.parseInvertedOuterField(outerColumnField);
                freeze.merge(invertedOuterField);

                List<OperationWithKey> operations = Collections.singletonList(currentMino.toOperation());

                // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
                recordResult(operations, outerColumnField.freeze(sizedBit.getHeight()));

                return;
            }

            // 最初の関数呼び出しで通ることはない
            // すでに探索済みのフィールドなら、その情報を利用する
            Set<IMinoField> minoFieldSet = resultsMap.getOrDefault(columnField, null);
            if (minoFieldSet != null) {
                int index = separableMinos.toIndex(currentMino);

                for (IMinoField minoField : minoFieldSet) {
                    if (index < minoField.getMaxIndex())
                        continue;

                    // outerで、最終的に使用されるブロック と すでに使っているブロックが重ならないことを確認
                    ColumnField lastOuterField = minoField.getOuterField();
                    if (lastOuterField.canMerge(outerColumnField)) {
                        OperationWithKey currentOperations = currentMino.toOperation();
                        long currentDeleteKey = currentOperations.getNeedDeletedKey();
                        long currentUsingKey = currentOperations.getUsingKey();

                        // いま置こうとしているミノと、それまでの結果に矛盾があるか確認
                        List<OperationWithKey> minoFieldOperations = minoField.getOperations();
                        boolean isContradiction = currentDeleteKey != 0L && minoFieldOperations.stream()
                                .anyMatch(operationWithKey -> {
                                    long deletedKey = operationWithKey.getNeedDeletedKey();
                                    long usingKey = operationWithKey.getUsingKey();
                                    return (currentUsingKey & deletedKey) != 0L && (usingKey & currentDeleteKey) != 0L;
                                });

                        // 矛盾があるときはスキップ
                        if (isContradiction)
                            continue;

                        ArrayList<OperationWithKey> operations = new ArrayList<>();
                        operations.add(currentOperations);
                        operations.addAll(minoFieldOperations);

                        // 使用されるブロックを算出
                        ColumnField usingBlock = lastOuterField.freeze(sizedBit.getHeight());
                        usingBlock.merge(outerColumnField);

                        // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
                        Field freeze = wallField.freeze(sizedBit.getHeight());
                        Field invertedOuterField = reference.parseInvertedOuterField(usingBlock);
                        freeze.merge(invertedOuterField);

                        // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
                        recordResult(operations, usingBlock);
                    }
                }

                return;
            }

            // まだ探索したことのないフィールドのとき
            // innerに対しておける可能性がある手順を取得
            for (ColumnFieldConnection connection : connections.getConnections()) {
                // outerで重なりがないか確認する
                ColumnField nextOuterField = connection.getOuterField();
                if (nextOuterField.canMerge(outerColumnField)) {
                    // フィールドとミノ順を進める
                    currentMino = connection.getMino();
                    nextOuterField.merge(outerColumnField);

                    // 新しいフィールドを基に探索
                    ColumnField innerField = connection.getInnerField();
                    calculateResult(innerField, nextOuterField);

                    // フィールドとミノ順を戻す
                    nextOuterField.reduce(outerColumnField);
                }
            }
        }

        private void recordResult(List<OperationWithKey> operations, ColumnField outerField) {
            IMinoField result = new MinoField(operations, outerField, sizedBit.getHeight(), separableMinos);
            results.add(result);
        }
    }
}
