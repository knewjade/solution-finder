package searcher.pack.calculator;

import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import core.field.Field;
import searcher.pack.SeparableMinos;
import searcher.pack.connections.ColumnFieldConnection;
import searcher.pack.connections.ColumnFieldConnections;
import searcher.pack.mino_field.RecursiveMinoField;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class ConnectionsToStreamCallable implements Callable<Stream<RecursiveMinoField>> {
    private final SolutionsCalculator calculator;
    private final ColumnField initColumnField;
    private final ColumnField outerColumnField;
    private final Field wallField;
    private final ColumnField limitOuterField;

    public ConnectionsToStreamCallable(SolutionsCalculator calculator, ColumnField initColumnField, ColumnField outerColumnField, Field wallField, ColumnField limitOuterField) {
        this.calculator = calculator;
        this.initColumnField = initColumnField;
        this.outerColumnField = outerColumnField;
        this.wallField = wallField;
        this.limitOuterField = limitOuterField;
    }

    @Override
    public Stream<RecursiveMinoField> call() throws Exception {
        ColumnFieldConnections connections = calculator.getConnections(this.initColumnField);
        return connections.getConnectionStream().parallel()
                .flatMap(this::parseConnectionToMinoField);
    }

    private Stream<? extends RecursiveMinoField> parseConnectionToMinoField(ColumnFieldConnection connection) {
        // outerで重なりがないか確認する
        ColumnField nextOuterField = connection.getOuterField();
        if (nextOuterField.canMerge(limitOuterField) && nextOuterField.canMerge(outerColumnField)) {
            ColumnField freeze = nextOuterField.freeze(calculator.getHeight());

            // フィールドとミノ順を進める
            SeparableMino currentMino = connection.getMino();
            freeze.merge(outerColumnField);

            // 新しいフィールドを基に探索
            ColumnField innerField = connection.getInnerField();
            return calculate(innerField, freeze, wallField, currentMino);
        }
        return Stream.empty();
    }

    // columnField = inner + outer
    // outerColumnField = outer only
    private Stream<RecursiveMinoField> calculate(ColumnField columnField, ColumnField outerColumnField, Field wallField, SeparableMino currentMino) {
        // 最初の関数呼び出しで通ることはない
        // 全てが埋まったとき、それまでの手順を解とする
        if (calculator.isFilled(columnField))
            return parseWhenFilled(outerColumnField, wallField, currentMino);

        // 最初の関数呼び出しで通ることはない
        // すでに探索済みのフィールドなら、その情報を利用する
        RecursiveMinoFields minoFields = calculator.getRecursiveMinoFields(columnField);
        if (minoFields == null)
            return Stream.empty();

        return parseWhenNext(outerColumnField, wallField, currentMino, minoFields);
    }

    private Stream<RecursiveMinoField> parseWhenFilled(ColumnField outerColumnField, Field wallField, SeparableMino currentMino) {
        // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
        Field freeze = wallField.freeze(calculator.getHeight());
        Field invertedOuterField = calculator.parseInvertedOuterField(outerColumnField);
        freeze.merge(invertedOuterField);

        // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
        SeparableMinos separableMinos = calculator.getSeparableMinos();
        RecursiveMinoField result = new RecursiveMinoField(currentMino, outerColumnField.freeze(calculator.getHeight()), separableMinos);
        return Stream.of(result);
    }

    private Stream<RecursiveMinoField> parseWhenNext(ColumnField outerColumnField, Field wallField, SeparableMino currentMino, RecursiveMinoFields minoFields) {
        SeparableMinos separableMinos = calculator.getSeparableMinos();
        int index = separableMinos.toIndex(currentMino);

        return minoFields.recursiveStream()
                .filter(minoField -> minoField.getMaxIndex() <= index)
                .map(minoField -> {
                    // outerで、最終的に使用されるブロック と すでに使っているブロックが重ならないことを確認
                    ColumnField lastOuterField = minoField.getOuterField();
                    if (!lastOuterField.canMerge(outerColumnField))
                        return null;

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
                        return null;

                    // 使用されるブロックを算出
                    ColumnField usingBlock = lastOuterField.freeze(calculator.getHeight());
                    usingBlock.merge(outerColumnField);

                    // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
                    Field freeze = wallField.freeze(calculator.getHeight());
                    Field invertedOuterField = calculator.parseInvertedOuterField(usingBlock);
                    freeze.merge(invertedOuterField);

                    return new RecursiveMinoField(currentMino, minoField, usingBlock, separableMinos);
                })
                .filter(Objects::nonNull);
    }
}
