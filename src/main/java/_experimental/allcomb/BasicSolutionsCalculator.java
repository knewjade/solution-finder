package _experimental.allcomb;


import common.buildup.BuildUp;
import common.datastore.IOperationWithKey;
import common.datastore.OperationWithKey;
import core.field.Field;
import core.field.SmallField;

import java.util.*;

/**
 * マルチスレッド非対応
 */
class BasicSolutionsCalculator {
    private static final int WIDTH = 3;
    public static final int WALL_START_X = 6;
    public static final int FIELD_WIDTH = 10;

    private final int height;
    private final Bit bit;
    private final BasicReference reference;
    private final GrandOnlyReachable grandOnlyReachable = new GrandOnlyReachable();

    private HashMap<ColumnField, Set<MinoField>> resultsMap = new HashMap<>();
    private LinkedList<SeparableMino> minos = new LinkedList<>();
    private HashSet<MinoField> results = new HashSet<>();
    private SmallField wallField = new SmallField();

    BasicSolutionsCalculator(List<SeparableMino> minos, int height) {
        assert height <= 10;
        this.height = height;
        this.bit = new Bit(WIDTH, height);
        this.reference = new BasicReference(bit, minos);
    }

    BasicSolutions calculate() {
        List<ColumnSmallField> basicFields = reference.getBasicFields();

        this.resultsMap = new HashMap<>();
        for (ColumnField columnField : basicFields) {
            HashSet<MinoField> results = calculate(columnField);
            resultsMap.put(columnField, results);
        }

        return new BasicSolutions(resultsMap);
    }

    private HashSet<MinoField> calculate(ColumnField columnField) {
        // 初期化
        this.minos = new LinkedList<>();
        this.results = new HashSet<>();
        this.wallField = createWallField(columnField);

        ColumnSmallField initOuterField = new ColumnSmallField();
        calculateResult(columnField, initOuterField);
        return results;
    }

    // innerと探索に関係ないブロックが埋まっているフィールド
    private SmallField createWallField(ColumnField columnField) {
        SmallField wallField = new SmallField();
        for (int y = 0; y < height; y++)
            for (int x = WALL_START_X; x < FIELD_WIDTH; x++)
                wallField.setBlock(x, y);
        Field innerField = reference.parseInnerField(columnField);
        wallField.merge(innerField);
        return wallField;
    }

    // columnField = inner + outer
    // outerColumnField = outer only
    private void calculateResult(ColumnField columnField, ColumnField outerColumnField) {
        ColumnFieldConnections connections = reference.getConnections(columnField);

        // 全てが埋まったとき、それまでの手順を解とする
        if (ColumnFieldConnections.isFilled(connections)) {
            // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
            Field freeze = wallField.freeze(height);
            Field invertedOuterField = reference.parseInvertedOuterField(outerColumnField);
            freeze.merge(invertedOuterField);

            List<IOperationWithKey> operations = toOperationWithKeys(minos);

            // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
            if (existsValidBuildPattern(freeze, operations))
                recordResult(operations, outerColumnField.freeze(height));

            return;
        }

        // すでに探索済みのフィールドなら、その情報を利用する
        Set<MinoField> minoFieldSet = resultsMap.getOrDefault(columnField, null);
        if (minoFieldSet != null) {
            for (MinoField minoField : minoFieldSet) {
                // outerで、最終的に使用されるブロック と すでに使っているブロックが重ならないことを確認
                ColumnField lastOuterField = minoField.getOuterField();
                if (lastOuterField.canMerge(outerColumnField)) {
                    List<IOperationWithKey> operations = toOperationWithKeys(this.minos);
                    operations.addAll(minoField.getOperations());

                    // 使用されるブロックを算出
                    ColumnField usingBlock = lastOuterField.freeze(height);
                    usingBlock.merge(outerColumnField);

                    // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
                    Field freeze = wallField.freeze(height);
                    Field invertedOuterField = reference.parseInvertedOuterField(usingBlock);
                    freeze.merge(invertedOuterField);

                    // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
                    if (existsValidBuildPattern(freeze, operations))
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
                minos.addLast(connection.getMino());
                nextOuterField.merge(outerColumnField);

                // 新しいフィールドを基に探索
                ColumnField innerField = connection.getInnerField();
                calculateResult(innerField, nextOuterField);

                // フィールドとミノ順を戻す
                nextOuterField.reduce(outerColumnField);
                minos.pollLast();
            }
        }
    }

    private List<IOperationWithKey> toOperationWithKeys(List<SeparableMino> minos) {
        ArrayList<IOperationWithKey> operations = new ArrayList<>();
        for (SeparableMino mino : minos) {
            IOperationWithKey key = new OperationWithKey(mino.getMino(), mino.getX(), mino.getDeleteKey(), mino.getUsingKey(), mino.getLowerY());
            operations.add(key);
        }
        return operations;
    }

    // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
    // ただし、フィールドをブロックで埋めると回転入れなどができない場合があるため、判定は下に地面があるかだけを判定
    // (部分的には回転入れできなくても、左右のSolutionパターン次第では入れられる可能性がある)
    private boolean existsValidBuildPattern(Field freeze, List<IOperationWithKey> operations) {
        return BuildUp.existsValidBuildPattern(freeze, operations, height, grandOnlyReachable);
    }

    private void recordResult(List<IOperationWithKey> operations, ColumnField outerField) {
        MinoField result = new MinoField(operations, outerField, height);
        results.add(result);
    }
}
